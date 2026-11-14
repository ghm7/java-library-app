package com.example.library.ui;

import com.example.library.domain.*;
import com.example.library.repository.RepositorioDocumentoBinario;
import com.example.library.service.ServicioPeriodoDemo;
import java.awt.*;
import java.nio.file.NoSuchFileException;
import java.util.Objects;
import java.util.UUID;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import static java.awt.BorderLayout.*;
import static javax.swing.BorderFactory.*;

public class PanelDocumentos extends JPanel {
  public static final Color CREMA = new Color(0xFFF8ED), GRAFITO = new Color(0x2F2F33);
  public static final Font TITULO = new Font("SansSerif", Font.BOLD, 26);
  private static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 15);
  private final RepositorioDocumentoBinario catalogo;
  private final ServicioPeriodoDemo demo;
  private final JPanel pantalla = panel(new BorderLayout(0, 12));
  private final JTextArea mensaje = texto("", 1);
  private final Timer reloj = new Timer(1000, e -> comprobarDemo());
  private boolean expirada, avisoMostrado;

  public PanelDocumentos(RepositorioDocumentoBinario catalogo, ServicioPeriodoDemo demo) {
    super(new BorderLayout(0, 12));
    this.catalogo = catalogo;
    this.demo = demo;
    expirada = demo.estaExpirada();
    setBackground(CREMA);
    setBorder(createEmptyBorder(20, 24, 20, 24));
    mensaje.setName("mensaje");
    mensaje.getAccessibleContext().setAccessibleName("Estado de la operación");
    add(pantalla, CENTER);
    add(mensaje, SOUTH);
    mostrarCatalogo("");
  }

  @Override public void addNotify() {
    super.addNotify();
    reloj.start();
    comprobarDemo();
  }
  @Override public void removeNotify() {
    reloj.stop();
    super.removeNotify();
  }

  private boolean comprobarDemo() {
    if (!expirada && demo.estaExpirada()) {
      expirada = true;
      mostrarCatalogo("");
    }
    if (expirada && !avisoMostrado && isShowing()) {
      avisoMostrado = true;
      JOptionPane.showMessageDialog(this, "El período de prueba de 30 minutos finalizó. Podés seguir "
          + "consultando el catálogo, pero no crear, editar ni eliminar documentos.",
          "Demo expirada", JOptionPane.INFORMATION_MESSAGE);
    }
    return !expirada;
  }

  private static JPanel panel(LayoutManager layout) {
    JPanel panel = new JPanel(layout);
    panel.setOpaque(false);
    return panel;
  }
  private static JLabel etiqueta(String nombre, boolean titulo) {
    JLabel etiqueta = new JLabel(nombre);
    etiqueta.setFont(titulo ? TITULO : NORMAL);
    etiqueta.setForeground(titulo ? GRAFITO : new Color(0x6E6A67));
    return etiqueta;
  }
  private static JTextArea texto(String valor, int filas) {
    JTextArea area = new JTextArea(valor, filas, 0);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setEditable(false);
    area.setOpaque(false);
    area.setFont(NORMAL);
    area.setForeground(GRAFITO);
    return area;
  }
  private static JPanel superficie(LayoutManager layout) {
    JPanel panel = new JPanel(layout);
    panel.setBackground(Color.WHITE);
    panel.setBorder(createCompoundBorder(createLineBorder(new Color(0xE7DDD2)), createEmptyBorder(28, 28, 28, 28)));
    return panel;
  }
  private static JScrollPane desplazar(JComponent contenido, boolean horizontal) {
    JPanel marco = panel(new BorderLayout());
    marco.add(contenido, NORTH);
    JScrollPane scroll = new JScrollPane(marco);
    if (!horizontal) scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    return scroll;
  }
  private JPanel acciones(JButton... botones) {
    JPanel barra = panel(new FlowLayout());
    for (JButton boton : botones) barra.add(boton);
    return barra;
  }
  private JButton boton(String nombre, boolean escritura, Runnable accion) {
    JButton boton = new JButton(nombre);
    if (!nombre.equals("Cancelar")) {
      boton.setBackground(new Color(0xE97862));
      boton.setForeground(GRAFITO);
      boton.setFont(new Font("SansSerif", Font.BOLD, 14));
      boton.setBorder(createEmptyBorder(10, 16, 10, 16));
      boton.setFocusPainted(false);
      boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    boton.setEnabled(!escritura || !expirada);
    boton.addActionListener(e -> {
      try {
        if (!escritura || comprobarDemo()) accion.run();
      } catch (IllegalArgumentException error) {
        estado("Revisá los datos: " + error.getMessage(), true);
      } catch (IllegalStateException error) {
        estado(error.getMessage(), true);
      }
    });
    return boton;
  }
  private void estado(String aviso, boolean error) {
    mensaje.setText(aviso);
    mensaje.setForeground(error ? new Color(0xC95745) : GRAFITO);
    pantalla.revalidate();
    pantalla.repaint();
  }

  private void mostrarCatalogo(String aviso) {
    pantalla.removeAll();
    JPanel cabecera = panel(new BorderLayout(12, 0));
    cabecera.add(etiqueta("Catálogo", true), WEST);
    if (expirada) cabecera.add(etiqueta("Demo expirada: solo podés consultar el catálogo.", false), CENTER);
    JButton nuevo = boton("Nuevo documento", true, () -> mostrarFicha(null, true, ""));
    cabecera.add(acciones(nuevo, boton("Actualizar catálogo", false, () -> mostrarCatalogo(""))), EAST);
    pantalla.add(cabecera, NORTH);
    try {
      var documentos = catalogo.leerCatalogo().values();
      if (documentos.isEmpty()) {
        pantalla.add(texto("El catálogo está vacío. Agregá el primer documento con «Nuevo documento».", 1));
      } else {
        JPanel lista = panel(new GridLayout(0, 1, 0, 12));
        for (Documento doc : documentos) {
          JPanel tarjeta = superficie(new BorderLayout(16, 8));
          tarjeta.add(etiqueta(doc._tipo().toString(), false), NORTH);
          tarjeta.add(texto(doc._titulo(), 2), CENTER);
          tarjeta.add(boton("Ver detalle", false, () -> mostrarFicha(doc._id(), false, "")), EAST);
          lista.add(tarjeta);
        }
        JScrollPane scroll = desplazar(lista, false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        pantalla.add(scroll);
      }
      estado(aviso.isEmpty() ? documentos.size() + " documentos cargados." : aviso, false);
    } catch (IllegalStateException error) {
      boolean ausente = error.getCause() instanceof NoSuchFileException;
      nuevo.setEnabled(ausente && !expirada);
      pantalla.add(etiqueta(ausente ? "Catálogo no disponible" : "Archivo de catálogo inválido o ilegible", true));
      String causa = ausente ? "No se encontró el catálogo. Podés crear el primer documento para inicializarlo."
          : error.getMessage();
      estado(aviso.isEmpty() ? causa : aviso + " " + causa, true);
    }
  }

  private void mostrarFicha(UUID id, boolean editar, String aviso) {
    Documento doc;
    try {
      doc = id == null ? null : catalogo.buscarPorId(id);
    } catch (IllegalStateException error) {
      mostrarCatalogo("");
      estado((aviso.isEmpty() ? "" : aviso + " No se pudo actualizar el detalle. ") + error.getMessage(), true);
      return;
    }
    pantalla.removeAll();
    String titulo = id == null ? "Nuevo documento" : "Editar documento";
    pantalla.add(etiqueta(editar ? titulo : "Detalle del documento", true), NORTH);
    JPanel campos = editar ? panel(new GridBagLayout()) : superficie(new GridBagLayout());
    JPanel cuerpo = panel(new BorderLayout(0, 12));
    cuerpo.add(campos);
    TipoDocumento tipoActual = doc == null ? TipoDocumento.LIBRO : doc._tipo();
    JComboBox<TipoDocumento> tipo = new JComboBox<>(TipoDocumento.values());
    tipo.setSelectedItem(tipoActual);
    if (editar) fila(campos, "Tipo *", tipo, "tipo");
    else lectura(campos, "Tipo", tipoActual, 1);
    JTextComponent nombre = campo(campos, "Título", " *", "titulo", doc == null ? null : doc._titulo(), 2, editar);
    JTextComponent descripcion = campo(campos, "Descripción", "", "descripcion", doc == null ? null : doc._descripcion(), 5, editar);
    JTextComponent genero = campo(campos, "Género", "", "genero", doc == null ? null : doc._genero(), 1, editar);
    JTextComponent valoracion = campo(campos, "Valoración", " (1–5)", "valoracion", doc == null ? null : doc._valoracion(), 1, editar);
    JTextComponent paginas = campo(campos, "Páginas", " (1–999)", "paginas", doc == null ? null : doc._paginas(), 1, editar);
    if (editar) {
      cuerpo.add(etiqueta("* Obligatorio. Los demás campos pueden quedar vacíos.", false), NORTH);
      JButton guardar = boton("Guardar", true, () -> {
        Documento guardado = Documento.desdeFormulario(id == null ? UUID.randomUUID() : id,
            (TipoDocumento) tipo.getSelectedItem(), nombre.getText(), descripcion.getText(), genero.getText(),
            valoracion.getText(), paginas.getText());
        catalogo.guardarDocumento(guardado, id == null);
        mostrarFicha(guardado._id(), false, "Documento guardado correctamente.");
      });
      pantalla.add(acciones(guardar, boton("Cancelar", false, () -> {
        if (id == null) mostrarCatalogo(""); else mostrarFicha(id, false, "");
      })), SOUTH);
    } else {
      pantalla.add(acciones(boton("Volver al catálogo", false, () -> mostrarCatalogo("")),
          boton("Editar", true, () -> mostrarFicha(id, true, "")), boton("Eliminar", true, () -> eliminar(id))), SOUTH);
    }
    pantalla.add(desplazar(cuerpo, editar), CENTER);
    estado(aviso, false);
  }

  private static JTextArea lectura(JPanel campos, String nombre, Object valor, int filas) {
    JTextArea area = texto(Objects.toString(valor, "Sin especificar"), filas);
    area.getAccessibleContext().setAccessibleName(nombre);
    JScrollPane scroll = new JScrollPane(area);
    scroll.setBorder(createEmptyBorder());
    fila(campos, nombre, scroll, null);
    return area;
  }
  private static JTextComponent campo(JPanel campos, String nombre, String indicacion, String clave,
                                      Object valor, int filas, boolean editar) {
    if (!editar) return lectura(campos, nombre, valor, filas);
    String contenido = Objects.toString(valor, "");
    if (clave.equals("descripcion")) {
      JTextArea area = new JTextArea(contenido, filas, 30);
      area.setCaretPosition(0);
      area.setLineWrap(true);
      area.setWrapStyleWord(true);
      area.setName(clave);
      area.getAccessibleContext().setAccessibleName(nombre);
      fila(campos, nombre, new JScrollPane(area), "descripcionScroll");
      return area;
    }
    JTextField entrada = new JTextField(contenido, clave.equals("valoracion") || clave.equals("paginas") ? 8 : 30);
    fila(campos, nombre + indicacion, entrada, clave);
    return entrada;
  }
  private static void fila(JPanel campos, String nombre, JComponent control, String clave) {
    GridBagConstraints posicion = new GridBagConstraints();
    posicion.gridy = campos.getComponentCount() / 2;
    posicion.anchor = GridBagConstraints.NORTHWEST;
    posicion.insets = new Insets(6, 8, 6, 8);
    JLabel etiqueta = clave == null ? etiqueta(nombre, false) : new JLabel(nombre);
    etiqueta.setLabelFor(control);
    campos.add(etiqueta, posicion);
    posicion.gridx = 1;
    posicion.weightx = 1;
    posicion.fill = GridBagConstraints.HORIZONTAL;
    control.setName(clave);
    campos.add(control, posicion);
  }
  private void eliminar(UUID id) {
    String titulo = catalogo.buscarPorId(id)._titulo();
    JTextArea pregunta = texto("¿Eliminar «" + titulo + "»? Esta acción no se puede deshacer.", 4);
    pregunta.setColumns(45);
    int respuesta = JOptionPane.showOptionDialog(this, new JScrollPane(pregunta), "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Eliminar", "Cancelar"}, "Cancelar");
    if (respuesta == JOptionPane.YES_OPTION && comprobarDemo()) {
      catalogo.eliminar(id);
      mostrarCatalogo("Documento eliminado correctamente.");
    }
  }
}
