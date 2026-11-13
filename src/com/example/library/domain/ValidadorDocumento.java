package com.example.library.domain;

import java.util.UUID;

public class ValidadorDocumento {
  private ValidadorDocumento() {}

  public static UUID validarId(UUID id) {
    if (id == null) {
      throw new ValidarExcepcion("El id es obligatorio.");
    }

    return id;
  }

  public static TipoDocumento validarTipo(TipoDocumento tipo) {
    if (tipo == null) {
      throw new ValidarExcepcion("El tipo es obligatorio.");
    }

    return tipo;
  }

  public static String validarTitulo(String titulo) {
    if (titulo == null || titulo.isBlank()) {
      throw new ValidarExcepcion("El titulo es obligatorio.");
    }

    return titulo.trim();
  }

  public static String textoOpcional(String texto) {
    return texto == null || texto.isBlank() ? null : texto;
  }

  public static Integer validarValoracion(Integer valoracion) {
    return validarRango(valoracion, 5, "La valoración debe estar entre 1 y 5");
  }

  public static Integer validarPaginas(Integer paginas) {
    return validarRango(paginas, 999, "La cantidad de páginas debe estar entre 1 y 999.");
  }

  private static Integer validarRango(Integer valor, int maximo, String mensaje) {
    if (valor != null && (valor < 1 || valor > maximo)) {
      throw new ValidarExcepcion(mensaje);
    }
    return valor;
  }

  public static Integer parsearValoracion(String texto) {
    return validarValoracion(parsearEntero(texto, "La valoración"));
  }

  public static Integer parsearPaginas(String texto) {
    return validarPaginas(parsearEntero(texto, "La cantidad de páginas"));
  }

  private static Integer parsearEntero(String texto, String campo) {
    if (textoOpcional(texto) == null) {
      return null;
    }
    try {
      return Integer.valueOf(texto.trim());
    } catch (NumberFormatException e) {
      throw new ValidarExcepcion(campo + " debe ser un numero entero.");
    }
  }
}
