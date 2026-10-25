package com.example.library.domain;

public class ValidarExcepcion extends IllegalArgumentException {
  public ValidarExcepcion(String message) {
    super(message);
  }
}
