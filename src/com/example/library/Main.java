package com.example.library;

import javax.swing.JFrame; 
import javax.swing.SwingUtilities;
import com.example.library.ui.MainWindow;
import com.example.library.ui.UiTheme;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public final class Main {
  public static void main(String[] args) {
	  SwingUtilities.invokeLater(() -> {
	      UiTheme.installLookAndFeel();
	      new MainWindow().setVisible(true);
  });
  }


  private static void crearVentana() {
    JFrame ventana = new JFrame("Biblioteca Java");

    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ventana.setSize(900, 600);
    ventana.setLocationRelativeTo(null);

    JLabel mensaje = new JLabel(
      "Catalogo de biblioteca",
      SwingConstants.CENTER
    );

    ventana.add(mensaje);
    ventana.setVisible(true);
  }
}