package com.example.library.repository;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.example.library.domain.Documento;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class RepositorioDocumentoBinario {
  private final Path ruta;

  public RepositorioDocumentoBinario() {
    this(Path.of("src", "com", "example", "library", "data", "catalog", "libros.dat"));
  }

  public RepositorioDocumentoBinario(Path ruta) {
    this.ruta = ruta.normalize();
  }

  public ArrayList<Documento> leerTodos() {
    return new ArrayList<>(leerCatalogo().values());
  }

  public Documento buscarPorId(UUID id) {
    return exigirDocumento(leerCatalogo().get(id), id);
  }

  public void crear(Documento documento) {
    Map<UUID, Documento> catalogo = Files.notExists(ruta) ? new LinkedHashMap<>() : leerCatalogo();
    if (catalogo.putIfAbsent(documento.getId(), documento) != null) {
      throw new IllegalStateException("Ya existe un documento con id " + documento.getId() + ".");
    }
    guardar(catalogo.values());
  }

  public void actualizar(Documento documento) {
    Map<UUID, Documento> catalogo = leerCatalogo();
    UUID id = documento.getId();
    exigirDocumento(catalogo.replace(id, documento), id);
    guardar(catalogo.values());
  }

  public void eliminar(UUID id) {
    Map<UUID, Documento> catalogo = leerCatalogo();
    exigirDocumento(catalogo.remove(id), id);
    guardar(catalogo.values());
  }

  private Documento exigirDocumento(Documento documento, UUID id) {
    if (documento == null) {
      throw new IllegalStateException("No existe un documento con id " + id + ".");
    }
    return documento;
  }

  private Map<UUID, Documento> leerCatalogo() {
    try (var archivo = Files.newInputStream(ruta);
        var entrada = new ObjectInputStream(archivo)) {
      Object contenido = entrada.readObject();
      if (contenido == null || contenido.getClass() != ArrayList.class) {
        throw new IOException("Se esperaba una lista de documentos.");
      }
      Map<UUID, Documento> catalogo = new LinkedHashMap<>();
      for (Object elemento : (ArrayList<?>) contenido) {
        if (elemento == null || elemento.getClass() != Documento.class) {
          throw new IOException("La lista contiene un elemento inválido.");
        }
        Documento documento = ((Documento) elemento).copiaValidada();
        if (catalogo.putIfAbsent(documento.getId(), documento) != null) {
          throw new IOException("El catálogo contiene un id duplicado.");
        }
      }
      return catalogo;
    } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
      throw new IllegalStateException(
          "No se pudo leer el catálogo. Puede estar corrupto o ser inaccesible.", e);
    }
  }

  private void guardar(Collection<Documento> documentos) {
    Path directorio = ruta.toAbsolutePath().getParent();
    try {
      Files.createDirectories(directorio);
      Path temporal = Files.createTempFile(directorio, "catalogo-", ".tmp");
      try {
        try (var archivo = new FileOutputStream(temporal.toFile());
            var salida = new ObjectOutputStream(archivo)) {
          salida.writeObject(new ArrayList<>(documentos));
          salida.flush();
          archivo.getFD().sync();
        }
        reemplazarCatalogo(temporal);
      } finally {
        limpiarTemporal(temporal);
      }
    } catch (IOException e) {
      throw new IllegalStateException("No se pudo guardar el catálogo.", e);
    }
  }

  private void reemplazarCatalogo(Path temporal) throws IOException {
    try {
      Files.move(temporal, ruta, ATOMIC_MOVE, REPLACE_EXISTING);
      return;
    } catch (AtomicMoveNotSupportedException e) {
    }
    if (Files.notExists(ruta)) {
      Files.move(temporal, ruta);
      return;
    }
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
      if (!restaurando) {
        limpiarTemporal(respaldo);
      }
    }
  }

  private void limpiarTemporal(Path archivo) {
    try {
      Files.deleteIfExists(archivo);
    } catch (IOException ignored) {
    }
  }
}
