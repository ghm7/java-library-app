package com.example.library.ui;

import com.example.library.domain.Documento;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/** Composición escalable de documentos, estantes y decoraciones del catálogo. */
public final class ComposicionEstantes extends JComponent implements Scrollable {
  private static final double ANCHO_DISENO = 1440.0;
  private static final double ALTO_DISENO_INICIAL = 900.0;
  private static final double ALTO_FILA_ADICIONAL = 275.0;
  private static final double ALTO_ESTANTE_ADICIONAL = 86.0;
  private static final double MARGEN_ESTANTE_ADICIONAL = 30.0;
  private static final double ESPACIO_TARJETAS_ADICIONALES = 10.0;
  private static final int PRIMERA_FILA_ADICIONAL = 4;
  private static final String BASE_RECURSOS = "/com/example/library/ui/assets/papertrail-shelf/";
  private static final String ESTANTE_FIJO = "shelves/02_cozy_honey_wooden_shelf.png";

  private static final Color FONDO_TARJETA = Color.WHITE;
  private static final Color BORDE_TARJETA = new Color(0xE7DDD2);

  private static final List<String> VARIANTES_ESTANTES =
      List.of(
          "shelves/01_warm_wooden_wall_shelf_with_central_bracket.png",
          ESTANTE_FIJO,
          "shelves/03_cozy_caramel_wooden_floating_shelf.png",
          "shelves/04_polished_oak_floating_shelf.png",
          "shelves/05_cozy_honeywood_floating_shelf.png",
          "shelves/06_cozy_maple_floating_shelf.png",
          "shelves/07_warm_honey_wooden_shelf.png",
          "shelves/08_cozy_painted_wooden_shelf.png",
          "shelves/10_warm_caramel_wooden_wall_shelf.png");

  private static final List<Posicion> ESTANTES_INICIALES =
      List.of(
          new Posicion(-100, 250, 1575, 86),
          new Posicion(-100, 515, 1575, 86),
          new Posicion(-100, 795, 1575, 75));

  private static final List<Posicion> POSICIONES_TARJETAS =
      List.of(
          new Posicion(135, 92, 126, 193),
          new Posicion(280, 98, 130, 187),
          new Posicion(429, 90, 126, 195),
          new Posicion(574, 96, 128, 189),
          new Posicion(721, 86, 130, 199),
          new Posicion(870, 90, 126, 195),
          new Posicion(1015, 88, 129, 197),
          new Posicion(1163, 94, 127, 191),
          new Posicion(100, 378, 125, 172),
          new Posicion(245, 378, 125, 172),
          new Posicion(390, 374, 126, 176),
          new Posicion(535, 378, 125, 172),
          new Posicion(818, 390, 94, 160),
          new Posicion(925, 390, 94, 160),
          new Posicion(1032, 390, 94, 160),
          new Posicion(1139, 390, 94, 160),
          new Posicion(130 - 75, 628, 125, 197),
          new Posicion(275 - 75, 628, 125, 197),
          new Posicion(680 - 150, 628, 125, 197),
          new Posicion(825 - 150, 628, 125, 197),
          new Posicion(970 - 150, 628, 125, 197),
          new Posicion(1115 - 150, 628, 125, 197),
          new Posicion(1260 - 150, 628, 125, 197)
        );

  private static final List<ImagenPosicionada> DECORACIONES =
      List.of(
          new ImagenPosicionada("03_miniature_house_top.png", 110, 223, 58 * 1.1, 63 * 1.1),
          new ImagenPosicionada("01_trailing_plant_terracotta.png", 4, 150, 105 * 1.5, 176 * 1.5),
          new ImagenPosicionada("02_tall_plant_cream_pot.png", 1270, 150, 76 * 1.25, 116 * 1.25),
          new ImagenPosicionada("06_small_plant_cream_pot.png", 835, 213, 60, 82),
          new ImagenPosicionada("08_leaf_mug.png", 42, 492, 70, 70),
          new ImagenPosicionada("07_sleeping_cat.png", 335, 496, 105, 66),
          new ImagenPosicionada("09_yellow_flowers_vase.png", 650, 460, 54, 101),
          // new ImagenPosicionada("10_cat_on_book_stack.png", 455, 740, 112, 80)
          new ImagenPosicionada("11_terracotta_jar.png", 1125, 252, 43, 43),
          // new ImagenPosicionada("12_tea_cup_saucer.png", 1346, 505, 68, 49),
          new ImagenPosicionada("13_pastel_book_stack.png", 350, 730, 126 * 1.15, 90 * 1.15),
          new ImagenPosicionada("14_desktop_globe.png", 1220, 690, 75 * 1.5, 94 * 1.5),
          // new ImagenPosicionada("15_hanging_vines.png", 14, 380, 76, 134)
          new ImagenPosicionada("16_heart_leaf_terracotta_plant.png", 1200, 470, 86, 90),
          new ImagenPosicionada("17_mini_house_bottom.png", 585, 724, 70, 71),
          new ImagenPosicionada("18_small_terracotta_jar.png", 1210, 795, 38, 36)
          // new ImagenPosicionada("19_green_plant_brown_pot.png", 590, 695, 65, 80),
          // new ImagenPosicionada("20_pink_flowers_vase.png", 1250, 742, 58, 89),
          // new ImagenPosicionada("21_pink_camper_van.png", 1170, 732, 95, 65),
          // new ImagenPosicionada("22_white_ceramic_vase.png", 1400, 770, 36, 42)
          );

  private static final double ANCHO_TARJETA_ADICIONAL =
      POSICIONES_TARJETAS.stream().mapToDouble(Posicion::ancho).average().orElseThrow();
  private static final double ALTO_TARJETA_ADICIONAL =
      POSICIONES_TARJETAS.stream().mapToDouble(Posicion::alto).average().orElseThrow();
  private static final Map<String, BufferedImage> IMAGENES = cargarImagenes();

  private final List<Documento> _documentos;
  private final IntFunction<String> _varianteParaFila;

  public ComposicionEstantes(
      List<Documento> documentos, IntFunction<String> varianteParaFilaAdicional) {
    if (documentos == null) {
      throw new IllegalArgumentException("Los documentos son obligatorios.");
    }
    if (varianteParaFilaAdicional == null) {
      throw new IllegalArgumentException("El selector de estantes es obligatorio.");
    }

    _documentos = List.copyOf(documentos);
    _varianteParaFila = varianteParaFilaAdicional;
    setOpaque(false);
    setFocusable(false);
    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent evento) {
            revalidate();
          }
        });
  }

  public static List<String> getVariantesEstantes() {
    return VARIANTES_ESTANTES;
  }

  @Override
  public Dimension getPreferredSize() {
    int ancho = getWidth() > 0 ? getWidth() : (int) ANCHO_DISENO;
    double escala = ancho / ANCHO_DISENO;
    int filasAdicionales = calcularFilasAdicionales();
    int alto =
        (int) Math.ceil((ALTO_DISENO_INICIAL + filasAdicionales * ALTO_FILA_ADICIONAL) * escala);
    return new Dimension(ancho, alto);
  }

  @Override
  protected void paintComponent(Graphics graficos) {
    super.paintComponent(graficos);
    if (getWidth() <= 0) {
      return;
    }

    Graphics2D lienzo = (Graphics2D) graficos.create();
    try {
      lienzo.setRenderingHint(
          RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      lienzo.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      lienzo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      double escala = getWidth() / ANCHO_DISENO;
      int filasAdicionales = calcularFilasAdicionales();

      pintarEstantesIniciales(lienzo, escala);
      pintarEstantesAdicionales(lienzo, escala, filasAdicionales);
      pintarTarjetasIniciales(lienzo, escala);
      pintarTarjetasAdicionales(lienzo, escala);
      pintarDecoraciones(lienzo, escala);
    } finally {
      lienzo.dispose();
    }
  }

  private void pintarEstantesIniciales(Graphics2D lienzo, double escala) {
    BufferedImage estante = IMAGENES.get(ESTANTE_FIJO);
    for (Posicion posicion : ESTANTES_INICIALES) {
      pintarImagen(lienzo, estante, posicion, escala);
    }
  }

  private void pintarEstantesAdicionales(Graphics2D lienzo, double escala, int filasAdicionales) {
    for (int indiceFila = 0; indiceFila < filasAdicionales; indiceFila++) {
      int indiceVisual = PRIMERA_FILA_ADICIONAL + indiceFila;
      String recurso = _varianteParaFila.apply(indiceVisual);
      BufferedImage estante = IMAGENES.get(recurso);
      if (estante == null) {
        throw new IllegalArgumentException("Variante de estante desconocida: " + recurso);
      }

      double y =
          ALTO_DISENO_INICIAL + (indiceFila + 1) * ALTO_FILA_ADICIONAL - ALTO_ESTANTE_ADICIONAL;
      pintarImagen(
          lienzo,
          estante,
          new Posicion(
              MARGEN_ESTANTE_ADICIONAL,
              y,
              ANCHO_DISENO - MARGEN_ESTANTE_ADICIONAL * 2,
              ALTO_ESTANTE_ADICIONAL),
          escala);
    }
  }

  private void pintarTarjetasIniciales(Graphics2D lienzo, double escala) {
    int cantidad = Math.min(_documentos.size(), POSICIONES_TARJETAS.size());
    for (int indice = 0; indice < cantidad; indice++) {
      pintarTarjeta(lienzo, POSICIONES_TARJETAS.get(indice), escala);
    }
  }

  private void pintarTarjetasAdicionales(Graphics2D lienzo, double escala) {
    int cantidad = Math.max(0, _documentos.size() - POSICIONES_TARJETAS.size());
    int columnas = calcularColumnasAdicionales();
    double anchoFila =
        columnas * ANCHO_TARJETA_ADICIONAL + (columnas - 1) * ESPACIO_TARJETAS_ADICIONALES;
    double xInicial = (ANCHO_DISENO - anchoFila) / 2.0;

    for (int indice = 0; indice < cantidad; indice++) {
      int fila = indice / columnas;
      int columna = indice % columnas;
      double yEstante =
          ALTO_DISENO_INICIAL + (fila + 1) * ALTO_FILA_ADICIONAL - ALTO_ESTANTE_ADICIONAL;
      Posicion posicion =
          new Posicion(
              xInicial + columna * (ANCHO_TARJETA_ADICIONAL + ESPACIO_TARJETAS_ADICIONALES),
              yEstante - ALTO_TARJETA_ADICIONAL,
              ANCHO_TARJETA_ADICIONAL,
              ALTO_TARJETA_ADICIONAL);
      pintarTarjeta(lienzo, posicion, escala);
    }
  }

  private void pintarDecoraciones(Graphics2D lienzo, double escala) {
    for (ImagenPosicionada decoracion : DECORACIONES) {
      pintarImagen(lienzo, IMAGENES.get(decoracion.recurso()), decoracion.posicion(), escala);
    }
  }

  private void pintarTarjeta(Graphics2D lienzo, Posicion posicion, double escala) {
    Rectangle limites = escalar(posicion, escala);
    int arco = Math.max(4, (int) Math.round(12 * escala));

    lienzo.setColor(FONDO_TARJETA);
    lienzo.fillRoundRect(limites.x, limites.y, limites.width, limites.height, arco, arco);
    lienzo.setColor(BORDE_TARJETA);
    lienzo.setStroke(new BasicStroke(Math.max(1f, (float) escala)));
    lienzo.drawRoundRect(
        limites.x,
        limites.y,
        Math.max(0, limites.width - 1),
        Math.max(0, limites.height - 1),
        arco,
        arco);
  }

  private static void pintarImagen(
      Graphics2D lienzo, BufferedImage imagen, Posicion posicion, double escala) {
    Rectangle limites = escalar(posicion, escala);
    lienzo.drawImage(
        imagen,
        limites.x,
        limites.y,
        limites.x + limites.width,
        limites.y + limites.height,
        0,
        0,
        imagen.getWidth(),
        imagen.getHeight(),
        null);
  }

  private static Rectangle escalar(Posicion posicion, double escala) {
    int x = (int) Math.round(posicion.x() * escala);
    int y = (int) Math.round(posicion.y() * escala);
    int extremoX = (int) Math.round((posicion.x() + posicion.ancho()) * escala);
    int extremoY = (int) Math.round((posicion.y() + posicion.alto()) * escala);
    return new Rectangle(x, y, Math.max(1, extremoX - x), Math.max(1, extremoY - y));
  }

  private int calcularFilasAdicionales() {
    int adicionales = Math.max(0, _documentos.size() - POSICIONES_TARJETAS.size());
    int columnas = calcularColumnasAdicionales();
    return (adicionales + columnas - 1) / columnas;
  }

  private static int calcularColumnasAdicionales() {
    double anchoUtil = ANCHO_DISENO - MARGEN_ESTANTE_ADICIONAL * 2;
    return Math.max(
        1,
        (int)
            Math.floor(
                (anchoUtil + ESPACIO_TARJETAS_ADICIONALES)
                    / (ANCHO_TARJETA_ADICIONAL + ESPACIO_TARJETAS_ADICIONALES)));
  }

  private static Map<String, BufferedImage> cargarImagenes() {
    Map<String, BufferedImage> imagenes = new LinkedHashMap<>();
    for (String variante : VARIANTES_ESTANTES) {
      imagenes.put(variante, cargarImagen(variante));
    }
    for (ImagenPosicionada decoracion : DECORACIONES) {
      imagenes.put(decoracion.recurso(), cargarImagen(decoracion.recurso()));
    }
    return Map.copyOf(imagenes);
  }

  private static BufferedImage cargarImagen(String recurso) {
    try (InputStream entrada =
        ComposicionEstantes.class.getResourceAsStream(BASE_RECURSOS + recurso)) {
      if (entrada == null) {
        throw new IOException("No se encontró el recurso " + recurso + ".");
      }
      BufferedImage imagen = ImageIO.read(entrada);
      if (imagen == null) {
        throw new IOException("El recurso no es una imagen PNG válida: " + recurso + ".");
      }
      return imagen;
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableUnitIncrement(
      Rectangle rectanguloVisible, int orientacion, int direccion) {
    return 20;
  }

  @Override
  public int getScrollableBlockIncrement(
      Rectangle rectanguloVisible, int orientacion, int direccion) {
    return orientacion == SwingConstants.VERTICAL
        ? rectanguloVisible.height
        : rectanguloVisible.width;
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return true;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  private record Posicion(double x, double y, double ancho, double alto) {}

  private record ImagenPosicionada(String recurso, double x, double y, double ancho, double alto) {
    Posicion posicion() {
      return new Posicion(x, y, ancho, alto);
    }
  }
}
