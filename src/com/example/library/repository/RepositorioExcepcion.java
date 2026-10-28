package com.example.library.repository;

import java.nio.file.Path;
import java.util.UUID;

public class RepositorioExcepcion extends RuntimeException {
  private static final String _MENSAJE_RUTA_OBLIGATORIA =
    "La ruta del catálogo es obligatoria.";

  private static final String _MENSAJE_DOCUMENTO_OBLIGATORIO =
    "El documento es obligatorio.";

  private static final String _MENSAJE_ID_OBLIGATORIO =
    "El id es obligatorio.";

  public RepositorioExcepcion(String mensaje) {
    super(mensaje);
  }

  public RepositorioExcepcion(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }

  public static RepositorioExcepcion rutaObligatoria() {
    return new RepositorioExcepcion(_MENSAJE_RUTA_OBLIGATORIA);
  }

  public static RepositorioExcepcion documentoObligatorio() {
    return new RepositorioExcepcion(_MENSAJE_DOCUMENTO_OBLIGATORIO);
  }

  public static RepositorioExcepcion idObligatorio() {
    return new RepositorioExcepcion(_MENSAJE_ID_OBLIGATORIO);
  }

  public static RepositorioExcepcion catalogoNoEncontrado(Path ruta) {
    return new RepositorioExcepcion(
      "No se encontró el catálogo: " + ruta + "."
    );
  }

  public static RepositorioExcepcion rutaNoEsArchivo(Path ruta) {
    return new RepositorioExcepcion(
      "La ruta no corresponde a un archivo: " + ruta + "."
    );
  }

  public static RepositorioExcepcion catalogoInvalido(
    Path ruta,
    Throwable causa
  ) {
    return new RepositorioExcepcion(
      "El catálogo binario es inválido o no se puede leer: "
        + ruta + ".",
      causa
    );
  }

  public static RepositorioExcepcion errorAlPersistir(
    Path ruta,
    Throwable causa
  ) {
    return new RepositorioExcepcion(
      "No se pudo persistir el catálogo: " + ruta + ".",
      causa
    );
  }

  public static RepositorioExcepcion documentoNoEncontrado(UUID id) {
    return new RepositorioExcepcion(
      "No existe un documento con id " + id + "."
    );
  }

  public static RepositorioExcepcion documentoDuplicado(UUID id) {
    return new RepositorioExcepcion(
      "Ya existe un documento con id " + id + "."
    );
  }
}