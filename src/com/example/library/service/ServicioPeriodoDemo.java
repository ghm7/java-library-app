package com.example.library.service;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;

public final class ServicioPeriodoDemo {
  private static final Duration DURACION = Duration.ofMinutes(30);
  private static final String ARCHIVO_INICIO = "inicio-demo-java-library-app.txt";

  private final Path _archivoInicio;
  private final Clock _reloj;

  public ServicioPeriodoDemo() {
    this(
        Path.of(System.getProperty("java.io.tmpdir"), "java-library-app", ARCHIVO_INICIO),
        Clock.systemUTC());
  }

  public ServicioPeriodoDemo(Path archivoInicio, Clock reloj) {
    if (archivoInicio == null || reloj == null) {
      throw new IllegalArgumentException("La ruta y el reloj son obligatorios.");
    }

    _archivoInicio = archivoInicio.normalize();
    _reloj = reloj;
  }

  public boolean estaExpirada() {
    try {
      Instant inicio = _obtenerInicio();

      return !_reloj.instant().isBefore(inicio.plus(DURACION));
    } catch (IOException | DateTimeException | SecurityException e) {
      return true;
    }
  }

  private Instant _obtenerInicio() throws IOException {
    if (Files.exists(_archivoInicio)) {
      return _leerInicio();
    }

    Path directorio = _archivoInicio.getParent();
    if (directorio == null) {
      throw new IOException("El archivo de inicio no tiene directorio.");
    }

    Files.createDirectories(directorio);
    Instant ahora = _reloj.instant();

    try {
      Files.writeString(_archivoInicio, ahora.toString(), StandardOpenOption.CREATE_NEW);

      return ahora;
    } catch (FileAlreadyExistsException e) {
      return _leerInicio();
    }
  }

  private Instant _leerInicio() throws IOException {
    if (!Files.isRegularFile(_archivoInicio)) {
      throw new IOException("El inicio de la demo no es un archivo válido.");
    }

    Instant inicio = Instant.parse(Files.readString(_archivoInicio));

    if (inicio.isAfter(_reloj.instant())) {
      throw new IOException("El inicio de la demo no puede estar en el futuro.");
    }

    return inicio;
  }
}
