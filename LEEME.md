# Gestor de pilotos
Este es un programa de consola desarrollado en Kotlin para gestionar un listado de pilotos. Los datos se almacenan en un fichero binario de
acceso aleatorio llamado *pilotos.bin*
## 1. Estructura de datos
### **Data Class:**
```kotlin
data class Piloto(
val id: Int,
val nombre: String,
val escuderia: String,
val puntos_medios: Double
)
```
### **Estructura del registro binario:**
- **id**: Int - 4 bytes
- **nombre**: String - 50 bytes (longitud fija)
- **escuderia**: String - 30 bytes (longitud fija)
- **puntos_medios**: Double - 8 bytes
- **Tamaño Total del Registro**: 4 + 20 + 8 = 32 bytes
## 2. Instrucciones de ejecución
- **Requisitos previos**: Asegúrate de tener un JDK (ej. versión 17 o
  superior) instalado.
- **Compilación**: Abre el proyecto en IntelliJ IDEA y deja que Gradle
  sincronice las dependencias.
- **Ejecución**: Ejecuta la función main del fichero Main.kt.
- **Ficheros necesarios**: El programa espera encontrar un fichero
  *DatosF1.csv* en la carpeta *Datos* dentro de la raíz del proyecto
  para la carga inicial de datos.
## 3. Decisiones de diseño
- Elegí CSV para los datos iniciales porque es un formato muy fácil de
  crear y editar manualmente con cualquier hoja de cálculo.
- Decidí que el campo nombre tuviera 50 bytes porque considero que es
  suficiente para la mayoría de nombres de pilotos sin desperdiciar
  demasiado espacio.
