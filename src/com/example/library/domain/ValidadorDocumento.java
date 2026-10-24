package com.example.library.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.UUID;

public class ValidadorDocumento {
  private static DateTimeFormatter _FORMATO_FECHA = DateTimeFormatter
    .ofPattern("uuuu-MM-dd", Locale.ROOT)
    .withResolverStyle(ResolverStyle.STRICT);
  
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

  public static LocalDate parsearFecha(String fecha) {
    if (fecha == null || fecha.isBlank()) {
      return null;
    }

    String valor = fecha.trim();
    if (!valor.matches("\\d{4}-\\d{2}-\\d{2}")) {
      throw new ValidarExcepcion(
        "La fecha debe tener el formato ISO yyyy-MM-dd"
      );
    }

    try {
      return LocalDate.parse(valor, _FORMATO_FECHA);
    } catch (Exception e) {
      throw new ValidarExcepcion(
        "La fecha de publicación no es válida"
      );
    }
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
    } catch (Exception e) {
      throw new ValidarExcepcion(
        "La valoración debe ser un numero entero."
      );
    }
  }
}
