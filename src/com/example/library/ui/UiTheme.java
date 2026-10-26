package com.example.library.ui;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;


public final class UiTheme {
	  public static final Color CREMA = new Color(0xFFF8ED);
	  public static final Color CORAL = new Color(0xE97862);
	  public static final Color CORAL_OSCURO = new Color(0xC95745);
	  public static final Color GRAFITO = new Color(0x2F2F33);
	  public static final Color TEXTO_SECUNDARIO = new Color(0x6E6A67);

	  public static final Font TITULO = new Font("SansSerif", Font.BOLD, 26);
	  public static final Font SUBTITULO = new Font("SansSerif", Font.PLAIN, 15);

	  private UiTheme() {}

	  public static void installLookAndFeel() {
	    try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception ignored) {
	      // Se conserva el look & feel estándar disponible.
	    }
	  }
	}