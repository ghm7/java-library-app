package com.example.library.ui;

import com.example.library.service.ServicioDocumento;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class VentanaPrincipal extends JFrame {
  public VentanaPrincipal() {
    this(new ServicioDocumento());
  }

  public VentanaPrincipal(ServicioDocumento servicio) {
    super("PaperTrail");

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(1080, 680));
    setSize(1600, 900);
    setLocationRelativeTo(null);

    JPanel raiz = new JPanel(new BorderLayout(0, 0));
    raiz.setBackground(TemaGrafico.CREMA_SUAVE);
    raiz.setFocusable(true);
    setContentPane(raiz);

    raiz.add(_crearBarraLateral(), BorderLayout.WEST);
    raiz.add(_crearContenido(servicio), BorderLayout.CENTER);

    SwingUtilities.invokeLater(raiz::requestFocusInWindow);
  }

  private JPanel _crearContenido(ServicioDocumento servicio) {
    JPanel contenido = new JPanel(new BorderLayout(0, 16));
    contenido.setOpaque(false);
    contenido.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
    contenido.add(_crearBarraSuperior(), BorderLayout.NORTH);

    PanelRedondeado superficie = new PanelRedondeado(TemaGrafico.MARFIL, 26);
    superficie.setLayout(new BorderLayout());
    superficie.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TemaGrafico.BORDE_SUAVE),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
    superficie.add(new PanelDocumentos(servicio), BorderLayout.CENTER);
    contenido.add(superficie, BorderLayout.CENTER);
    return contenido;
  }

  private JPanel _crearBarraSuperior() {
    JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
    barra.setOpaque(false);

    barra.add(_crearBusqueda());
    barra.add(new BotonCapsula("Todos", TemaGrafico.CORAL_CLARO));
    barra.add(new BotonCapsula("Ciencia ficción", new Color(0xFCE9D9)));
    barra.add(new BotonCapsula("Fantasía", new Color(0xEFE5F2)));
    barra.add(new BotonCapsula("Ficción histórica", new Color(0xF7E9D6)));
    barra.add(new BotonCapsula("Misterio", new Color(0xEAE2F1)));
    barra.add(new BotonCapsula("Romance", new Color(0xF9E4E0)));
    barra.add(new BotonCapsula("Suspense", new Color(0xE5EDE4)));
    barra.add(new BotonCapsula("Terror", new Color(0xEADFE4)));

    return barra;
  }

  private JPanel _crearBusqueda() {
    PanelRedondeado contenedor = new PanelRedondeado(TemaGrafico.MARFIL, 50);
    contenedor.setLayout(new BorderLayout(10, 0));
    contenedor.setPreferredSize(new Dimension(355, 48));
    contenedor.setBorder(BorderFactory.createEmptyBorder(0, 17, 0, 17));

    JLabel icono = new JLabel("⌕");
    icono.setFont(new Font("SansSerif", Font.PLAIN, 29));
    icono.setForeground(TemaGrafico.TEXTO_SECUNDARIO);
    contenedor.add(icono, BorderLayout.WEST);

    JTextField busqueda = new CampoTextoConElipsis("Buscar libros, autores o una nueva aventura.");
    busqueda.setName("busqueda");
    busqueda.setFont(TemaGrafico.SUBTITULO);
    busqueda.setForeground(TemaGrafico.TEXTO_SECUNDARIO);
    busqueda.setBackground(TemaGrafico.MARFIL);
    busqueda.setBorder(BorderFactory.createEmptyBorder());
    busqueda.setCaretColor(TemaGrafico.TEXTO_SECUNDARIO);
    contenedor.add(busqueda, BorderLayout.CENTER);
    return contenedor;
  }

  private static class CampoTextoConElipsis extends JTextField {
    private static final String ELIPSIS = "...";
    private final String marcador;

    CampoTextoConElipsis(String marcador) {
      this.marcador = marcador;
    }

    @Override
    protected void paintComponent(Graphics graficos) {
      super.paintComponent(graficos);

      String texto = getText();
      boolean muestraMarcador = texto.isEmpty();

      if (hasFocus() && !muestraMarcador) {
        return;
      }

      Insets margenes = getInsets();
      int anchoDisponible = getWidth() - margenes.left - margenes.right;
      FontMetrics metricas = getFontMetrics(getFont());
      String textoOriginal = muestraMarcador ? marcador : texto;
      String textoVisible = _conElipsis(textoOriginal, metricas, anchoDisponible);

      if (!muestraMarcador && textoVisible.equals(textoOriginal)) {
        return;
      }

      Graphics copia = graficos.create();

      try {
        copia.setColor(getBackground());
        copia.fillRect(
            margenes.left,
            margenes.top,
            Math.max(0, anchoDisponible),
            Math.max(0, getHeight() - margenes.top - margenes.bottom));
        copia.setColor(getForeground());
        int altoDisponible = getHeight() - margenes.top - margenes.bottom;
        int lineaBase =
            margenes.top
                + Math.max(0, (altoDisponible - metricas.getHeight()) / 2)
                + metricas.getAscent();
        copia.drawString(textoVisible, margenes.left, lineaBase);

        if (muestraMarcador && hasFocus()) {
          getCaret().paint(copia);
        }

      } finally {
        copia.dispose();
      }
    }

    private String _conElipsis(String texto, FontMetrics metricas, int anchoDisponible) {
      if (metricas.stringWidth(texto) <= anchoDisponible) {
        return texto;
      }
      if (metricas.stringWidth(ELIPSIS) > anchoDisponible) {
        return "";
      }

      StringBuilder resultado = new StringBuilder();
      for (int indice = 0; indice < texto.length(); ) {
        int puntoCodigo = texto.codePointAt(indice);
        String siguiente =
            resultado.toString() + new String(Character.toChars(puntoCodigo)) + ELIPSIS;
        if (metricas.stringWidth(siguiente) > anchoDisponible) {
          break;
        }
        resultado.appendCodePoint(puntoCodigo);
        indice += Character.charCount(puntoCodigo);
      }
      return resultado + ELIPSIS;
    }
  }

  private JPanel _crearBarraLateral() {
    JPanel lateral = new JPanel(new GridBagLayout());
    lateral.setBackground(TemaGrafico.MARFIL);
    lateral.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, TemaGrafico.BORDE_SUAVE));
    lateral.setPreferredSize(new Dimension(205, 0));

    GridBagConstraints restricciones = new GridBagConstraints();
    restricciones.gridx = 0;
    restricciones.weightx = 1;
    restricciones.anchor = GridBagConstraints.CENTER;

    JPanel marca = new JPanel(new BorderLayout(8, 0));
    marca.setOpaque(false);
    JLabel hoja = new JLabel("♧");
    hoja.setFont(new Font("Serif", Font.PLAIN, 42));
    hoja.setForeground(new Color(0x587F3B));
    marca.add(hoja, BorderLayout.WEST);

    JPanel textos = new JPanel(new BorderLayout(0, 2));
    textos.setOpaque(false);
    JLabel nombre = new JLabel("PaperTrail");
    nombre.setFont(TemaGrafico.MARCA);
    nombre.setForeground(TemaGrafico.GRAFITO);
    textos.add(nombre, BorderLayout.NORTH);
    JLabel lema = new JLabel("<html>Libros para un mañana<br>mejor</html>");
    lema.setFont(new Font("SansSerif", Font.PLAIN, 12));
    lema.setForeground(TemaGrafico.TEXTO_SECUNDARIO);
    textos.add(lema, BorderLayout.CENTER);
    marca.add(textos, BorderLayout.CENTER);

    restricciones.gridy = 0;
    restricciones.insets = new Insets(24, 18, 0, 18);
    lateral.add(marca, restricciones);

    JLabel espacioImagen = new JLabel();
    espacioImagen.setName("espacioImagenLateral");
    espacioImagen.setPreferredSize(new Dimension(180, 310));
    restricciones.gridy = 1;
    restricciones.weighty = 1;
    restricciones.fill = GridBagConstraints.BOTH;
    restricciones.insets = new Insets(0, 10, 0, 10);
    lateral.add(espacioImagen, restricciones);

    JLabel cita =
        new JLabel(
            "<html><div style='text-align:center;'>Los buenos libros<br>hacen de vos alguien<br>más"
                + " amable y brillante.</div></html>");
    cita.setFont(new Font("SansSerif", Font.PLAIN, 14));
    cita.setForeground(TemaGrafico.TEXTO_SECUNDARIO);
    restricciones.gridy = 2;
    restricciones.weighty = 0;
    restricciones.fill = GridBagConstraints.NONE;
    restricciones.insets = new Insets(0, 12, 8, 12);
    lateral.add(cita, restricciones);

    JLabel corazon = new JLabel("♥");
    corazon.setFont(new Font("SansSerif", Font.PLAIN, 19));
    corazon.setForeground(TemaGrafico.CORAL);
    restricciones.gridy = 3;
    restricciones.insets = new Insets(0, 12, 28, 12);
    lateral.add(corazon, restricciones);

    return lateral;
  }
}
