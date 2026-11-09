package com.example.library.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class BotonCapsula extends JButton {
  private static final Color FONDO_NORMAL = Color.WHITE;
  private static final float FACTOR_OSCURECIMIENTO = 0.82f;

  private final Color _colorEnfasis;

  public BotonCapsula(String texto, Color colorEnfasis) {
    super(texto);
    _colorEnfasis = colorEnfasis;
    setFont(TemaGrafico.FILTRO);
    setForeground(TemaGrafico.GRAFITO);
    setBorder(BorderFactory.createEmptyBorder(10, 17, 10, 17));
    setContentAreaFilled(false);
    setFocusPainted(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    Graphics2D g2 = (Graphics2D) graphics.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(_colorDeFondo());
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
    g2.dispose();
    super.paintComponent(graphics);
  }

  private Color _colorDeFondo() {
    if (getModel().isRollover()) {
      return _oscurecer(_colorEnfasis);
    }

    if (hasFocus()) {
      return _colorEnfasis;
    }

    return FONDO_NORMAL;
  }

  private Color _oscurecer(Color color) {
    return new Color(
        Math.round(color.getRed() * FACTOR_OSCURECIMIENTO),
        Math.round(color.getGreen() * FACTOR_OSCURECIMIENTO),
        Math.round(color.getBlue() * FACTOR_OSCURECIMIENTO));
  }
}
