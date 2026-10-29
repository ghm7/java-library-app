package com.example.library.ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VentanaPrincipal extends JFrame {
    private static String _CATALOGO = "catalogo";
    private static String _DETALLE = "detalle";
    private static String _FORMULARIO = "formulario";
    private static String _EXPIRADO = "expirado";

    private CardLayout _cards = new CardLayout();
    private JPanel _content = new JPanel(_cards);

    public VentanaPrincipal() {
        super("Biblioteca Java");
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
        return Interfaz.superficie();
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
