package com.example.library.ui;

import com.example.library.domain.Documento;
import com.example.library.domain.TipoDocumento;
import com.example.library.domain.ValidadorDocumento;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.UUID;
import java.util.function.Consumer;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Formulario para crear y editar documentos. */
public class FormularioDocumento extends JPanel {
  private final JComboBox<TipoDocumento> _tipo = new JComboBox<>(TipoDocumento.values());
  private final JTextField _titulo = new JTextField(30);
  private final JTextArea _descripcion = new JTextArea(5, 30);
  private final JTextField _genero = new JTextField(30);
  private final JTextField _valoracion = new JTextField(8);
  private final JTextField _paginas = new JTextField(8);
  private final JLabel _encabezado = Interfaz.titulo("");
  private final JButton _guardarBoton = Interfaz.botonPrimario("Guardar");
  private UUID _id;

  public FormularioDocumento(Consumer<FormularioDocumento> guardar, Runnable cancelar) {
    setLayout(new BorderLayout(0, 12));
    setOpaque(false);
    add(_encabezado, BorderLayout.NORTH);

    _configurarCampos();
    add(_crearCuerpo(), BorderLayout.CENTER);
    add(_crearAcciones(guardar, cancelar), BorderLayout.SOUTH);
  }

  private void _configurarCampos() {
    DefaultListCellRenderer renderer = new DefaultListCellRenderer();
    _tipo.setRenderer(
        (lista, valor, indice, seleccionado, foco) -> {
          String nombre = valor == null ? "" : Interfaz.nombreTipo(valor);
          return renderer.getListCellRendererComponent(lista, nombre, indice, seleccionado, foco);
        });

    _descripcion.setLineWrap(true);
    _descripcion.setWrapStyleWord(true);
    _descripcion.setName("descripcion");
    _descripcion.getAccessibleContext().setAccessibleName("Descripción");
  }

  private JScrollPane _crearCuerpo() {
    JPanel campos = new JPanel(new GridBagLayout());
    campos.setOpaque(false);

    _agregarCampo(campos, 0, "Tipo *", _tipo, "tipo");
    _agregarCampo(campos, 1, "Título *", _titulo, "titulo");
    _agregarCampo(campos, 2, "Descripción", new JScrollPane(_descripcion), "descripcionScroll");
    _agregarCampo(campos, 3, "Género", _genero, "genero");
    _agregarCampo(campos, 4, "Valoración (1–5)", _valoracion, "valoracion");
    _agregarCampo(campos, 5, "Páginas (1–999)", _paginas, "paginas");

    JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
    cuerpo.setOpaque(false);
    cuerpo.add(
        Interfaz.subtitulo("* Obligatorio. Los demás campos pueden quedar vacíos."),
        BorderLayout.NORTH);
    cuerpo.add(campos, BorderLayout.CENTER);

    JPanel contenedor = new JPanel(new BorderLayout());
    contenedor.setOpaque(false);
    contenedor.add(cuerpo, BorderLayout.NORTH);

    return new JScrollPane(contenedor);
  }

  private JPanel _crearAcciones(Consumer<FormularioDocumento> guardar, Runnable cancelar) {
    JPanel acciones = new JPanel();
    acciones.setOpaque(false);

    _guardarBoton.addActionListener(e -> guardar.accept(this));
    JButton cancelarBoton = new JButton("Cancelar");
    cancelarBoton.addActionListener(e -> cancelar.run());

    acciones.add(_guardarBoton);
    acciones.add(cancelarBoton);

    return acciones;
  }

  private void _agregarCampo(
      JPanel campos, int fila, String nombre, JComponent campo, String identificador) {
    GridBagConstraints restricciones = new GridBagConstraints();
    restricciones.gridy = fila;
    restricciones.insets = new Insets(6, 8, 6, 8);
    restricciones.anchor = GridBagConstraints.NORTHWEST;
    JLabel etiqueta = new JLabel(nombre);
    etiqueta.setLabelFor(campo);
    campos.add(etiqueta, restricciones);
    restricciones.gridx = 1;
    restricciones.weightx = 1;
    restricciones.fill = GridBagConstraints.HORIZONTAL;
    campo.setName(identificador);
    campos.add(campo, restricciones);
  }

  public void cargar(Documento documento) {
    if (documento == null) {
      _id = null;
      _encabezado.setText("Nuevo documento");
      _tipo.setSelectedItem(TipoDocumento.LIBRO);
      _titulo.setText("");
      _descripcion.setText("");
      _genero.setText("");
      _valoracion.setText("");
      _paginas.setText("");
    } else {
      _id = documento.getId();
      _encabezado.setText("Editar documento");
      _tipo.setSelectedItem(documento.getTipo());
      _titulo.setText(documento.getTitulo());
      _descripcion.setText(documento.getDescripcion());
      _genero.setText(documento.getGenero());
      _valoracion.setText(_texto(documento.getValoracion()));
      _paginas.setText(_texto(documento.getPaginas()));
    }
    _descripcion.setCaretPosition(0);
  }

  private String _texto(Integer valor) {
    return valor == null ? "" : valor.toString();
  }

  public UUID getId() {
    return _id;
  }

  public void setEscrituraHabilitada(boolean habilitada) {
    _guardarBoton.setEnabled(habilitada);
  }

  public TipoDocumento getTipo() {
    return (TipoDocumento) _tipo.getSelectedItem();
  }

  public String getTitulo() {
    return _titulo.getText();
  }

  public String getDescripcion() {
    return _descripcion.getText();
  }

  public String getGenero() {
    return _genero.getText();
  }

  public Integer getValoracion() {
    return ValidadorDocumento.parsearValoracion(_valoracion.getText());
  }

  public Integer getPaginas() {
    return ValidadorDocumento.parsearPaginas(_paginas.getText());
  }
}
