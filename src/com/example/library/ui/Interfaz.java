package com.example.library.ui;

import com.example.library.domain.TipoDocumento;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Interfaz {
  private Interfaz() {}

  public static JLabel titulo(String text) {
    JLabel label = new JLabel(text);

    label.setFont(TemaGrafico.TITULO);
    label.setForeground(TemaGrafico.GRAFITO);

    return label;
  }

  public static JLabel subtitulo(String text) {
    JLabel label = new JLabel(text);

    label.setFont(TemaGrafico.SUBTITULO);
    label.setForeground(TemaGrafico.TEXTO_SECUNDARIO);

    return label;
  }

  public static JButton botonPrimario(String text) {
    JButton button = new JButton(text);

    button.setFont(new Font("SansSerif", Font.BOLD, 14));
    button.setForeground(TemaGrafico.GRAFITO);
    button.setBackground(TemaGrafico.CORAL);
    button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    button.setFocusPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    return button;
  }

  public static JTextArea texto(String texto) {
    JTextArea area = new JTextArea(texto);

    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setOpaque(false);
    area.setFont(TemaGrafico.SUBTITULO);
    area.setForeground(TemaGrafico.GRAFITO);

    return area;
  }

  public static String nombreTipo(TipoDocumento tipo) {
    return switch (tipo) {
      case LIBRO -> "Libro";
      case ARTICULO_CIENTIFICO -> "Artículo científico";
      case PUBLICACION -> "Publicación";
      case OTRO -> "Otro";
    };
  }

  public static JPanel superficie() {
    JPanel panel = new JPanel();

    panel.setBackground(Color.WHITE);
    panel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE7DDD2)),
            BorderFactory.createEmptyBorder(28, 28, 28, 28)));

    return panel;
  }
}
