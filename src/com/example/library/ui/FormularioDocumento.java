package com.example.library.ui;

import com.example.library.domain.Documento;
import com.example.library.domain.TipoDocumento;
import com.example.library.domain.ValidadorDocumento;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.Objects;
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
    JPanel campos = Interfaz.panel(new GridBagLayout());

    _agregarCampo(campos, "Tipo *", _tipo, "tipo");
    _agregarCampo(campos, "Título *", _titulo, "titulo");
    _agregarCampo(campos, "Descripción", new JScrollPane(_descripcion), "descripcionScroll");
    _agregarCampo(campos, "Género", _genero, "genero");
    _agregarCampo(campos, "Valoración (1–5)", _valoracion, "valoracion");
    _agregarCampo(campos, "Páginas (1–999)", _paginas, "paginas");

    JPanel cuerpo = Interfaz.panel(new BorderLayout(0, 12));
    cuerpo.add(
        Interfaz.subtitulo("* Obligatorio. Los demás campos pueden quedar vacíos."),
        BorderLayout.NORTH);
    cuerpo.add(campos, BorderLayout.CENTER);

    return Interfaz.conDesplazamiento(cuerpo);
  }

  private JPanel _crearAcciones(Consumer<FormularioDocumento> guardar, Runnable cancelar) {
    JPanel acciones = Interfaz.panel(new FlowLayout());

    _guardarBoton.addActionListener(e -> guardar.accept(this));
    JButton cancelarBoton = new JButton("Cancelar");
    cancelarBoton.addActionListener(e -> cancelar.run());

    acciones.add(_guardarBoton);
    acciones.add(cancelarBoton);

    return acciones;
  }

  private void _agregarCampo(JPanel campos, String nombre, JComponent campo, String identificador) {
    campo.setName(identificador);
    Interfaz.agregarFila(campos, new JLabel(nombre), campo);
  }

  public void cargar(Documento documento) {
    boolean nuevo = documento == null;
    _id = nuevo ? null : documento.getId();
    _encabezado.setText(nuevo ? "Nuevo documento" : "Editar documento");
    _tipo.setSelectedItem(nuevo ? TipoDocumento.LIBRO : documento.getTipo());
    _titulo.setText(nuevo ? "" : documento.getTitulo());
    _descripcion.setText(nuevo ? "" : documento.getDescripcion());
    _genero.setText(nuevo ? "" : documento.getGenero());
    _valoracion.setText(nuevo ? "" : Objects.toString(documento.getValoracion(), ""));
    _paginas.setText(nuevo ? "" : Objects.toString(documento.getPaginas(), ""));
    _descripcion.setCaretPosition(0);
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
