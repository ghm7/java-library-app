package com.example.library.domain;

import java.io.Serializable;
import java.util.UUID;

public class Documento implements Serializable {
  private static final long serialVersionUID = 1L;

  private final UUID _id;
  private final TipoDocumento _tipo;
  private final String _titulo;
  private final String _descripcion;
  private final String _genero;
  private final Integer _valoracion;
  private final Integer _paginas;

  public Documento(
      UUID id,
      TipoDocumento tipo,
      String titulo,
      String descripcion,
      String genero,
      Integer valoracion,
      Integer paginas) {
    this._id = ValidadorDocumento.validarId(id);
    this._tipo = ValidadorDocumento.validarTipo(tipo);
    this._titulo = ValidadorDocumento.validarTitulo(titulo);
    this._descripcion = ValidadorDocumento.textoOpcional(descripcion);
    this._genero = ValidadorDocumento.textoOpcional(genero);
    this._valoracion = ValidadorDocumento.validarValoracion(valoracion);
    this._paginas = ValidadorDocumento.validarPaginas(paginas);
  }

  public static Documento nuevo(
      TipoDocumento tipo,
      String titulo,
      String descripcion,
      String genero,
      Integer valoracion,
      Integer paginas) {
    return new Documento(UUID.randomUUID(), tipo, titulo, descripcion, genero, valoracion, paginas);
  }

  public Documento actualizar(
      TipoDocumento tipo,
      String titulo,
      String descripcion,
      String genero,
      Integer valoracion,
      Integer paginas) {
    return new Documento(this._id, tipo, titulo, descripcion, genero, valoracion, paginas);
  }

  public Documento copiaValidada() {
    return actualizar(_tipo, _titulo, _descripcion, _genero, _valoracion, _paginas);
  }

  public UUID getId() {
    return this._id;
  }

  public TipoDocumento getTipo() {
    return this._tipo;
  }

  public String getTitulo() {
    return this._titulo;
  }

  public String getDescripcion() {
    return this._descripcion;
  }

  public String getGenero() {
    return this._genero;
  }

  public Integer getValoracion() {
    return this._valoracion;
  }

  public Integer getPaginas() {
    return this._paginas;
  }
}
