# Cinco ejercicios para resolver con el código abierto

Antes de cada prueba, anotá tu predicción. Para practicar cambios, usá una copia del proyecto. Los ejercicios de lectura también se pueden resolver siguiendo el método en el editor.

## Básicos

1. **Normalizá un campo opcional.** En `Documento.java`, poné un punto de interrupción en `opcional`. Seguí qué devuelve hoy para `null`, `"   "` y `"  Ciencia  "`. Proponé el cambio mínimo para que el último devuelva `"Ciencia"` y los otros dos sigan devolviendo `null`.
   *Pista.* Compará esa línea con el tratamiento de `_titulo` en el constructor.
2. **Agregá un tipo documental.** En `TipoDocumento.java`, estudiá `toString` y agregá hipotéticamente `TESIS("Tesis")`. Detenete en `PanelDocumentos.mostrarFicha`, donde nace el selector. El resultado esperado es poder elegir «Tesis» y leer esa etiqueta en la tarjeta guardada.
   *Pista.* Mirá de dónde sale el arreglo que recibe el `JComboBox`.

## Intermedios

3. **Encontrá el instante exacto del vencimiento.** En `ServicioPeriodoDemo.java`, seguí el `return` de `estaExpirada` con inicio `10:00:00Z` y tres relojes fijos del mismo día. Predecí el resultado a las `10:29:59Z`, `10:30:00Z` y `10:30:01Z`. Tenés que poder señalar la comparación que incluye la igualdad.
   *Pista.* Preguntate cuánto vale `ahora.isBefore(inicio.plus(DURACION))` en el segundo caso.
4. **Filtrá tarjetas por título.** Diseñá un filtro parcial en `PanelDocumentos.java`, método `mostrarCatalogo`. Probalo en el bucle con la consulta «sol» y los títulos «SOLAR», «Mar» y «Girasol». Deben quedar la primera y la tercera tarjeta en ese orden. Una consulta vacía recupera las tres.
   *Pista.* `RepositorioDocumentoBinario.leerCatalogo` ya entrega los datos en orden. Decidí qué tarjetas construir sin escribir el archivo.

## Avanzado

5. **Seguí un reemplazo que falla.** En `RepositorioDocumentoBinario.java`, marcá puntos de interrupción en el `catch` de `AtomicMoveNotSupportedException` de `guardar` y en el `catch` interno de `reemplazarConRespaldo`. Suponé que existe un catálogo previo y que falla también el reemplazo ordinario. Dibujá qué archivos intenta limpiar el programa si restaurar la copia funciona y si falla.
   *Pista.* Seguí el valor de `restaurando` justo antes del `finally`.

## Criterios de solución

1. Hoy el tercer caso conserva los espacios. En la rama con contenido, devolver `texto.trim()` produce el resultado pedido.
2. El selector usa `TipoDocumento.values()`. La constante viaja en `Documento._tipo()` y la tarjeta muestra su `toString()`.
3. Los resultados son `false`, `true`, `true`. Exactamente en el límite, «ahora es anterior» ya es falso.
4. Compará consulta y título con la misma normalización de mayúsculas. Filtrá en memoria y conservá el orden y el total de datos guardados.
5. Si restaura, el original vuelve a su ruta y se intenta borrar el respaldo. Si restaurar falla, se conserva el `.bak`. En ambas ramas `guardar` intenta limpiar el temporal y propaga `IllegalStateException`.
