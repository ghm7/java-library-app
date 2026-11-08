package com.example.library.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class BotonCapsula extends JButton {
    private final Color _colorFondo;

    public BotonCapsula(String texto, Color colorFondo) {
        super(texto);
        _colorFondo = colorFondo;
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
        g2.setColor(getModel().isRollover() ? _colorFondo.brighter() : _colorFondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(graphics);
    }
}
