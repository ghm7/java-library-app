package com.example.library.ui;

import com.example.library.domain.TipoDocumento;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Interfaz {
  private Interfaz() {}

  public static JLabel titulo(String texto) {
    return etiqueta(texto, TemaGrafico.TITULO, TemaGrafico.GRAFITO);
  }

  public static JLabel subtitulo(String texto) {
    return etiqueta(texto, TemaGrafico.SUBTITULO, TemaGrafico.TEXTO_SECUNDARIO);
  }

  private static JLabel etiqueta(String texto, Font fuente, Color color) {
    JLabel etiqueta = new JLabel(texto);
    etiqueta.setFont(fuente);
    etiqueta.setForeground(color);
    return etiqueta;
  }

  static JPanel panel(LayoutManager layout) {
    JPanel panel = new JPanel(layout);
    panel.setOpaque(false);
    return panel;
  }

  static JScrollPane conDesplazamiento(JPanel contenido) {
    JPanel contenedor = panel(new BorderLayout());
    contenedor.add(contenido, BorderLayout.NORTH);
    return new JScrollPane(contenedor);
  }

  static void agregarFila(JPanel campos, JLabel etiqueta, JComponent campo) {
    GridBagConstraints posicion = new GridBagConstraints();
    posicion.gridy = campos.getComponentCount() / 2;
    posicion.anchor = GridBagConstraints.NORTHWEST;
    posicion.insets = new Insets(6, 8, 6, 8);
    etiqueta.setLabelFor(campo);
    campos.add(etiqueta, posicion);
    posicion.gridx = 1;
    posicion.weightx = 1;
    posicion.fill = GridBagConstraints.HORIZONTAL;
    campos.add(campo, posicion);
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
