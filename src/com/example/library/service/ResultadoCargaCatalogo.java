package com.example.library.service;

import com.example.library.domain.Documento;
import java.util.List;
import java.util.Objects;

public class ResultadoCargaCatalogo {
  private final EstadoCargaCatalogo _estado;
  private final List<Documento> _documentos;
  private final String _mensaje;

  public ResultadoCargaCatalogo(
      EstadoCargaCatalogo estado, List<Documento> documentos, String mensaje) {
    this._estado = Objects.requireNonNull(estado);
    this._documentos = List.copyOf(documentos);
    this._mensaje = mensaje == null ? "" : mensaje;
  }

  public EstadoCargaCatalogo getEstado() {
    return this._estado;
  }

  public List<Documento> getDocumentos() {
    return this._documentos;
  }

  public String getMensaje() {
    return this._mensaje;
  }
}
