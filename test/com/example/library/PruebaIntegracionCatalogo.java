package com.example.library;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import com.example.library.domain.Documento;
import com.example.library.domain.TipoDocumento;
import com.example.library.repository.RepositorioDocumentoBinario;
import com.example.library.repository.RepositorioExcepcion;
import com.example.library.service.ServicioDocumento;
import com.example.library.service.ServicioPeriodoDemo;

/** Prueba ejecutable sin dependencias externas de persistencia y demo. */
public final class PruebaIntegracionCatalogo {
  private PruebaIntegracionCatalogo() {
  }

  public static void main(String[] args) throws Exception {
    Path directorio = Files.createTempDirectory("catalogo-prueba-");
    try {
      _verificarCrudEnCatalogoNuevo(directorio.resolve("libros.dat"));
      _verificarCatalogoCorruptoNoSeSobrescribe(
          directorio.resolve("catalogo-corrupto.dat")
      );
      _verificarExpiracionDemo(directorio.resolve("inicio-demo.txt"));
      System.out.println("Pruebas de integración superadas.");
    } finally {
      _eliminarDirectorio(directorio);
    }
  }

  private static void _verificarCrudEnCatalogoNuevo(Path catalogo) {
    ServicioDocumento servicio = new ServicioDocumento(
        new RepositorioDocumentoBinario(catalogo)
    );
    Documento creado = servicio.crear(
        TipoDocumento.LIBRO,
        "El catálogo inicial",
        "Descripción",
        "Ficción",
        5,
        120
    );
    _afirmar(Files.exists(catalogo), "Crear debe inicializar libros.dat.");
    _afirmar(servicio.listar().size() == 1, "El documento creado debe listarse.");

    Documento actualizado = servicio.actualizar(
        creado.getId(),
        TipoDocumento.ARTICULO_CIENTIFICO,
        "Título actualizado",
        null,
        "Ciencia",
        4,
        80
    );
    _afirmar(
        "Título actualizado".equals(servicio.consultar(creado.getId()).getTitulo()),
        "Actualizar debe persistir los cambios."
    );
    _afirmar(
        actualizado.getId().equals(creado.getId()),
        "Actualizar debe conservar el identificador."
    );

    servicio.eliminar(creado.getId());
    _afirmar(servicio.listar().isEmpty(), "Eliminar debe persistir la baja.");
  }

  private static void _verificarCatalogoCorruptoNoSeSobrescribe(Path catalogo)
      throws Exception {
    byte[] contenido = "contenido no serializado".getBytes(StandardCharsets.UTF_8);
    Files.write(catalogo, contenido);
    RepositorioDocumentoBinario repositorio = new RepositorioDocumentoBinario(catalogo);

    try {
      repositorio.crear(Documento.nuevo(
          TipoDocumento.LIBRO, "Documento", null, null, null, null
      ));
      throw new AssertionError("Un catálogo corrupto no debe aceptar escrituras.");
    } catch (RepositorioExcepcion esperado) {
      _afirmar(
          java.util.Arrays.equals(contenido, Files.readAllBytes(catalogo)),
          "Un catálogo corrupto no debe sobrescribirse."
      );
    }
  }

  private static void _verificarExpiracionDemo(Path archivoInicio) {
    Instant inicio = Instant.parse("2026-01-01T00:00:00Z");
    ServicioPeriodoDemo activa = new ServicioPeriodoDemo(
        archivoInicio,
        Clock.fixed(inicio, ZoneOffset.UTC)
    );
    _afirmar(!activa.estaExpirada(), "La demo debe iniciar activa.");

    ServicioPeriodoDemo expirada = new ServicioPeriodoDemo(
        archivoInicio,
        Clock.fixed(inicio.plus(Duration.ofMinutes(30)), ZoneOffset.UTC)
    );
    _afirmar(expirada.estaExpirada(), "La demo debe expirar a los 30 minutos.");
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
