package com.example.library.service;

import com.example.library.domain.Documento;
import com.example.library.domain.TipoDocumento;
import com.example.library.domain.ValidadorDocumento;
import com.example.library.repository.RepositorioDocumentoBinario;
import java.util.ArrayList;
import java.util.UUID;

public class ServicioDocumento {
  private final RepositorioDocumentoBinario _repositorio;

  public ServicioDocumento() {
    this(new RepositorioDocumentoBinario());
  }

  public ServicioDocumento(RepositorioDocumentoBinario repositorio) {
    this._repositorio = repositorio;
  }

  public ArrayList<Documento> listar() {
    return _repositorio.leerTodos();
  }

  public Documento consultar(UUID id) {
    ValidadorDocumento.validarId(id);
    return _repositorio.buscarPorId(id);
  }

  public Documento crear(
      TipoDocumento tipo,
      String titulo,
      String descripcion,
      String genero,
      Integer valoracion,
      Integer paginas) {
    Documento documento = Documento.nuevo(tipo, titulo, descripcion, genero, valoracion, paginas);

    _repositorio.crear(documento);
    return documento;
  }

  public Documento actualizar(
      UUID id,
      TipoDocumento tipo,
      String titulo,
      String descripcion,
      String genero,
      Integer valoracion,
      Integer paginas) {
    Documento documento = new Documento(id, tipo, titulo, descripcion, genero, valoracion, paginas);

    _repositorio.actualizar(documento);
    return documento;
  }

  public void eliminar(UUID id) {
    ValidadorDocumento.validarId(id);

    _repositorio.eliminar(id);
  }
}
