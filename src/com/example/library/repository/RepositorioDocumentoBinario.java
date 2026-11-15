package com.example.library.repository;

import static java.nio.file.StandardCopyOption.*;

import com.example.library.domain.Documento;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public record RepositorioDocumentoBinario(Path ruta) {
  public RepositorioDocumentoBinario {
    ruta = ruta.normalize();
  }

  public RepositorioDocumentoBinario() {
    this(Path.of("src/com/example/library/data/catalog/libros.dat"));
  }

  public Documento buscarPorId(UUID id) {
    return exigirDocumento(leerCatalogo().get(id), id);
  }

  public void guardarDocumento(Documento documento, boolean nuevo) {
    var catalogo =
        nuevo && Files.notExists(ruta) ? new LinkedHashMap<UUID, Documento>() : leerCatalogo();
    UUID id = documento._id();

    if (nuevo && catalogo.containsKey(id))
      throw new IllegalStateException("Ya existe un documento con id " + id + ".");

    if (!nuevo) exigirDocumento(catalogo.get(id), id);

    catalogo.put(id, documento);
    guardar(catalogo.values());
  }

  public void eliminar(UUID id) {
    Map<UUID, Documento> catalogo = leerCatalogo();
    exigirDocumento(catalogo.remove(id), id);
    guardar(catalogo.values());
  }

  private Documento exigirDocumento(Documento documento, UUID id) {
    if (documento == null)
      throw new IllegalStateException("No existe un documento con id " + id + ".");

    return documento;
  }

  public Map<UUID, Documento> leerCatalogo() {
    try (var archivo = Files.newInputStream(ruta);
        var entrada = new ObjectInputStream(archivo)) {
      Object contenido = entrada.readObject();

      if (contenido == null || contenido.getClass() != ArrayList.class)
        throw new IOException("Se esperaba una lista de documentos.");
      var catalogo = new LinkedHashMap<UUID, Documento>();

      for (Object elemento : (ArrayList<?>) contenido) {
        if (!(elemento instanceof Documento doc))
          throw new IOException("La lista contiene un elemento inválido.");

        if (catalogo.putIfAbsent(doc._id(), doc) != null)
          throw new IOException("El catálogo contiene un id duplicado.");
      }

      return catalogo;
    } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
      throw new IllegalStateException(
          "No se pudo leer el catálogo. Puede estar corrupto o ser inaccesible.", e);
    }
  }

  private void guardar(Collection<Documento> documentos) {
    Path directorio = ruta.toAbsolutePath().getParent();
    Path temporal = null;

    try {
      Files.createDirectories(directorio);
      temporal = Files.createTempFile(directorio, "catalogo-", ".tmp");

      try (var archivo = new FileOutputStream(temporal.toFile());
          var salida = new ObjectOutputStream(archivo)) {
        salida.writeObject(new ArrayList<>(documentos));
        salida.flush();
        archivo.getFD().sync();
      }

      try {
        Files.move(temporal, ruta, ATOMIC_MOVE, REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException e) {
        if (Files.notExists(ruta)) Files.move(temporal, ruta);
        else reemplazarConRespaldo(temporal);
      }
    } catch (IOException e) {
      throw new IllegalStateException("No se pudo guardar el catálogo.", e);
    } finally {
      if (temporal != null) limpiarTemporal(temporal);
    }
  }

  private void reemplazarConRespaldo(Path temporal) throws IOException {
    Path respaldo = Files.createTempFile(temporal.getParent(), "catalogo-", ".bak");
    boolean restaurando = false;

    try {
      Files.copy(ruta, respaldo, REPLACE_EXISTING, COPY_ATTRIBUTES);
      try {
        Files.move(temporal, ruta, REPLACE_EXISTING);
      } catch (IOException e) {
        restaurando = true;
        Files.copy(respaldo, ruta, REPLACE_EXISTING, COPY_ATTRIBUTES);
        restaurando = false;
        throw e;
      }
    } finally {
      if (!restaurando) limpiarTemporal(respaldo);
    }
  }

  private void limpiarTemporal(Path archivo) {
    try {
      Files.deleteIfExists(archivo);
    } catch (IOException ignored) {
    }
  }
}
