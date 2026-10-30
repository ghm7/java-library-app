package com.example.library.service;

import java.util.List;

import com.example.library.domain.Documento;

public class ResultadoCargaCatalogo {
  private final EstadoCargaCatalogo _estado;
  private final List<Documento> _documentos;
  private final String _mensaje;

  public ResultadoCargaCatalogo(
    EstadoCargaCatalogo estado,
    List<Documento> documentos,
    String mensaje
  ) {
    if (estado == null) {
      throw new IllegalArgumentException("El estado de carga es obligatorio.");
    }

    if (documentos == null) {
      throw new IllegalArgumentException("La lista de documentos es obligatoria.");
    }

    this._estado = estado;
    this._documentos = List.copyOf(documentos);
    this._mensaje = mensaje == null ? "" : mensaje;
  }

  public EstadoCargaCatalogo obtenerEstado() {
    return this._estado;
  }

  public List<Documento> obtenerDocumentos() {
    return this._documentos;
  }

  public String obtenerMensaje() {
    return this._mensaje;
  }
}
