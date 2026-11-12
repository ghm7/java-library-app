package com.example.library.ui;

import java.awt.Component;
import java.awt.Container;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.example.library.domain.TipoDocumento;
import com.example.library.repository.RepositorioDocumentoBinario;
import com.example.library.service.ServicioDocumento;
import com.example.library.service.ServicioPeriodoDemo;

/** Prueba del recorrido CRUD desde los controles de Swing, sin abrir una ventana. */
public final class PruebaPanelDocumentos {
  private PruebaPanelDocumentos() {
  }

  public static void main(String[] args) throws Exception {
    Path directorio = Files.createTempDirectory("panel-catalogo-prueba-");
    try {
      _ejecutarCrud(directorio);
      _verificarCatalogoCorrupto(directorio);
      _verificarBloqueoDemo(directorio);
      System.out.println("Prueba de interfaz superada.");
    } finally {
      _eliminarDirectorio(directorio);
    }
  }

  private static void _ejecutarCrud(Path directorio) throws Exception {
    ServicioDocumento servicio = new ServicioDocumento(
        new RepositorioDocumentoBinario(directorio.resolve("libros.dat"))
    );
    ServicioPeriodoDemo demo = new ServicioPeriodoDemo(
        directorio.resolve("inicio-activa.txt"),
        Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    );

    SwingUtilities.invokeAndWait(() -> {
      PanelDocumentos panel = new PanelDocumentos(servicio, documento -> true, demo);
      JButton nuevo = _boton(panel, "Nuevo documento");
      _afirmar(nuevo.isEnabled(), "Debe poder inicializarse un catálogo ausente.");

      nuevo.doClick();
      _campo(panel, "titulo").setText("Documento desde Swing");
      _campo(panel, "genero").setText("Tecnología");
      _campo(panel, "valoracion").setText("5");
      _campo(panel, "paginas").setText("250");
      _combo(panel, "tipo").setSelectedItem(TipoDocumento.ARTICULO_CIENTIFICO);
      _boton(panel, "Guardar").doClick();
      _afirmar(
          servicio.listar().size() == 1,
          "Guardar en el formulario debe crear el documento."
      );

      _boton(panel, "Editar").doClick();
      _campo(panel, "titulo").setText("Documento editado desde Swing");
      _boton(panel, "Guardar").doClick();
      _afirmar(
          "Documento editado desde Swing".equals(servicio.listar().get(0).getTitulo()),
          "Editar en el formulario debe actualizar el documento."
      );

      _boton(panel, "Eliminar").doClick();
      _afirmar(servicio.listar().isEmpty(), "Eliminar debe remover el documento.");
    });
  }

  private static void _verificarBloqueoDemo(Path directorio) throws Exception {
    Path inicio = directorio.resolve("inicio-expirada.txt");
    Instant inicioDemo = Instant.parse("2026-01-01T00:00:00Z");
    new ServicioPeriodoDemo(inicio, Clock.fixed(inicioDemo, ZoneOffset.UTC))
        .estaExpirada();
    ServicioDocumento servicio = new ServicioDocumento(
        new RepositorioDocumentoBinario(directorio.resolve("demo-libros.dat"))
    );
    ServicioPeriodoDemo demoExpirada = new ServicioPeriodoDemo(
        inicio,
        Clock.fixed(inicioDemo.plusSeconds(1800), ZoneOffset.UTC)
    );

    SwingUtilities.invokeAndWait(() -> {
      PanelDocumentos panel = new PanelDocumentos(
          servicio,
          documento -> true,
          demoExpirada
      );
      _afirmar(
          !_boton(panel, "Nuevo documento").isEnabled(),
          "La demo expirada debe bloquear la creación."
      );
    });
  }

  private static void _verificarCatalogoCorrupto(Path directorio) throws Exception {
    Path catalogo = directorio.resolve("catalogo-corrupto.dat");
    Files.writeString(catalogo, "archivo dañado", StandardCharsets.UTF_8);
    ServicioDocumento servicio = new ServicioDocumento(
        new RepositorioDocumentoBinario(catalogo)
    );
    ServicioPeriodoDemo demo = new ServicioPeriodoDemo(
        directorio.resolve("inicio-corrupcion.txt"),
        Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    );

    SwingUtilities.invokeAndWait(() -> {
      PanelDocumentos panel = new PanelDocumentos(servicio, documento -> true, demo);
      _afirmar(
          !_boton(panel, "Nuevo documento").isEnabled(),
          "La interfaz debe bloquear escrituras ante un catálogo corrupto."
      );
    });
  }

  private static JButton _boton(Container contenedor, String texto) {
    for (Component componente : contenedor.getComponents()) {
      if (componente instanceof JButton
          && texto.equals(((JButton) componente).getText())) {
        return (JButton) componente;
      }
      if (componente instanceof Container) {
        try {
          return _boton((Container) componente, texto);
        } catch (IllegalArgumentException ignored) {
          // Se continúa buscando en el siguiente componente hermano.
        }
      }
    }
    throw new IllegalArgumentException("No se encontró el botón " + texto + ".");
  }

  private static JTextField _campo(Container contenedor, String nombre) {
    Component componente = _componente(contenedor, nombre);
    if (!(componente instanceof JTextField)) {
      throw new IllegalArgumentException("El campo " + nombre + " no es de texto.");
    }
    return (JTextField) componente;
  }

  @SuppressWarnings("unchecked")
  private static JComboBox<TipoDocumento> _combo(Container contenedor, String nombre) {
    Component componente = _componente(contenedor, nombre);
    if (!(componente instanceof JComboBox<?>)) {
      throw new IllegalArgumentException("El campo " + nombre + " no es una lista.");
    }
    return (JComboBox<TipoDocumento>) componente;
  }

  private static Component _componente(Container contenedor, String nombre) {
    for (Component componente : contenedor.getComponents()) {
      if (nombre.equals(componente.getName())) {
        return componente;
      }
      if (componente instanceof Container) {
        try {
          return _componente((Container) componente, nombre);
        } catch (IllegalArgumentException ignored) {
          // Se continúa buscando en el siguiente componente hermano.
        }
      }
    }
    throw new IllegalArgumentException("No se encontró el campo " + nombre + ".");
  }

  private static void _afirmar(boolean condicion, String mensaje) {
    if (!condicion) {
      throw new AssertionError(mensaje);
    }
  }

  private static void _eliminarDirectorio(Path directorio) throws Exception {
    try (var rutas = Files.walk(directorio)) {
      rutas.sorted((izquierda, derecha) -> derecha.compareTo(izquierda))
          .forEach(ruta -> {
            try {
              Files.deleteIfExists(ruta);
            } catch (Exception e) {
              throw new IllegalStateException(e);
            }
          });
    }
  }
}
