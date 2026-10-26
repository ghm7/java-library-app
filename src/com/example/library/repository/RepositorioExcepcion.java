package com.example.library.repository;

public class RepositorioExcepcion extends RuntimeException {
  public RepositorioExcepcion(String mensaje) {
    super(mensaje);
  }

  public RepositorioExcepcion(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}