package com.example.library;

import com.example.library.ui.TemaGrafico;
import com.example.library.ui.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class Principal {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          TemaGrafico.instalarApariencia();
          new VentanaPrincipal().setVisible(true);
        });
  }
}
