package com.example.library.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.example.library.domain.Documento;
import com.example.library.domain.ValidarExcepcion;
import com.example.library.service.EstadoCargaCatalogo;
import com.example.library.service.ResultadoCargaCatalogo;
import com.example.library.service.ServicioCargaCatalogo;
import com.example.library.service.ServicioDocumento;
import com.example.library.service.ServicioDocumentoExcepcion;

/** Presentación y acciones Swing; todo acceso a datos pasa por el servicio. */
public class PanelDocumentos extends JPanel {
    private static final String CATALOGO = "catalogo";
    private static final String DETALLE = "detalle";
    private static final String FORMULARIO = "formulario";

    private final ServicioDocumento _servicio;
    private final ServicioCargaCatalogo _carga;
    private final CardLayout _cards = new CardLayout();
    private final JPanel _contenido = new JPanel(_cards);
    private final JPanel _catalogo = new JPanel(new BorderLayout(0, 12));
    private final JPanel _detalle = new JPanel(new BorderLayout(0, 12));
    private final FormularioDocumento _formulario;
    private final JTextArea _mensaje = Interfaz.texto("");
    private final Predicate<Documento> _confirmarEliminacion;

    public PanelDocumentos(ServicioDocumento servicio) {
        this(servicio, null);
    }

    PanelDocumentos(ServicioDocumento servicio, Predicate<Documento> confirmarEliminacion) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio es obligatorio.");
        }
        _servicio = servicio;
        _carga = new ServicioCargaCatalogo(servicio);
        _confirmarEliminacion = confirmarEliminacion == null
            ? this::_confirmarEliminacion
            : confirmarEliminacion;
        _formulario = new FormularioDocumento(this::_guardar, this::_cancelarFormulario);

        _configurarPanel();
        _mostrarCatalogo("");
    }

    private void _configurarPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(TemaGrafico.CREMA);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        _detalle.setOpaque(false);
        _catalogo.setOpaque(false);

        _contenido.setOpaque(false);
        _contenido.add(_catalogo, CATALOGO);
        _contenido.add(_detalle, DETALLE);
        _contenido.add(_formulario, FORMULARIO);

        _mensaje.setName("mensaje");
        _mensaje.getAccessibleContext().setAccessibleName("Estado de la operación");

        add(_contenido, BorderLayout.CENTER);
        add(_mensaje, BorderLayout.SOUTH);
    }

    private JButton _boton(String texto, Runnable accion) {
        JButton boton = Interfaz.botonPrimario(texto);
        boton.addActionListener(e -> accion.run());

        return boton;
    }

    private boolean _confirmarEliminacion(Documento documento) {
        JTextArea pregunta = Interfaz.texto(
            "¿Eliminar «" + documento.getTitulo() + "»? Esta acción no se puede deshacer.");
        pregunta.setColumns(45);
        pregunta.setRows(4);

        int opcion = JOptionPane.showOptionDialog(
            this,
            new JScrollPane(pregunta),
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            new String[] { "Eliminar", "Cancelar" },
            "Cancelar");
        return opcion == JOptionPane.YES_OPTION;
    }

    private void _mensaje(String texto, boolean error) {
        _mensaje.setForeground(error ? TemaGrafico.CORAL_OSCURO : TemaGrafico.GRAFITO);
        _mensaje.setText(texto);
    }

    private void _mostrarCatalogo(String aviso) {
        ResultadoCargaCatalogo resultado = _carga.cargarCatalogo();
        _catalogo.removeAll();
        _catalogo.add(_crearCabeceraCatalogo(resultado), BorderLayout.NORTH);

        if (resultado.getEstado() == EstadoCargaCatalogo.CARGADO) {
            _mostrarCatalogoCargado(resultado, aviso);
        } else {
            _mostrarErrorCatalogo(resultado, aviso);
        }

        _mostrarTarjeta(CATALOGO);
    }

    private JPanel _crearCabeceraCatalogo(ResultadoCargaCatalogo resultado) {
        JPanel cabecera = new JPanel(new BorderLayout(12, 0));
        cabecera.setOpaque(false);
        cabecera.add(Interfaz.titulo("Catálogo"), BorderLayout.WEST);

        JButton crear = _boton("Nuevo documento", () -> _abrirFormulario(null));
        crear.setEnabled(resultado.getEstado() == EstadoCargaCatalogo.CARGADO);
        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.add(crear);
        acciones.add(_boton("Actualizar catálogo", () -> _mostrarCatalogo("")));
        cabecera.add(acciones, BorderLayout.EAST);

        return cabecera;
    }

    private void _mostrarErrorCatalogo(ResultadoCargaCatalogo resultado, String aviso) {
        String titulo = resultado.getEstado() == EstadoCargaCatalogo.AUSENTE
            ? "Catálogo no disponible"
            : "Archivo de catálogo inválido o ilegible";
        _catalogo.add(Interfaz.titulo(titulo), BorderLayout.CENTER);
        _mensaje((aviso.isEmpty() ? "" : aviso + " ") + resultado.getMensaje(), true);
    }

    private void _mostrarCatalogoCargado(ResultadoCargaCatalogo resultado, String aviso) {
        List<Documento> documentos = resultado.getDocumentos();
        if (documentos.isEmpty()) {
            _catalogo.add(
                Interfaz.texto("El catálogo está vacío. Agregá el primer documento con «Nuevo documento»."),
                BorderLayout.CENTER);
        } else {
            _catalogo.add(_crearListaDocumentos(documentos), BorderLayout.CENTER);
        }
        _mensaje(aviso.isEmpty() ? resultado.getMensaje() : aviso, false);
    }

    private JScrollPane _crearListaDocumentos(List<Documento> documentos) {
        JPanel tarjetas = new JPanel(new GridLayout(0, 1, 0, 12));
        tarjetas.setOpaque(false);
        for (Documento documento : documentos) {
            tarjetas.add(_crearTarjetaDocumento(documento));
        }

        JPanel lista = new JPanel(new BorderLayout());
        lista.setOpaque(false);
        lista.add(tarjetas, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JPanel _crearTarjetaDocumento(Documento documento) {
        JPanel tarjeta = Interfaz.superficie();
        tarjeta.setLayout(new BorderLayout(16, 8));
        tarjeta.add(
            Interfaz.subtitulo(Interfaz.nombreTipo(documento.getTipo())),
            BorderLayout.NORTH);

        JTextArea titulo = Interfaz.texto(documento.getTitulo());
        titulo.setRows(2);
        tarjeta.add(titulo, BorderLayout.CENTER);
        tarjeta.add(
            _boton("Ver detalle", () -> _mostrarDetalle(documento.getId(), "")),
            BorderLayout.EAST);
        return tarjeta;
    }

    private void _mostrarTarjeta(String nombre) {
        _cards.show(_contenido, nombre);
        revalidate();
        repaint();
    }

    private void _mostrarDetalle(UUID id, String aviso) {
        try {
            Documento documento = _servicio.consultar(id);
            _construirDetalle(documento);
            _mensaje(aviso, false);
            _mostrarTarjeta(DETALLE);
        } catch (ServicioDocumentoExcepcion e) {
            _mostrarCatalogo("");
            String mensaje = aviso.isEmpty()
                ? e.getMessage()
                : aviso + " No se pudo actualizar el detalle. " + e.getMessage();
            _mensaje(mensaje, true);
        }
    }

    private void _construirDetalle(Documento documento) {
        _detalle.removeAll();
        _detalle.add(Interfaz.titulo("Detalle del documento"), BorderLayout.NORTH);
        _detalle.add(_crearCamposDetalle(documento), BorderLayout.CENTER);
        _detalle.add(_crearAccionesDetalle(documento.getId()), BorderLayout.SOUTH);
    }

    private JScrollPane _crearCamposDetalle(Documento documento) {
        JPanel campos = Interfaz.superficie();
        campos.setLayout(new GridBagLayout());
        _agregarDetalle(campos, "Tipo", Interfaz.nombreTipo(documento.getTipo()));
        _agregarDetalle(campos, "Título", documento.getTitulo());
        _agregarDetalle(campos, "Descripción", documento.getDescripcion());
        _agregarDetalle(campos, "Género", documento.getGenero());
        _agregarDetalle(campos, "Valoración", documento.getValoracion());
        _agregarDetalle(campos, "Páginas", documento.getPaginas());

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        cuerpo.add(campos, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(cuerpo);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JPanel _crearAccionesDetalle(UUID id) {
        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.add(_boton("Volver al catálogo", () -> _mostrarCatalogo("")));
        acciones.add(_boton("Editar", () -> _abrirFormulario(id)));
        acciones.add(_boton("Eliminar", () -> _eliminar(id)));
        return acciones;
    }

    private void _agregarDetalle(JPanel campos, String nombre, Object valor) {
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.gridy = campos.getComponentCount() / 2;
        posicion.anchor = GridBagConstraints.NORTHWEST;
        posicion.insets = new Insets(6, 8, 6, 8);
        campos.add(Interfaz.subtitulo(nombre), posicion);
        JTextArea texto = Interfaz.texto(valor == null ? "Sin especificar" : valor.toString());
        texto.getAccessibleContext().setAccessibleName(nombre);
        int filas = switch (nombre) {
            case "Descripción" -> 5;
            case "Título" -> 2;
            default -> 1;
        };
        texto.setRows(filas);
        posicion.gridx = 1;
        posicion.weightx = 1;
        posicion.fill = GridBagConstraints.HORIZONTAL;
        JScrollPane scroll = new JScrollPane(texto);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        campos.add(scroll, posicion);
    }

    private void _abrirFormulario(UUID id) {
        try {
            _formulario.cargar(id == null ? null : _servicio.consultar(id));
            _mensaje("", false);
            _mostrarTarjeta(FORMULARIO);
        } catch (ServicioDocumentoExcepcion e) {
            _mostrarCatalogo("");
            _mensaje(e.getMessage(), true);
        }
    }

    private void _cancelarFormulario() {
        if (_formulario.getId() == null) {
            _mostrarCatalogo("");
        } else {
            _mostrarDetalle(_formulario.getId(), "");
        }
    }

    private void _guardar(FormularioDocumento formulario) {
        try {
            Documento guardado = _guardarDocumento(formulario);
            _mostrarDetalle(guardado.getId(), "Documento guardado correctamente.");
        } catch (ValidarExcepcion e) {
            _mensaje("Revisá los datos: " + e.getMessage(), true);
        } catch (ServicioDocumentoExcepcion e) {
            _mensaje(e.getMessage(), true);
        }
    }

    private Documento _guardarDocumento(FormularioDocumento formulario) {
        if (formulario.getId() == null) {
            return _servicio.crear(
                formulario.getTipo(), formulario.getTitulo(), formulario.getDescripcion(),
                formulario.getGenero(), formulario.getValoracion(), formulario.getPaginas());
        }

        return _servicio.actualizar(
            formulario.getId(), formulario.getTipo(), formulario.getTitulo(),
            formulario.getDescripcion(), formulario.getGenero(),
            formulario.getValoracion(), formulario.getPaginas());
    }

    private void _eliminar(UUID id) {
        try {
            Documento documento = _servicio.consultar(id);
            if (!_confirmarEliminacion.test(documento)) {
                return;
            }
            _servicio.eliminar(id);
            _mostrarCatalogo("Documento eliminado correctamente.");
        } catch (ServicioDocumentoExcepcion e) {
            _mensaje(e.getMessage(), true);
        }
    }
}
