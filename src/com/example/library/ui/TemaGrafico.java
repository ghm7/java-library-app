package com.example.library.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.UIManager;

public class TemaGrafico {
  public static final Color CREMA = new Color(0xFFF8ED);
  public static final Color CREMA_SUAVE = new Color(0xFFF3EA);
  public static final Color MARFIL = new Color(0xFFFAF5);
  public static final Color CORAL = new Color(0xE97862);
  public static final Color CORAL_CLARO = new Color(0xF7B6B4);
  public static final Color CORAL_OSCURO = new Color(0xC95745);
  public static final Color GRAFITO = new Color(0x3D343A);
  public static final Color TEXTO_SECUNDARIO = new Color(0x8C7D7B);
  public static final Color BORDE_SUAVE = new Color(0xEFDCCE);

  public static final Font TITULO = new Font("Serif", Font.BOLD, 26);
  public static final Font SUBTITULO = new Font("SansSerif", Font.PLAIN, 15);
  public static final Font MARCA = new Font("Serif", Font.BOLD, 25);
  public static final Font FILTRO = cargarFuenteFiltro();

  private TemaGrafico() {}

  private static Font cargarFuenteFiltro() {
    try (InputStream recurso = TemaGrafico.class.getResourceAsStream(
        "/com/example/library/ui/fonts/Nunito-Bold.ttf")) {
      if (recurso == null) {
        throw new IOException("No se encontró la fuente Nunito Bold.");
      }
      return Font.createFont(Font.TRUETYPE_FONT, recurso).deriveFont(13f);
    } catch (FontFormatException | IOException ignored) {
      return new Font("SansSerif", Font.PLAIN, 13);
    }
  }

  public static void instalarApariencia() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {
    }
  }
}
