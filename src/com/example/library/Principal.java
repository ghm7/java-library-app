package com.example.library;

import javax.swing.SwingUtilities;
import com.example.library.repository.RepositorioDocumentoBinario;
import com.example.library.service.ResultadoCargaCatalogo;
import com.example.library.service.ServicioCargaCatalogo;
import com.example.library.ui.VentanaPrincipal;
import com.example.library.ui.TemaGrafico;

public class Principal {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Principal::_iniciarAplicacion);
  }

  private static void _iniciarAplicacion() {
    TemaGrafico.instalarApariencia();

    ResultadoCargaCatalogo resultado = new ServicioCargaCatalogo(
      new RepositorioDocumentoBinario()
    ).cargarCatalogo();

    new VentanaPrincipal(resultado).setVisible(true);
  }
}
