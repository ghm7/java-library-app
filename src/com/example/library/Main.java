package com.example.library;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public final class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Main::crearVentana);
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