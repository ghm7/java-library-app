package com.example.library;

import javax.swing.SwingUtilities;
import com.example.library.ui.VentanaPrincipal;
import com.example.library.ui.TemaGrafico;

public class Principal {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      TemaGrafico.instalarApariencia();
      new VentanaPrincipal().setVisible(true);
    });
  }
}
