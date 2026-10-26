package com.example.library.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class Ui {
  private Ui() {}

  public static JLabel title(String text) {
    JLabel label = new JLabel(text);
    label.setFont(UiTheme.TITULO);
    label.setForeground(UiTheme.GRAFITO);
    return label;
  }

  public static JLabel subtitle(String text) {
    JLabel label = new JLabel(text);
    label.setFont(UiTheme.SUBTITULO);
    label.setForeground(UiTheme.TEXTO_SECUNDARIO);
    return label;
  }

  public static JButton primaryButton(String text) {
    JButton button = new JButton(text);
    button.setFont(new Font("SansSerif", Font.BOLD, 14));
    button.setForeground(Color.WHITE);
    button.setBackground(UiTheme.CORAL);
    button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    button.setFocusPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return button;
  }

  public static JPanel surface() {
    JPanel panel = new JPanel();
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0xE7DDD2)),
        BorderFactory.createEmptyBorder(28, 28, 28, 28)));
    return panel;
  }
}