package com.example.library.ui;

import com.example.library.service.ServicioDocumento;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VentanaPrincipal extends JFrame {
  public VentanaPrincipal() {
    this(new ServicioDocumento());
  }

  public VentanaPrincipal(ServicioDocumento servicio) {
    super("Biblioteca Java");

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(900, 600));
    setSize(1000, 680);
    setLocationRelativeTo(null);

    add(_crearCabecera(), BorderLayout.NORTH);
    add(new PanelDocumentos(servicio), BorderLayout.CENTER);
  }

  private JPanel _crearCabecera() {
    JPanel cabecera = new JPanel(new BorderLayout());
    cabecera.setBackground(TemaGrafico.GRAFITO);
    cabecera.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

    JLabel marca = new JLabel("Biblioteca Java");
    marca.setForeground(TemaGrafico.CREMA);
    marca.setFont(TemaGrafico.TITULO);

    cabecera.add(marca, BorderLayout.WEST);

    return cabecera;
  }
}
