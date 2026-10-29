package com.example.library.repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    try (
      ObjectInputStream entrada = new ObjectInputStream(
        Files.newInputStream(this._ruta, StandardOpenOption.READ)
      );
    ) {
      Object objetoLeido = entrada.readObject();
      return this._validarDocumentosLeidos(objetoLeido);
    } catch (IOException | ClassNotFoundException e) {
      throw RepositorioExcepcion.catalogoInvalido(this._ruta, e);
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

    ArrayList<Documento> documentos;

    if (Files.notExists(this._ruta)) {
      documentos = new ArrayList<>();
    } else {
      documentos = this.leerTodos();
    }

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
    if (!(objetoLeido instanceof ArrayList<?>)) {
      throw new RepositorioExcepcion(
        "El catálogo no contiene una lista de documentos válida."
      );
    }

    ArrayList<?> elementos = (ArrayList<?>) objetoLeido;
    ArrayList<Documento> documentos = new ArrayList<>();
    HashSet<UUID> ids = new HashSet<>();

    for (Object elemento : elementos) {
      if (
        elemento == null
        || elemento.getClass() != Documento.class
      ) {
        throw new RepositorioExcepcion(
          "El catálogo contiene un elemento que no es un Documento."
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
        throw new RepositorioExcepcion(
          "El catálogo contiene un documento inválido",
          e
        );
      }
    
      if (!ids.add(documentoValidado.getId())) {
        throw new RepositorioExcepcion(
          "El catálogo contiene ids duplicados."
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
        ObjectOutputStream salida = new ObjectOutputStream(
          Files.newOutputStream(
            temporal,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING
          )
        )
      ) {
        salida.writeObject(documentos);
        salida.flush();
      }

      try {
        Files.move(
          temporal,
          this._ruta,
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING
        );
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(
          temporal,
          this._ruta,
          StandardCopyOption.REPLACE_EXISTING
        );
      }
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
