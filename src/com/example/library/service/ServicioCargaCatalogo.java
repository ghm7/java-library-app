package com.example.library.service;

import com.example.library.domain.Documento;
import com.example.library.repository.RepositorioDocumentoBinario;
import com.example.library.repository.RepositorioExcepcion;
import java.util.List;

public class ServicioCargaCatalogo {
  private final ServicioDocumento _servicioDocumento;

  public ServicioCargaCatalogo(RepositorioDocumentoBinario repositorio) {
    this(new ServicioDocumento(repositorio));
  }

  public ServicioCargaCatalogo(ServicioDocumento servicioDocumento) {
    if (servicioDocumento == null) {
      throw new IllegalArgumentException("El servicio de documentos es obligatorio.");
    }
    this._servicioDocumento = servicioDocumento;
  }

  public ResultadoCargaCatalogo cargarCatalogo() {
    try {
      List<Documento> documentos = this._servicioDocumento.listar();
      String mensaje = documentos.size() + " documentos cargados.";

      return new ResultadoCargaCatalogo(EstadoCargaCatalogo.CARGADO, documentos, mensaje);
    } catch (ServicioDocumentoExcepcion e) {
      boolean ausente =
          e.getCause() instanceof RepositorioExcepcion causa && causa.esCatalogoAusente();
      EstadoCargaCatalogo estado =
          ausente ? EstadoCargaCatalogo.AUSENTE : EstadoCargaCatalogo.INVALIDO;
      return new ResultadoCargaCatalogo(estado, List.of(), e.getMessage());
    } catch (RuntimeException e) {
      return new ResultadoCargaCatalogo(
          EstadoCargaCatalogo.INVALIDO,
          List.of(),
          "No se pudo cargar el catálogo de documentos. No se realizaron cambios.");
    }
  }
}
