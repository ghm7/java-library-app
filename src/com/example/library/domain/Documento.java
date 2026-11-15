package com.example.library.domain;

import java.io.Serializable;
import java.util.UUID;

public record Documento(
    UUID _id,
    TipoDocumento _tipo,
    String _titulo,
    String _descripcion,
    String _genero,
    Integer _valoracion,
    Integer _paginas)
    implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final String VALORACION = "La valoración debe estar entre 1 y 5";
  private static final String PAGINAS = "La cantidad de páginas debe estar entre 1 y 999.";

  public Documento {
    if (_id == null) throw new IllegalArgumentException("El id es obligatorio.");

    if (_tipo == null) throw new IllegalArgumentException("El tipo es obligatorio.");

    if (_titulo == null || _titulo.isBlank())
      throw new IllegalArgumentException("El titulo es obligatorio.");

    _titulo = _titulo.trim();
    _descripcion = opcional(_descripcion);
    _genero = opcional(_genero);

    validarRango(_valoracion, 5, VALORACION);
    validarRango(_paginas, 999, PAGINAS);
  }

  private static String opcional(String texto) {
    if (texto == null || texto.isBlank()) {
      return null;
    } else {
      return texto;
    }
  }

  private static Integer validarRango(Integer valor, int maximo, String mensaje) {
    if (valor != null && (valor < 1 || valor > maximo)) throw new IllegalArgumentException(mensaje);

    return valor;
  }

  public static Documento desdeFormulario(
      UUID id,
      TipoDocumento tipo,
      String titulo,
      String descripcion,
      String genero,
      String valoracion,
      String paginas) {
    return new Documento(
        id,
        tipo,
        titulo,
        descripcion,
        genero,
        validarRango(entero(valoracion, "La valoración"), 5, VALORACION),
        validarRango(entero(paginas, "La cantidad de páginas"), 999, PAGINAS));
  }

  private static Integer entero(String texto, String nombre) {
    if (texto.isBlank()) return null;

    try {
      return Integer.valueOf(texto.trim());
    } catch (NumberFormatException error) {
      throw new IllegalArgumentException(nombre + " debe ser un numero entero.");
    }
  }
}
