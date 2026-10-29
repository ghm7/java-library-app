package com.example.library.domain;

import java.util.UUID;

public class ValidadorDocumento {
  private ValidadorDocumento() {
  }

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
    if (texto == null || texto.isBlank()) {
      return null;
    }

    return texto;
  }

  public static Integer validarValoracion(Integer valoracion) {
    if (valoracion == null) {
      return null;
    }

    if (valoracion < 1 || valoracion > 5) {
      throw new ValidarExcepcion(
        "La valoración debe estar entre 1 y 5"
      );
    }

    return valoracion;
  }

  public static Integer parsearValoracion(String valoracion) {
    if (valoracion == null || valoracion.isBlank()) {
      return null;
    }

    try {
      return validarValoracion(Integer.valueOf(valoracion.trim()));
    } catch (NumberFormatException e) {
      throw new ValidarExcepcion(
        "La valoración debe ser un numero entero."
      );
    }
  }

  public static Integer validarPaginas(Integer paginas) {
    if (paginas == null) {
      return null;
    }

    if (paginas < 1 || paginas > 999) {
      throw new ValidarExcepcion(
        "La cantidad de páginas debe estar entre 1 y 999."
      );
    }

    return paginas;
  }

  public static Integer parsearPaginas(String paginas) {
    if (paginas == null || paginas.isBlank()) {
      return null;
    }

    try {
      return validarPaginas(Integer.valueOf(paginas.trim()));
    } catch (NumberFormatException e) {
      throw new ValidarExcepcion(
        "La cantidad de páginas debe ser un numero entero."
      );
    }
  }
}
