package com.example.library.repository;

import java.nio.file.Path;
import java.util.UUID;

public class RepositorioExcepcion extends RuntimeException {
  private final boolean _catalogoAusente;

  public RepositorioExcepcion(String mensaje) {
    this(mensaje, null, false);
  }

  public RepositorioExcepcion(String mensaje, Throwable causa) {
    this(mensaje, causa, false);
  }

  private RepositorioExcepcion(String mensaje, Throwable causa, boolean catalogoAusente) {
    super(mensaje, causa);
    this._catalogoAusente = catalogoAusente;
  }

  public boolean esCatalogoAusente() {
    return this._catalogoAusente;
  }

  public static RepositorioExcepcion rutaObligatoria() {
    return new RepositorioExcepcion("La ruta del catálogo es obligatoria.");
  }

  public static RepositorioExcepcion documentoObligatorio() {
    return new RepositorioExcepcion("El documento es obligatorio.");
  }

  public static RepositorioExcepcion idObligatorio() {
    return new RepositorioExcepcion("El id es obligatorio.");
  }

  public static RepositorioExcepcion catalogoNoEncontrado(Path ruta) {
    return new RepositorioExcepcion(
        "No se encontró el catálogo: "
            + ruta
            + ". Podés crear el primer documento para inicializarlo.",
        null,
        true);
  }

  public static RepositorioExcepcion rutaNoEsArchivo(Path ruta) {
    return new RepositorioExcepcion("La ruta no corresponde a un archivo: " + ruta + ".");
  }

  public static RepositorioExcepcion catalogoVacio(Path ruta) {
    return new RepositorioExcepcion(
        "El archivo del catálogo está vacío: "
            + ruta
            + ". Restaure un catálogo válido antes de continuar.");
  }

  public static RepositorioExcepcion catalogoCorrupto(Path ruta, Throwable causa) {
    return catalogoInvalido("El catálogo está corrupto o incompleto", ruta, causa);
  }

  public static RepositorioExcepcion catalogoIncompatible(Path ruta, Throwable causa) {
    return catalogoInvalido(
        "El catálogo fue creado con un formato o una versión incompatible", ruta, causa);
  }

  public static RepositorioExcepcion catalogoConFormatoInvalido(Path ruta) {
    return catalogoInvalido("El catálogo no contiene una lista de documentos válida", ruta, null);
  }

  public static RepositorioExcepcion elementoInvalido(Path ruta, int posicion) {
    return catalogoInvalido(
        "El elemento " + posicion + " del catálogo no es un Documento válido", ruta, null);
  }

  public static RepositorioExcepcion documentoInvalido(Path ruta, int posicion, Throwable causa) {
    return catalogoInvalido(
        "El documento " + posicion + " del catálogo tiene campos inválidos", ruta, causa);
  }

  public static RepositorioExcepcion idDuplicadoEnCatalogo(Path ruta, UUID id) {
    return catalogoInvalido("El catálogo contiene el id duplicado " + id, ruta, null);
  }

  private static RepositorioExcepcion catalogoInvalido(
      String problema, Path ruta, Throwable causa) {
    return new RepositorioExcepcion(problema + ": " + ruta + ". No se realizaron cambios.", causa);
  }

  public static RepositorioExcepcion errorAlLeer(Path ruta, Throwable causa) {
    return new RepositorioExcepcion(
        "No se pudo leer el catálogo: " + ruta + ". Compruebe sus permisos y vuelva a intentarlo.",
        causa);
  }

  public static RepositorioExcepcion errorAlPersistir(Path ruta, Throwable causa) {
    return new RepositorioExcepcion(
        "No se pudo guardar el catálogo: " + ruta + ". La operación no se completó.", causa);
  }

  public static RepositorioExcepcion documentoNoEncontrado(UUID id) {
    return new RepositorioExcepcion("No existe un documento con id " + id + ".");
  }

  public static RepositorioExcepcion documentoDuplicado(UUID id) {
    return new RepositorioExcepcion("Ya existe un documento con id " + id + ".");
  }
}
