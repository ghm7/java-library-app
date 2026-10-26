package com.example.library.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class MainWindow extends JFrame {
  private static final String CATALOGO = "catalogo";
  private static final String DETALLE = "detalle";
  private static final String FORMULARIO = "formulario";
  private static final String EXPIRADO = "expirado";

  private final CardLayout cards = new CardLayout();
  private final JPanel content = new JPanel(cards);

  public MainWindow() {
    super("Biblioteca Java");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(900, 600));
    setSize(1000, 680);
    setLocationRelativeTo(null);

    add(createHeader(), BorderLayout.NORTH);

    content.add(createCatalogo(), CATALOGO);
    content.add(createDetalle(), DETALLE);
    content.add(createFormulario(), FORMULARIO);
    content.add(createExpirado(), EXPIRADO);
    add(content, BorderLayout.CENTER);

    showCard(CATALOGO);
  }

  private JPanel createHeader() {
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(UiTheme.GRAFITO);
    header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

    JLabel brand = new JLabel("Biblioteca Java");
    brand.setForeground(UiTheme.CREMA);
    brand.setFont(UiTheme.TITULO);

    JButton catalogo = new JButton("Catálogo");
    catalogo.addActionListener(event -> showCard(CATALOGO));

    header.add(brand, BorderLayout.WEST);
    header.add(catalogo, BorderLayout.EAST);
    return header;
  }

  private JPanel createCatalogo() {
    JPanel panel = page("Catálogo");
    panel.add(Ui.subtitle("Aún no hay documentos para mostrar."), BorderLayout.NORTH);

    JButton verDetalle = Ui.primaryButton("Ver ejemplo de detalle");
    verDetalle.addActionListener(event -> showCard(DETALLE));
    panel.add(verDetalle, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel createDetalle() {
    JPanel panel = page("Detalle");
    panel.add(Ui.subtitle("Esta vista mostrará los metadatos de un documento en RF-08."),
        BorderLayout.CENTER);

    JButton editar = Ui.primaryButton("Abrir formulario");
    editar.addActionListener(event -> showCard(FORMULARIO));
    panel.add(editar, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel createFormulario() {
    JPanel panel = page("Formulario");
    panel.add(Ui.subtitle("El formulario visual se conectará al modelo y al servicio después."),
        BorderLayout.CENTER);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelar = new JButton("Cancelar");
    cancelar.addActionListener(event -> showCard(CATALOGO));

    JButton simularExpiracion = Ui.primaryButton("Ver estado expirado");
    simularExpiracion.addActionListener(event -> showCard(EXPIRADO));

    actions.add(cancelar);
    actions.add(simularExpiracion);
    panel.add(actions, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel createExpirado() {
    JPanel panel = page("Período expirado");
    panel.add(Ui.subtitle("Las acciones de escritura estarán deshabilitadas en este estado."),
        BorderLayout.CENTER);

    JButton volver = Ui.primaryButton("Volver al catálogo");
    volver.addActionListener(event -> showCard(CATALOGO));
    panel.add(volver, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel page(String title) {
    JPanel container = new JPanel(new BorderLayout(0, 24));
    container.setBackground(UiTheme.CREMA);
    container.setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

    JPanel surface = Ui.surface();
    surface.setLayout(new BorderLayout(0, 20));
    surface.add(Ui.title(title), BorderLayout.NORTH);

    container.add(surface, BorderLayout.CENTER);
    return surface;
  }

  private void showCard(String card) {
    cards.show(content, card);
  }
}
