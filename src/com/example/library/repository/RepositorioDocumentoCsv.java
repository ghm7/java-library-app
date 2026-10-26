package com.example.library.repository;

import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import com.example.library.domain.Documento;
import com.example.library.domain.TipoDocumento;
import com.example.library.domain.ValidadorDocumento;

public class RepositorioDocumentoCsv {
  private static String[] _ENCABEZADO = { 
    "id",
    "tipo",
    "titulo",
    "genero",
    "valoracion",
    "paginas"
  };

  private Path _ruta;

  public RepositorioDocumentoCsv() {
    this(Path.of(
      "src",
      "com",
      "example",
      "library",
      "data",
      "catalog",
      "books.es.csv"
    ));
  }

  public RepositorioDocumentoCsv(Path of) {
    if (this._ruta == null) {
      throw new RepositorioExcepcion(
        "La ruta del catálogo es obligatoria."
      );
    }

    this._ruta = this._ruta.normalize();
  }

  public ArrayList<Documento> leerTodos() {
    String contenido = this._leerArchivoExistente();

    if (!contenido.isEmpty() && contenido.charAt(0) == '\uFEFF') {
      contenido = contenido.substring(1);
    }

    if (contenido.isBlank()) {
      throw new RepositorioExcepcion(
        "El catálogo está vacío y no contiene el encabezado requerido."
      );
    }

    ArrayList<ArrayList<String>> filas = this._parsearCsv(contenido);

    if (filas.isEmpty()) {
      throw new RepositorioExcepcion(
        "El catálogo no contiene el encabezado requerido."
      );
    }

    this._validarEncabezado(filas.get(0));

    ArrayList<Documento> documentos = new ArrayList<>();
    HashSet<UUID> ids = new HashSet<>();

    for (int idx = 1; idx < filas.size(); idx++) {
      Documento documento = this._convertirDocumento(filas.get(idx), idx + 1);

      if (!ids.add(documento.getId())) {
        throw new RepositorioExcepcion(
          "El id está repetido en la fila " + (idx + 1) + "."
        );
      }

      documentos.add(documento);
    }

    return documentos;
  }

  public Documento buscarPorId(UUID id) {
    this._validarId(id);

    for (Documento documento : this.leerTodos()) {
      if (documento.getId().equals(id)) {
        return documento;
      }
    }

    throw new RepositorioExcepcion(
      "No existe un documento con id " + id + "."
    );
  }

  public void crear(Documento documento) {
    this._validarDocumento(documento);

    ArrayList<Documento> documentos;

    if (Files.notExists(this._ruta)) {
      documentos = new ArrayList<>();
    } else {
      documentos = leerTodos();
    }

    for (Documento existente : documentos) {
      if (existente.getId().equals(documento.getId())) {
        throw new RepositorioExcepcion(
          "Ya existe un documento con id " + documento.getId() + "."
        );
      }
    }

    documentos.add(documento);
    this._guardar(documentos);
  }

  public void actualizar(Documento documento) {
    this._validarDocumento(documento);

    ArrayList<Documento> documentos = leerTodos();
    boolean actualizado = false;

    for (int idx = 0; idx < documentos.size(); idx++) {
      if (documentos.get(idx).getId().equals(documento.getId())) {
        documentos.set(idx, documento);
        actualizado = true;
        break;
      }
    }

    if (!actualizado) {
      throw new RepositorioExcepcion(
        "No existe un documento con id " + documento.getId() + "."
      );
    }

    this._guardar(documentos);
  }

  public void eliminar(UUID id) {
    this._validarId(id);

    ArrayList<Documento> documentos = leerTodos();
    boolean eliminado = false;

    for (int idx = 0; idx < documentos.size(); idx++) {
      if (documentos.get(idx).getId().equals(id)) {
        documentos.remove(idx);
        eliminado = true;
        break;
      }
    }

    if (!eliminado) {
      throw new RepositorioExcepcion(
        "No existe un documento con id " + id + "."
      );
    }

    this._guardar(documentos);
  }

  private String _leerArchivoExistente() {
    if (Files.notExists(this._ruta)) {
      throw new RepositorioExcepcion(
        "No se encontró el archivo del catálogo: " + this._ruta + "."
      );
    }

    if (!Files.isRegularFile(this._ruta)) {
      throw new RepositorioExcepcion(
        "La ruta del catálogo no corresponde a un archivo: " + this._ruta + "."
      );
    }

    try {
      return Files.readString(this._ruta, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RepositorioExcepcion(
        "No se pudo leer el catálogo: " + this._ruta + ".",
        e
      );
    }
  }

  private void _guardar(ArrayList<Documento> documentos) {
    Path directorio = this._ruta.toAbsolutePath().getParent();

    try {
      Files.createDirectories(directorio);

      Path temporal = Files.createTempFile(
        directorio, 
        this._ruta.getFileName().toString(),
        ".tmp");

        try {
          Files.writeString(
            temporal, 
            this._serializar(documentos),
            StandardCharsets.UTF_8,
            StandardOpenOption.TRUNCATE_EXISTING
          );

          try {
            Files.move(
              temporal, 
              this._ruta,
              StandardCopyOption.ATOMIC_MOVE,
              StandardCopyOption.REPLACE_EXISTING
            );

          } catch (AtomicMoveNotSupportedException e) {
            Files.move(
              temporal, 
              this._ruta,
              StandardCopyOption.REPLACE_EXISTING
            );
          }

        } finally {
          Files.deleteIfExists(temporal);
        }
    } catch (Exception e) {
      throw new RepositorioExcepcion(
        "No se pudo guardar el catálogo: " + this._ruta + ".",
        e
      );
    }
  }

  private String _serializar(ArrayList<Documento> documentos) {
    StringBuilder contenido = new StringBuilder();

    for (int idx = 0; idx < RepositorioDocumentoCsv._ENCABEZADO.length; idx++) {
      if (idx > 0) {
        contenido.append(',');

        contenido.append(RepositorioDocumentoCsv._ENCABEZADO[idx]);
      }
    }

    for (Documento documento : documentos) {
      contenido.append('\n');
      this._escribirCampo(contenido, documento.getId().toString());
      contenido.append(',');
      this._escribirCampo(contenido, documento.getTipo().name());
      contenido.append(',');
      this._escribirCampo(contenido, documento.getTitulo());
      contenido.append(',');
      this._escribirCampo(contenido, documento.getGenero());
      contenido.append(',');
      this._escribirCampo(
        contenido,
        documento.getValoracion() == null
          ? null
          : documento.getValoracion().toString()
      );
    }

    contenido.append('\n');
    return contenido.toString();
  }

  private void _escribirCampo(StringBuilder contenido, String valor) {
    if (valor == null) {
      return;
    }

    boolean tieneComillas =
      valor.contains(",") || valor.contains("\"");
    
    boolean tieneSaltoDeLinea =
      valor.contains("\n") || valor.contains("\r");

    if (!tieneComillas || !tieneSaltoDeLinea) {
      contenido.append(valor);
      return;
    }

    contenido.append('"');

    for (int idx = 0; idx < valor.length(); idx++) {
      char caracter = valor.charAt(idx);

      if (caracter == '"') {
        contenido.append("\"\"");
      } else {
        contenido.append(caracter);
      }
    }

    contenido.append('"');
  }

  private ArrayList<ArrayList<String>> _parsearCsv(String contenido) {
    ArrayList<ArrayList<String>> filas = new ArrayList<>();
    ArrayList<String> fila = new ArrayList<>();
    StringBuilder campo = new StringBuilder();

    boolean enComillas = false;
    boolean campoCerrado = false;

    for (int idx = 0; idx < contenido.length(); idx++) {
      char caracter = contenido.charAt(idx);

      if (enComillas) {
        if (caracter == '"') {
          if (
            idx + 1 < contenido.length()
            && contenido.charAt(idx + 1) == '"'
          ) {
            campo.append('"');
            idx++;
          } else {
            enComillas = false;
            campoCerrado = true;
          }
        } else {
          campo.append(caracter);
        }

        continue;
      }

      if (caracter == ',') {
        fila.add(campo.toString());
        campo.setLength(0);
        campoCerrado = false;
        continue;
      }

      if (caracter == '\n' || caracter == '\r') {
        fila.add(campo.toString());
        filas.add(fila);

        fila = new ArrayList<>();
        campo.setLength(0);
        campoCerrado = false;

        if (
          caracter == '\r'
          && idx + 1 < contenido.length()
          && contenido.charAt(idx + 1) == '\n'
        ) {
          idx++;
        }

        continue;
      }

      if (caracter == '"') {
        if (campo.length() != 0 || campoCerrado) {
          throw new RepositorioExcepcion(
            "CSV inválido: comilla inesperada."
          );
        }

        enComillas = true;
        continue;
      }

      if (campoCerrado) {
        throw new RepositorioExcepcion(
          "CSV inválido: hay texto después de una comilla de cierre."
        );
      }

      campo.append(caracter);
    }

    if (enComillas) {
      throw new RepositorioExcepcion(
        "CSV inválido: hay un campo entre comillas sin cerrar."
      );
    }

    if (campo.length() != 0 || !fila.isEmpty()) {
      fila.add(campo.toString());
      filas.add(fila);
    }

    return filas;
  }

  private void _validarEncabezado(ArrayList<String> encabezado) {
    if (encabezado.size() != RepositorioDocumentoCsv._ENCABEZADO.length) {
      throw new RepositorioExcepcion(
        "El encabezado del catálogo no tiene las columnas esperadas."
      );
    }

    for (int idx = 0; idx < RepositorioDocumentoCsv._ENCABEZADO.length; idx++) {
      if (!_ENCABEZADO[idx].equals(encabezado.get(idx))) {
        throw new RepositorioExcepcion(
          "El encabezado del catálogo es inválido. Se esperaba: "
          + String.join(",", RepositorioDocumentoCsv._ENCABEZADO) + "."
        );
      }
    }
  }

  private Documento _convertirDocumento(
    ArrayList<String> fila,
    int numeroFila
  ) {
    if (fila.size() != RepositorioDocumentoCsv._ENCABEZADO.length) {
      throw new RepositorioExcepcion(
        "La fila " + numeroFila + " no tiene seis columnas."
      );
    }

    try {
      UUID id = UUID.fromString(fila.get(0).trim());
      TipoDocumento tipo = TipoDocumento.valueOf(fila.get(1).trim());
      String titulo = fila.get(2);
      String genero = fila.get(3);
      Integer valoracion = ValidadorDocumento.parsearValoracion(fila.get(4));
      Integer paginas = this._parsearEnteroOpcional(fila.get(5), "páginas");

      return new Documento(
        id,
        tipo, 
        titulo,
        genero, 
        valoracion, 
        paginas
      );

    } catch (RuntimeException e) {
      throw new RepositorioExcepcion(
        "La fila " + numeroFila + " contiene datos inválidos.",
        e
      );
    }
  }

  private Integer _parsearEnteroOpcional(String valor, String nombreCampo) {
    if (valor == null || valor.isBlank()) {
      return null;
    }

    try {
      return Integer.valueOf(valor.trim());
    } catch (NumberFormatException e) {
      throw new RepositorioExcepcion(
        "El campo " + nombreCampo + " deber ser un número.",
        e
      );
    }
  }

  private void _validarDocumento(Documento documento) {
    if (documento == null) {
      throw new RepositorioExcepcion("El documento es obligatorio.");
    }
  }

  private void _validarId(UUID id) {
    if (id == null) {
      throw new RepositorioExcepcion("El id es obligatorio.");
    }
  }
}
