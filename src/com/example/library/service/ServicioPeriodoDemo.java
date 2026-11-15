package com.example.library.service;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;

public final class ServicioPeriodoDemo {
  private static final Duration DURACION = Duration.ofMinutes(30);
  private static final String ARCHIVO_INICIO = "inicio-demo-java-library-app.txt";

  private final Path archivoInicio;
  private final Clock reloj;

  public ServicioPeriodoDemo(Path archivoInicio, Clock reloj) {
    this.archivoInicio = archivoInicio.normalize();
    this.reloj = reloj;
  }

  public ServicioPeriodoDemo() {
    this(
        Path.of(System.getProperty("java.io.tmpdir"), "java-library-app", ARCHIVO_INICIO),
        Clock.systemUTC());
  }

  public boolean estaExpirada() {
    try {
      if (Files.notExists(archivoInicio)) registrarInicio();

      Instant inicio = Instant.parse(Files.readString(archivoInicio));
      Instant ahora = reloj.instant();

      return inicio.isAfter(ahora) || !ahora.isBefore(inicio.plus(DURACION));
    } catch (IOException | DateTimeException | SecurityException e) {
      return true;
    }
  }

  private void registrarInicio() throws IOException {
    Files.createDirectories(archivoInicio.toAbsolutePath().getParent());
    try {
      Files.writeString(archivoInicio, reloj.instant().toString(), StandardOpenOption.CREATE_NEW);
    } catch (FileAlreadyExistsException ignorado) {
    }
  }
}
