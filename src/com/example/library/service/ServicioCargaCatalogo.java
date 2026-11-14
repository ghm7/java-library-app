package com.example.library.service;

import com.example.library.domain.Documento;
import com.example.library.repository.RepositorioDocumentoBinario;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Objects;

public class ServicioCargaCatalogo {
  private final ServicioDocumento _servicioDocumento;

  public ServicioCargaCatalogo(RepositorioDocumentoBinario repositorio) {
    this(new ServicioDocumento(repositorio));
  }

  public ServicioCargaCatalogo(ServicioDocumento servicioDocumento) {
    this._servicioDocumento = Objects.requireNonNull(servicioDocumento);
  }

  public ResultadoCargaCatalogo cargarCatalogo() {
    try {
      List<Documento> documentos = this._servicioDocumento.listar();
      String mensaje = documentos.size() + " documentos cargados.";

      return new ResultadoCargaCatalogo(EstadoCargaCatalogo.CARGADO, documentos, mensaje);
    } catch (IllegalStateException e) {
      if (e.getCause() instanceof NoSuchFileException) {
        return new ResultadoCargaCatalogo(
            EstadoCargaCatalogo.AUSENTE,
            List.of(),
            "No se encontró el catálogo. Podés crear el primer documento para inicializarlo.");
      }
      return new ResultadoCargaCatalogo(EstadoCargaCatalogo.INVALIDO, List.of(), e.getMessage());
    }
  }
}
