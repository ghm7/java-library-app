package com.example.library.domain;

public enum TipoDocumento {
  LIBRO("Libro"), ARTICULO_CIENTIFICO("Artículo científico"), PUBLICACION("Publicación"), OTRO("Otro");

  private final String nombre;
  TipoDocumento(String nombre) { this.nombre = nombre; }
  @Override public String toString() { return nombre; }
}
