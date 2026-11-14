# Quince preguntas de defensa

Tapá la columna de respuestas y señalá en el código dónde comprobarías cada predicción.

## Básicas

| Pregunta | Respuesta esperada |
|---|---|
| 1. ¿Qué archivo demuestra que el proyecto apunta a Java 17? | `comp.ps1` invoca `javac --release 17`. Un ejemplo de sintaxis admitida es el `record Documento`. |
| 2. ¿Por qué editar reconstruye un documento? | Los componentes del record son finales. En `PanelDocumentos.mostrarFicha`, Guardar llama a `Documento.desdeFormulario` con el UUID existente y los valores editados. |
| 3. ¿De dónde sale el texto «Artículo científico»? | `TipoDocumento.ARTICULO_CIENTIFICO` guarda esa etiqueta. La tarjeta llama a `toString()` al dibujarla en `mostrarCatalogo`. |
| 4. ¿Desde qué hilo se construye la ventana? | `Principal.main` usa `SwingUtilities.invokeLater(Principal::abrir)`. Swing ejecuta `abrir` en su hilo de eventos. |
| 5. ¿Cómo llega el repositorio hasta un botón? | `Principal.abrir` lo entrega al constructor de `PanelDocumentos`. El panel lo conserva en `catalogo` y la acción del botón usa ese campo. |

## Intermedias

| Pregunta | Respuesta esperada |
|---|---|
| 6. ¿En qué capa cambiarías el máximo de páginas? | En `Documento`, tanto en `desdeFormulario` como en el constructor compacto y su mensaje. En `PanelDocumentos.mostrarFicha` actualizaría también la indicación visible. |
| 7. ¿Da lo mismo escribir espacios que «0» en páginas? | `Documento.entero` convierte espacios en `null`, admitido por `validarRango`. El cero sí es un entero, pero queda por debajo del mínimo. |
| 8. ¿Cómo detecta el lector dos documentos con el mismo UUID? | `RepositorioDocumentoBinario.leerCatalogo` recorre la lista. Si `putIfAbsent` devuelve el documento anterior, rechaza el catálogo. |
| 9. ¿Por qué la interfaz permite crear el primer registro si el archivo falta? | `mostrarCatalogo` reconoce `NoSuchFileException` como causa del fallo. Con la demo vigente habilita el alta y `guardarDocumento` empieza con un mapa vacío. |
| 10. ¿Para qué recibe el servicio un `Clock`? | `ServicioPeriodoDemo.estaExpirada` consulta ese reloj. Un reloj fijo permite comprobar la frontera exacta de 30 minutos sin esperar. |

## Avanzadas

| Pregunta | Respuesta esperada |
|---|---|
| 11. Dos instancias guardan al mismo tiempo. ¿Puede perderse una edición aunque funcione el movimiento atómico? | Sí. Ambas pueden leer la misma versión y escribir catálogos completos distintos. La última escritura puede borrar la primera. Se necesitaría coordinar toda la operación de lectura, cambio y escritura de `guardarDocumento`. |
| 12. ¿Un índice por título en un mapa local aceleraría muchas búsquedas sucesivas? | Si se reconstruye en cada llamada a `leerCatalogo`, todavía hay que leer todo el archivo cada vez. Para aprovecharlo entre búsquedas habría que conservarlo y definir cómo actualizarlo cuando cambien los datos. |
| 13. ¿Qué recuperás si falla el reemplazo ordinario y también la restauración? | `reemplazarConRespaldo` deja el `.bak` porque `restaurando` sigue en `true`. La copia permite intentar recuperar el catálogo previo. La ruta principal puede haber quedado dañada. |
| 14. ¿Qué cambiarías para que la UI también admita una base de datos? | Extraería el contrato de lectura y escritura a una interfaz. `PanelDocumentos` recibiría esa interfaz y `Principal.abrir` elegiría la implementación al crear el panel. Es una modificación propuesta. |
| 15. Si el reloj retrocede hasta antes del inicio registrado, ¿qué verá el usuario? | `estaExpirada` devuelve `true` por `inicio.isAfter(ahora)`. Cuando `PanelDocumentos.comprobarDemo` lo detecta, deja la sesión en modo consulta. Aunque el reloj luego se corrija, el panel conserva `expirada=true`. |
