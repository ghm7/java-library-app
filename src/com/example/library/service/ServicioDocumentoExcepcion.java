package com.example.library.service;

public class ServicioDocumentoExcepcion extends RuntimeException {
  public ServicioDocumentoExcepcion(String mensaje) {
    super(mensaje);
  }

  public ServicioDocumentoExcepcion(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }

  public static ServicioDocumentoExcepcion repositorioObligatorio() {
    return new ServicioDocumentoExcepcion(
      "El repositorio de documentos es obligatorio."
    );
  }

  public static ServicioDocumentoExcepcion errorAlListar(Throwable causa) {
    return new ServicioDocumentoExcepcion(
      "No se pudo obtener el catálogo de documentos.",
      causa
    );
  }

  public static ServicioDocumentoExcepcion errorAlConsultar(
    Throwable causa
  ) {
    return new ServicioDocumentoExcepcion(
      causa.getMessage(),
      causa
    );
  }

  public static ServicioDocumentoExcepcion errorAlCrear(Throwable causa) {
    return new ServicioDocumentoExcepcion(
      "No se pudo crear el documento."
    );
  }

  public static ServicioDocumentoExcepcion errorAlActualizar(
    Throwable causa
  ) {
    return new ServicioDocumentoExcepcion(
      causa.getMessage(),
      causa
    );
  }

  public static ServicioDocumentoExcepcion errorAlEliminar(
    Throwable causa
  ) {
    return new ServicioDocumentoExcepcion(
      causa.getMessage(),
      causa
    );
  }
}
