package com.example.library;

import com.example.library.repository.RepositorioDocumentoBinario;
import com.example.library.service.ServicioPeriodoDemo;
import com.example.library.ui.PanelDocumentos;
import java.awt.*;
import javax.swing.*;

public class Principal {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Principal::abrir);
  }

  private static void abrir() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ignorado) {
    }

    JFrame ventana = new JFrame("Biblioteca Java");
    JLabel marca = new JLabel(ventana.getTitle());

    marca.setOpaque(true);
    marca.setBackground(PanelDocumentos.GRAFITO);
    marca.setForeground(PanelDocumentos.CREMA);
    marca.setFont(PanelDocumentos.TITULO);
    marca.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

    ventana.add(marca, BorderLayout.NORTH);
    ventana.add(new PanelDocumentos(new RepositorioDocumentoBinario(), new ServicioPeriodoDemo()));
    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ventana.setMinimumSize(new Dimension(900, 600));
    ventana.setSize(1000, 680);
    ventana.setLocationRelativeTo(null);
    ventana.setVisible(true);
  }
}
