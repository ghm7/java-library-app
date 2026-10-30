package com.example.library.repository;

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
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import com.example.library.domain.Documento;

public class RepositorioDocumentoBinario {
  private final Path _ruta;
  
  public RepositorioDocumentoBinario() {
    this(Path.of(
      "src",
      "com",
      "example",
      "library",
      "data",
      "catalog",
      "libros.dat"
    ));
  }

  public RepositorioDocumentoBinario(Path ruta) {
    if (ruta == null) {
      throw RepositorioExcepcion.rutaObligatoria();
    }

    this._ruta = ruta.normalize();
  }

  public ArrayList<Documento> leerTodos() {
    if (Files.notExists(this._ruta)) {
      throw RepositorioExcepcion.catalogoNoEncontrado(this._ruta);
    }
    
    if (!Files.isRegularFile(this._ruta)) {
      throw RepositorioExcepcion.rutaNoEsArchivo(this._ruta);
    }

    try {
      if (Files.size(this._ruta) == 0) {
        throw RepositorioExcepcion.catalogoVacio(this._ruta);
      }
    } catch (RepositorioExcepcion e) {
      throw e;
    } catch (IOException e) {
      throw RepositorioExcepcion.errorAlLeer(this._ruta, e);
    }

    try (
      ObjectInputStream entrada = new ObjectInputStream(
        Files.newInputStream(this._ruta, StandardOpenOption.READ)
      );
    ) {
      Object objetoLeido = entrada.readObject();
      return this._validarDocumentosLeidos(objetoLeido);
    } catch (InvalidClassException | ClassNotFoundException e) {
      throw RepositorioExcepcion.catalogoIncompatible(this._ruta, e);
    } catch (EOFException | ObjectStreamException e) {
      throw RepositorioExcepcion.catalogoCorrupto(this._ruta, e);
    } catch (RepositorioExcepcion e) {
      throw e;
    } catch (IOException e) {
      throw RepositorioExcepcion.errorAlLeer(this._ruta, e);
    } catch (RuntimeException e) {
      throw RepositorioExcepcion.catalogoCorrupto(this._ruta, e);
    }
  }

  public Documento buscarPorId(UUID id) {
    this._validarId(id);

    for (Documento documento : this.leerTodos()) {
      if (documento.getId().equals(id)) {
        return documento;
      }
    }

    throw RepositorioExcepcion.documentoNoEncontrado(id);
  }

  public void crear(Documento documento) {
    this._validarDocumento(documento);

    ArrayList<Documento> documentos = this.leerTodos();

    for (Documento existente : documentos) {
      if (existente.getId().equals(documento.getId())) {
        throw RepositorioExcepcion.documentoDuplicado(documento.getId());
      }
    }

    documentos.add(documento);
    this._guardar(documentos);
  }

  public void actualizar(Documento documento) {
    this._validarDocumento(documento);

    ArrayList<Documento> documentos = this.leerTodos();
    boolean estaActualizado = false; 

    for (int idx = 0; idx < documentos.size(); idx++) {
      if (documentos.get(idx).getId().equals(documento.getId())) {
        documentos.set(idx, documento);
        estaActualizado = true;
        break;
      }
    }

    if (!estaActualizado) {
      throw RepositorioExcepcion.documentoNoEncontrado(documento.getId());
    }
  
    this._guardar(documentos);
  }

  public void eliminar(UUID id) {
    this._validarId(id);

    ArrayList<Documento> documentos = this.leerTodos();
    boolean estaEliminado = false;

    for (int idx = 0; idx < documentos.size(); idx++) {
      if (documentos.get(idx).getId().equals(id)) {
        documentos.remove(idx);
        estaEliminado = true;
        break;
      }
    }

    if (!estaEliminado) {
      throw RepositorioExcepcion.documentoNoEncontrado(id);
    }

    this._guardar(documentos);
  }

  private ArrayList<Documento> _validarDocumentosLeidos(
    Object objetoLeido
  ) {
    if (
      objetoLeido == null
      || objetoLeido.getClass() != ArrayList.class
    ) {
      throw RepositorioExcepcion.catalogoConFormatoInvalido(this._ruta);
    }

    ArrayList<?> elementos = (ArrayList<?>) objetoLeido;
    ArrayList<Documento> documentos = new ArrayList<>();
    HashSet<UUID> ids = new HashSet<>();

    for (int idx = 0; idx < elementos.size(); idx++) {
      Object elemento = elementos.get(idx);

      if (
        elemento == null
        || elemento.getClass() != Documento.class
      ) {
        throw RepositorioExcepcion.elementoInvalido(
          this._ruta,
          idx + 1
        );
      }

      Documento documentoOriginal = (Documento) elemento;
      Documento documentoValidado;

      try {
        documentoValidado = new Documento(
          documentoOriginal.getId(),
          documentoOriginal.getTipo(),
          documentoOriginal.getTitulo(),
          documentoOriginal.getDescripcion(),
          documentoOriginal.getGenero(),
          documentoOriginal.getValoracion(), 
          documentoOriginal.getPaginas()
        );
      } catch (RuntimeException e) {
        throw RepositorioExcepcion.documentoInvalido(
          this._ruta,
          idx + 1,
          e
        );
      }
    
      if (!ids.add(documentoValidado.getId())) {
        throw RepositorioExcepcion.idDuplicadoEnCatalogo(
          this._ruta,
          documentoValidado.getId()
        );
      }

      documentos.add(documentoValidado);
    }
  
    return documentos;
  }

  private void _guardar(ArrayList<Documento> documentos) {
    Path directorio = this._ruta.toAbsolutePath().getParent();
    Path temporal = null;

    try {
      Files.createDirectories(directorio);

      temporal = Files.createTempFile(
        directorio,
        this._ruta.getFileName().toString(),
        ".tmp"
      );

      try (
        FileOutputStream archivoTemporal = new FileOutputStream(
          temporal.toFile()
        );
        ObjectOutputStream salida = new ObjectOutputStream(
          archivoTemporal
        )
      ) {
        salida.writeObject(documentos);
        salida.flush();
        archivoTemporal.getFD().sync();
      }

      this._reemplazarCatalogo(temporal);
      temporal = null;
    } catch (IOException e) {
      throw RepositorioExcepcion.errorAlPersistir(this._ruta, e);
    } finally {
      if (temporal != null) {
        try {
          Files.deleteIfExists(temporal);
        } catch (IOException ignored) {
        }
      }
    }
  }

  private void _reemplazarCatalogo(Path temporal) throws IOException {
    try {
      Files.move(
        temporal,
        this._ruta,
        StandardCopyOption.ATOMIC_MOVE,
        StandardCopyOption.REPLACE_EXISTING
      );
      return;
    } catch (AtomicMoveNotSupportedException e) {
      this._reemplazarConRespaldo(temporal);
    }
  }

  private void _reemplazarConRespaldo(Path temporal) throws IOException {
    Path directorio = this._ruta.toAbsolutePath().getParent();
    Path respaldo = Files.createTempFile(
      directorio,
      this._ruta.getFileName().toString(),
      ".bak"
    );
    boolean restauradoOReemplazado = false;

    try {
      Files.copy(
        this._ruta,
        respaldo,
        StandardCopyOption.REPLACE_EXISTING,
        StandardCopyOption.COPY_ATTRIBUTES
      );

      try {
        Files.move(
          temporal,
          this._ruta,
          StandardCopyOption.REPLACE_EXISTING
        );
        restauradoOReemplazado = true;
      } catch (IOException errorAlReemplazar) {
        try {
          Files.copy(
            respaldo,
            this._ruta,
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.COPY_ATTRIBUTES
          );
          restauradoOReemplazado = true;
        } catch (IOException errorAlRestaurar) {
          errorAlReemplazar.addSuppressed(errorAlRestaurar);
        }

        throw errorAlReemplazar;
      }
    } finally {
      if (restauradoOReemplazado) {
        try {
          Files.deleteIfExists(respaldo);
        } catch (IOException ignored) {
        }
      }
    }
  }

  private void _validarDocumento(Documento documento) {
    if (documento == null) {
      throw RepositorioExcepcion.documentoObligatorio();
    }
  }

  private void _validarId(UUID id) {
    if (id == null) {
      throw RepositorioExcepcion.idObligatorio();
    }
  }
}
