# Java 17 explicado desde el proyecto

`comp.ps1` usa `javac --release 17`. Dos ejemplos que podés señalar en el código compilado para esa versión son los `record` y la variable de patrón de `instanceof Documento doc`.

| Concepto | Dónde aparece | Cómo funciona y qué resuelve |
|---|---|---|
| Clase y objeto | `Principal.abrir`, `new PanelDocumentos(...)` | La clase define comportamiento y campos. El objeto creado conserva el catálogo, el servicio y el estado de esa ventana. |
| Record y constructor compacto | `Documento.java`, `public Documento { … }` | Java genera campos finales y accesores como `_titulo()`. El constructor aplica `trim()` al título antes de la asignación. Editar crea otro documento con el mismo UUID. |
| Fábrica estática | `Documento.desdeFormulario` | Construye un documento desde textos sin tener que crear primero una instancia. Delega la conversión de números a `entero`. |
| Referencias y opcionales | `Documento.entero`, `opcional`, `validarRango` | `Integer` admite `null` para páginas sin especificar. Una cadena numérica en blanco da `null`. Pasarle una referencia nula a `entero` falla al invocar `isBlank()`. |
| Enum | `TipoDocumento.toString`, `PanelDocumentos.mostrarFicha` | Las cuatro constantes restringen las opciones. `values()` llena el selector y cada constante aporta su etiqueta. |
| Herencia y sobrescritura | `PanelDocumentos.addNotify`, `removeNotify` | El panel especializa dos métodos de `JPanel` para arrancar y detener su temporizador dentro del ciclo de vida de Swing. |
| Interfaces y despacho polimórfico | `PanelDocumentos.boton` | Recibe un `Runnable`. La llamada `accion.run()` ejecuta la acción concreta registrada para ese botón. |
| Eventos, lambdas y referencia a método | `Principal.main`, `PanelDocumentos.boton` | `Principal::abrir` agenda la creación de la ventana. La lambda del botón reúne el control de demo y el manejo de errores en el hilo de eventos. |
| Genéricos y bucles | `RepositorioDocumentoBinario.leerCatalogo` | `for` recorre los elementos leídos. `Map<UUID, Documento>` tipa claves y valores. `LinkedHashMap` conserva el orden de inserción para las tarjetas. |
| Patrón de tipo | `leerCatalogo`, `elemento instanceof Documento doc` | Comprueba el tipo de cada elemento deserializado. Al superar la validación, se puede usar `doc._id()` sin una conversión explícita. |
| Excepciones | `Documento.entero`, `PanelDocumentos.boton` | Un texto como «dos» provoca `NumberFormatException`. El dominio lo traduce a `IllegalArgumentException` y el botón muestra el mensaje. |
| Serialización y cierre de recursos | `RepositorioDocumentoBinario.guardar` y `leerCatalogo` | Los flujos de objetos escriben y leen una `ArrayList` de documentos serializables. `try` con recursos cierra los flujos aunque haya una excepción. |
| Tiempo comprobable | `ServicioPeriodoDemo(Path, Clock)` | Un reloj recibido por constructor permite calcular con un instante conocido. `CREATE_NEW` evita sobrescribir el inicio si ya existe. |

## Tres métodos para recorrer a mano

1. **Leer y buscar.** `leerCatalogo` exige la clase exacta `ArrayList`. `putIfAbsent` revela un UUID repetido cuando devuelve el documento anterior. La lectura completa cuesta O(n) en promedio. Aunque `get(id)` cuesta O(1) en promedio, `buscarPorId` primero reconstruye todo el mapa.
2. **Guardar sin escribir encima del único original.** `guardar` escribe los documentos en un temporal del mismo directorio, vacía el flujo, sincroniza el descriptor e intenta un movimiento atómico. Si el sistema no lo admite y el destino falta, mueve el temporal. Si el destino existe, `reemplazarConRespaldo` lo copia antes de reemplazarlo. Si además falla la restauración, conserva el respaldo para recuperación.
3. **Razonar en el límite.** En `estaExpirada`, con inicio a las 10:00 y un reloj del mismo día, a las 10:29:59 la demo sigue vigente y a las 10:30:00 vence. Un inicio posterior al reloj, una fecha ilegible o un error de acceso también devuelven `true`.
