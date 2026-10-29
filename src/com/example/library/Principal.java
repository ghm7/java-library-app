package com.example.library;

import javax.swing.JFrame; 
import javax.swing.SwingUtilities;
import com.example.library.ui.VentanaPrincipal;
import com.example.library.ui.TemaGrafico;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Principal {
  public static void main(String[] args) {
	  SwingUtilities.invokeLater(() -> {
	    TemaGrafico.instalarApariencia();
	    _crearVentana();
	    new VentanaPrincipal().setVisible(true);
  });
  }


  private static void _crearVentana() {
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