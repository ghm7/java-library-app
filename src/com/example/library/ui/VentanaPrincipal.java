package com.example.library.ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.example.library.service.EstadoCargaCatalogo;
import com.example.library.service.ResultadoCargaCatalogo;

public class VentanaPrincipal extends JFrame {
    private static String _CATALOGO = "catalogo";
    private static String _DETALLE = "detalle";
    private static String _FORMULARIO = "formulario";
    private static String _EXPIRADO = "expirado";

    private CardLayout _cards = new CardLayout();
    private JPanel _content = new JPanel(_cards);
    private ResultadoCargaCatalogo _resultadoCarga;

    public VentanaPrincipal() {
        this(new ResultadoCargaCatalogo(
            EstadoCargaCatalogo.CARGADO,
            List.of(),
            ""
        ));
    }

    public VentanaPrincipal(ResultadoCargaCatalogo resultadoCarga) {
        super("Biblioteca Java");
        if (resultadoCarga == null) {
            throw new IllegalArgumentException(
                "El resultado de carga del catálogo es obligatorio."
            );
        }

        this._resultadoCarga = resultadoCarga;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1000, 680);
        setLocationRelativeTo(null);

        add(_crearCabecera(), BorderLayout.NORTH);

        _content.add(_crearCatalogo(), _CATALOGO);
        _content.add(_crearDetalle(), _DETALLE);
        _content.add(_crearFormulario(), _FORMULARIO);
        _content.add(_crearExpirado(), _EXPIRADO);
        add(_content, BorderLayout.CENTER);

        _cards.show(_content, _CATALOGO);
    }

    private JPanel _crearCabecera() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TemaGrafico.GRAFITO);
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel brand = new JLabel("Biblioteca Java");
        brand.setForeground(TemaGrafico.CREMA);
        brand.setFont(TemaGrafico.TITULO);
        header.add(brand, BorderLayout.WEST);
        
        return header;
    }

    private JPanel _crearCatalogo() {
        JPanel catalogo = Interfaz.superficie();
        catalogo.setLayout(new BorderLayout(0, 12));

        if (this._resultadoCarga.getEstado() == EstadoCargaCatalogo.CARGADO) {
            catalogo.add(Interfaz.titulo("Catálogo cargado"), BorderLayout.NORTH);
            catalogo.add(
                Interfaz.subtitulo(this._resultadoCarga.getMensaje()),
                BorderLayout.CENTER
            );
            return catalogo;
        }

        String titulo = this._resultadoCarga.getEstado()
            == EstadoCargaCatalogo.AUSENTE
            ? "Catálogo no disponible"
            : "No se pudo cargar el catálogo";

        catalogo.add(Interfaz.titulo(titulo), BorderLayout.NORTH);
        catalogo.add(
            Interfaz.subtitulo(this._resultadoCarga.getMensaje()),
            BorderLayout.CENTER
        );
        return catalogo;
    }

    private JPanel _crearDetalle() {
        return Interfaz.superficie();
    }

    private JPanel _crearFormulario() {
        return Interfaz.superficie();
    }

    private JPanel _crearExpirado() {
        return Interfaz.superficie();
    }
}
