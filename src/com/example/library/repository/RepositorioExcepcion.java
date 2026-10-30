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
      "No se encontró el catálogo: " + ruta
        + ". Restaure un catálogo válido antes de continuar."
    );
  }

  public static RepositorioExcepcion rutaNoEsArchivo(Path ruta) {
    return new RepositorioExcepcion(
      "La ruta no corresponde a un archivo: " + ruta + "."
    );
  }

  public static RepositorioExcepcion catalogoVacio(Path ruta) {
    return new RepositorioExcepcion(
      "El archivo del catálogo está vacío: " + ruta
        + ". Restaure un catálogo válido antes de continuar."
    );
  }

  public static RepositorioExcepcion catalogoCorrupto(
    Path ruta,
    Throwable causa
  ) {
    return new RepositorioExcepcion(
      "El catálogo está corrupto o incompleto: " + ruta
        + ". No se realizaron cambios.",
      causa
    );
  }

  public static RepositorioExcepcion catalogoIncompatible(
    Path ruta,
    Throwable causa
  ) {
    return new RepositorioExcepcion(
      "El catálogo fue creado con un formato o una versión incompatible: "
        + ruta + ". No se realizaron cambios.",
      causa
    );
  }

  public static RepositorioExcepcion catalogoConFormatoInvalido(
    Path ruta
  ) {
    return new RepositorioExcepcion(
      "El catálogo no contiene una lista de documentos válida: "
        + ruta + ". No se realizaron cambios."
    );
  }

  public static RepositorioExcepcion elementoInvalido(
    Path ruta,
    int posicion
  ) {
    return new RepositorioExcepcion(
      "El elemento " + posicion
        + " del catálogo no es un Documento válido: " + ruta
        + ". No se realizaron cambios."
    );
  }

  public static RepositorioExcepcion documentoInvalido(
    Path ruta,
    int posicion,
    Throwable causa
  ) {
    return new RepositorioExcepcion(
      "El documento " + posicion
        + " del catálogo tiene campos inválidos: " + ruta
        + ". No se realizaron cambios.",
      causa
    );
  }

  public static RepositorioExcepcion idDuplicadoEnCatalogo(
    Path ruta,
    UUID id
  ) {
    return new RepositorioExcepcion(
      "El catálogo contiene el id duplicado " + id + ": " + ruta
        + ". No se realizaron cambios."
    );
  }

  public static RepositorioExcepcion errorAlLeer(
    Path ruta,
    Throwable causa
  ) {
    return new RepositorioExcepcion(
      "No se pudo leer el catálogo: " + ruta
        + ". Compruebe sus permisos y vuelva a intentarlo.",
      causa
    );
  }

  public static RepositorioExcepcion errorAlPersistir(
    Path ruta,
    Throwable causa
  ) {
    return new RepositorioExcepcion(
      "No se pudo guardar el catálogo: " + ruta
        + ". La operación no se completó.",
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
