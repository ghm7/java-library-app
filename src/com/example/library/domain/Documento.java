package com.example.library.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Documento {
  private UUID _id;
  private TipoDocumento _tipo;
  private String _titulo;
  private String _autor;
  private String _genero;
  private LocalDate _fechaPublicacion;
  private Integer _valoracion;

  public Documento(
    UUID id,
    TipoDocumento tipo,
    String titulo,
    String autor,
    String genero,
    LocalDate fechaPublicacion,
    Integer valoracion
  ) {
    this._id = ValidadorDocumento.validarId(id);
    this._tipo = ValidadorDocumento.validarTipo(tipo);
    this._titulo = ValidadorDocumento.validarTitulo(titulo);
    this._autor = ValidadorDocumento.textoOpcional(autor);
    this._genero = ValidadorDocumento.textoOpcional(genero);
    this._fechaPublicacion = fechaPublicacion;
    this._valoracion = 
      ValidadorDocumento.validarValoracion(valoracion);
  }

  public static Documento nuevo(
    TipoDocumento tipo,
    String titulo,
    String autor,
    String genero,
    String fechaPublicacion,
    Integer valoracion
  ) {
    return new Documento(
      UUID.randomUUID(), 
      tipo, 
      titulo, 
      autor, 
      genero, 
      ValidadorDocumento.parsearFecha(fechaPublicacion),
      valoracion
    );
  }

  public Documento actualizar(
    TipoDocumento tipo,
    String titulo,
    String autor,
    String genero,
    String fechaPublicacion,
    Integer valoracion
  ) {
    return new Documento(
      this._id,
      tipo,
      titulo,
      autor,
      genero,
      ValidadorDocumento.parsearFecha(fechaPublicacion),
      valoracion
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

  public String getAutor() {
    return this._autor;
  }

  public String getGenero() {
    return this._genero;
  }

  public LocalDate getFechaPublicacion() {
    return this._fechaPublicacion;
  }

  public Integer getValoracion() {
    return this._valoracion;
  }

  public String getFechaPublicacionIso() {
    if (this._fechaPublicacion == null) {
      return null;
    }

    return this._fechaPublicacion.format(DateTimeFormatter.ISO_LOCAL_DATE);
  }
}
