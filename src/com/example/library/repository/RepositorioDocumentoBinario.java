package com.example.library.repository;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.example.library.domain.Documento;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
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
    if (ruta == null) {
      throw RepositorioExcepcion.rutaObligatoria();
    }
    this.ruta = ruta.normalize();
  }

  public ArrayList<Documento> leerTodos() {
    return new ArrayList<>(leerCatalogo().values());
  }

  public Documento buscarPorId(UUID id) {
    validarId(id);
    Documento encontrado = leerCatalogo().get(id);
    if (encontrado == null) {
      throw RepositorioExcepcion.documentoNoEncontrado(id);
    }
    return encontrado;
  }

  public void crear(Documento documento) {
    validarDocumento(documento);
    Map<UUID, Documento> catalogo = Files.notExists(ruta) ? new LinkedHashMap<>() : leerCatalogo();
    if (catalogo.putIfAbsent(documento.getId(), documento) != null) {
      throw RepositorioExcepcion.documentoDuplicado(documento.getId());
    }
    guardar(catalogo.values());
  }

  public void actualizar(Documento documento) {
    validarDocumento(documento);
    Map<UUID, Documento> catalogo = leerCatalogo();
    if (catalogo.replace(documento.getId(), documento) == null) {
      throw RepositorioExcepcion.documentoNoEncontrado(documento.getId());
    }
    guardar(catalogo.values());
  }

  public void eliminar(UUID id) {
    validarId(id);
    Map<UUID, Documento> catalogo = leerCatalogo();
    if (catalogo.remove(id) == null) {
      throw RepositorioExcepcion.documentoNoEncontrado(id);
    }
    guardar(catalogo.values());
  }

  private Map<UUID, Documento> leerCatalogo() {
    if (Files.notExists(ruta)) {
      throw RepositorioExcepcion.catalogoNoEncontrado(ruta);
    }
    if (!Files.isRegularFile(ruta)) {
      throw RepositorioExcepcion.rutaNoEsArchivo(ruta);
    }
    try {
      if (Files.size(ruta) == 0) {
        throw RepositorioExcepcion.catalogoVacio(ruta);
      }
      try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(ruta))) {
        return validarCatalogo(entrada.readObject());
      }
    } catch (InvalidClassException | ClassNotFoundException e) {
      throw RepositorioExcepcion.catalogoIncompatible(ruta, e);
    } catch (EOFException | ObjectStreamException e) {
      throw RepositorioExcepcion.catalogoCorrupto(ruta, e);
    } catch (IOException e) {
      throw RepositorioExcepcion.errorAlLeer(ruta, e);
    } catch (RepositorioExcepcion e) {
      throw e;
    } catch (RuntimeException e) {
      throw RepositorioExcepcion.catalogoCorrupto(ruta, e);
    }
  }

  private Map<UUID, Documento> validarCatalogo(Object contenido) {
    if (contenido == null || contenido.getClass() != ArrayList.class) {
      throw RepositorioExcepcion.catalogoConFormatoInvalido(ruta);
    }
    // El índice permite buscar por id y mantiene el orden de la lista guardada en disco.
    Map<UUID, Documento> catalogo = new LinkedHashMap<>();
    for (Object elemento : (ArrayList<?>) contenido) {
      int posicion = catalogo.size() + 1;
      if (elemento == null || elemento.getClass() != Documento.class) {
        throw RepositorioExcepcion.elementoInvalido(ruta, posicion);
      }
      Documento documento;
      try {
        documento = ((Documento) elemento).copiaValidada();
      } catch (RuntimeException e) {
        throw RepositorioExcepcion.documentoInvalido(ruta, posicion, e);
      }
      if (catalogo.putIfAbsent(documento.getId(), documento) != null) {
        throw RepositorioExcepcion.idDuplicadoEnCatalogo(ruta, documento.getId());
      }
    }
    return catalogo;
  }

  private void guardar(Collection<Documento> documentos) {
    Path directorio = ruta.toAbsolutePath().getParent();
    try {
      Files.createDirectories(directorio);
      Path temporal = Files.createTempFile(directorio, ruta.getFileName().toString(), ".tmp");
      try {
        try (FileOutputStream archivo = new FileOutputStream(temporal.toFile());
            ObjectOutputStream salida = new ObjectOutputStream(archivo)) {
          salida.writeObject(new ArrayList<>(documentos));
          salida.flush();
          archivo.getFD().sync();
        }
        reemplazarCatalogo(temporal);
      } finally {
        limpiarTemporal(temporal);
      }
    } catch (IOException e) {
      throw RepositorioExcepcion.errorAlPersistir(ruta, e);
    }
  }

  private void reemplazarCatalogo(Path temporal) throws IOException {
    try {
      Files.move(temporal, ruta, ATOMIC_MOVE, REPLACE_EXISTING);
    } catch (AtomicMoveNotSupportedException e) {
      if (Files.exists(ruta)) {
        reemplazarConRespaldo(temporal);
      } else {
        Files.move(temporal, ruta, REPLACE_EXISTING);
      }
    }
  }

  private void reemplazarConRespaldo(Path temporal) throws IOException {
    Path respaldo =
        Files.createTempFile(temporal.getParent(), ruta.getFileName().toString(), ".bak");
    boolean conservarRespaldo = false;
    try {
      Files.copy(ruta, respaldo, REPLACE_EXISTING, COPY_ATTRIBUTES);
      try {
        Files.move(temporal, ruta, REPLACE_EXISTING);
      } catch (IOException errorAlReemplazar) {
        try {
          Files.copy(respaldo, ruta, REPLACE_EXISTING, COPY_ATTRIBUTES);
        } catch (IOException errorAlRestaurar) {
          conservarRespaldo = true;
          errorAlReemplazar.addSuppressed(errorAlRestaurar);
        }
        throw errorAlReemplazar;
      }
    } finally {
      if (!conservarRespaldo) {
        limpiarTemporal(respaldo);
      }
    }
  }

  private void limpiarTemporal(Path archivo) {
    try {
      Files.deleteIfExists(archivo);
    } catch (IOException ignored) {
      // Un fallo de limpieza no cambia el resultado del guardado.
    }
  }

  private void validarDocumento(Documento documento) {
    if (documento == null) {
      throw RepositorioExcepcion.documentoObligatorio();
    }
  }

  private void validarId(UUID id) {
    if (id == null) {
      throw RepositorioExcepcion.idObligatorio();
    }
  }
}
