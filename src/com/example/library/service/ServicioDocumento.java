package com.example.library.service;

import java.util.ArrayList;
import java.util.UUID;

import com.example.library.domain.Documento;
import com.example.library.domain.TipoDocumento;
import com.example.library.domain.ValidadorDocumento;
import com.example.library.repository.RepositorioDocumentoBinario;
import com.example.library.repository.RepositorioExcepcion;

public class ServicioDocumento {
  private RepositorioDocumentoBinario _repositorio;

  public ServicioDocumento() {
    this(new RepositorioDocumentoBinario());
  }

  public ServicioDocumento(
    RepositorioDocumentoBinario repositorio
  ) {
    if (repositorio == null) {
      throw ServicioDocumentoExcepcion.repositorioObligatorio();
    }

    this._repositorio = repositorio;
  }

  public ArrayList<Documento> listar() {
    try {
      return this._repositorio.leerTodos();
    } catch (RepositorioExcepcion e) {
      throw ServicioDocumentoExcepcion.errorAlConsultar(e);
    }
  }

  public Documento crear(
    TipoDocumento tipo,
    String titulo,
    String descripcion,
    String genero,
    Integer valoracion,
    Integer paginas
  ) {
    Documento documento = Documento.nuevo(
      tipo,
      titulo,
      descripcion, 
      genero, 
      valoracion, 
      paginas
    );

    try {
      this._repositorio.crear(documento);
      return documento;
    } catch (RepositorioExcepcion e) {
      throw ServicioDocumentoExcepcion.errorAlCrear(e);
    }
  }

  public Documento actualizar(
    UUID id,
    TipoDocumento tipo,
    String titulo,
    String descripcion,
    String genero,
    Integer valoracion,
    Integer paginas
  ) {
    Documento documento = new Documento(
      id,
      tipo,
      titulo,
      descripcion,
      genero,
      valoracion,
      paginas
    );

    try {
      this._repositorio.actualizar(documento);
      return documento;
    } catch (RepositorioExcepcion e) {
      throw ServicioDocumentoExcepcion.errorAlActualizar(e);
    }
  }

  public void eliminar(UUID id) {
    ValidadorDocumento.validarId(id);

    try {
      this._repositorio.eliminar(id);
    } catch (RepositorioExcepcion e) {
      throw ServicioDocumentoExcepcion.errorAlEliminar(e);
    }
  }
}
