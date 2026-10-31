package com.example.library.service;

import java.util.List;

import com.example.library.domain.Documento;
import com.example.library.repository.RepositorioDocumentoBinario;
import com.example.library.repository.RepositorioExcepcion;

public class ServicioCargaCatalogo {
  private final ServicioDocumento _servicioDocumento;

  public ServicioCargaCatalogo(RepositorioDocumentoBinario repositorio) {
    this._servicioDocumento = new ServicioDocumento(repositorio);
  }

  public ResultadoCargaCatalogo cargarCatalogo() {
    try {
      List<Documento> documentos = this._servicioDocumento.listar();
      String mensaje = documentos.size() + " documentos cargados.";

      return new ResultadoCargaCatalogo(
        EstadoCargaCatalogo.CARGADO,
        documentos,
        mensaje
      );
    } catch (ServicioDocumentoExcepcion e) {
      return new ResultadoCargaCatalogo(
        this._estadoPara(e),
        List.of(),
        e.getMessage()
      );
    }
  }

  private EstadoCargaCatalogo _estadoPara(
    ServicioDocumentoExcepcion excepcion
  ) {
    Throwable causa = excepcion.getCause();

    if (
      causa instanceof RepositorioExcepcion
      && ((RepositorioExcepcion) causa).esCatalogoAusente()
    ) {
      return EstadoCargaCatalogo.AUSENTE;
    }

    return EstadoCargaCatalogo.INVALIDO;
  }
}
