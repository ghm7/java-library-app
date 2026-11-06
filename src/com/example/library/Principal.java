package com.example.library;

import javax.swing.SwingUtilities;
import com.example.library.service.ServicioDocumento;
import com.example.library.ui.VentanaPrincipal;
import com.example.library.ui.TemaGrafico;

public class Principal {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Principal::_iniciarAplicacion);
  }

  private static void _iniciarAplicacion() {
    TemaGrafico.instalarApariencia();

    new VentanaPrincipal(new ServicioDocumento()).setVisible(true);
  }
}
