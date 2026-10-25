package com.example.library.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Documento {
  private UUID _id;
  private TipoDocumento _tipo;
  private String _titulo;
  private String _genero;
  private Integer _valoracion;
  private Integer _paginas;

  public Documento(
    UUID id,
    TipoDocumento tipo,
    String titulo,
    String autor,
    String genero,
    LocalDate fechaPublicacion,
    Integer valoracion,
    Integer paginas
  ) {
    this._id = ValidadorDocumento.validarId(id);
    this._tipo = ValidadorDocumento.validarTipo(tipo);
    this._titulo = ValidadorDocumento.validarTitulo(titulo);
    this._genero = ValidadorDocumento.textoOpcional(genero);
    this._valoracion = 
      ValidadorDocumento.validarValoracion(valoracion);
    this._paginas = paginas;
  }

  public static Documento nuevo(
    TipoDocumento tipo,
    String titulo,
    String autor,
    String genero,
    String fechaPublicacion,
    Integer valoracion,
    Integer paginas
  ) {
    return new Documento(
      UUID.randomUUID(), 
      tipo, 
      titulo, 
      autor, 
      genero, 
      ValidadorDocumento.parsearFecha(fechaPublicacion),
      valoracion,
      paginas
    );
  }

  public Documento actualizar(
    TipoDocumento tipo,
    String titulo,
    String autor,
    String genero,
    String fechaPublicacion,
    Integer valoracion,
    Integer paginas
  ) {
    return new Documento(
      this._id,
      tipo,
      titulo,
      autor,
      genero,
      ValidadorDocumento.parsearFecha(fechaPublicacion),
      valoracion,
      paginas
    );
  }

  // Getters
  public UUID getId() {
    return this._id;
  }

  public TipoDocumento getTipo() {
    return this._tipo;
  }

  public String getTitulo() {
    return this._titulo;
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
