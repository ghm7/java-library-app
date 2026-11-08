package com.example.library.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PanelRedondeado extends JPanel {
  private final Color _colorFondo;
  private final int _radio;

  public PanelRedondeado(Color colorFondo, int radio) {
    _colorFondo = colorFondo;
    _radio = radio;
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    Graphics2D g2 = (Graphics2D) graphics.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(_colorFondo);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), _radio, _radio);
    g2.dispose();
    super.paintComponent(graphics);
  }
}
