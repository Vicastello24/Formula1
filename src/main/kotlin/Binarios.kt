import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.serialization.json.Json
import sun.security.krb5.Confounder.bytes
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
data class PilotoBinario(val id: Int, val nombre: String,val escuderia: String, val puntos_Medios: Double)
// Tamaño fijo para cada registro en el fichero
const val TAMANO_ID = Int.SIZE_BYTES // 4 bytes
const val TAMANO_NOMBRE = 200 // String de tamaño fijo 20 bytes
const val TAMANO_ESCUDERIA = 100
const val TAMANO_PUNTOS = Double.SIZE_BYTES // 8 bytes
const val TAMANO_REGISTRO = TAMANO_ID + TAMANO_NOMBRE + TAMANO_ESCUDERIA +TAMANO_PUNTOS
//Función que crea un fichero (si no existe) o lo vacía (si existe)
//Si el fichero existe: CREATE se ignora. TRUNCATE_EXISTING se activa y

//Si el fichero no existe: CREATE se activa y crea un fichero nuevo y
fun asegurarCarpeta(path: Path) {
    val parent = path.parent
    if (parent != null && !Files.exists(parent)) {
        Files.createDirectories(parent)
        println("Carpeta '${parent}' creada.")
    }
}
fun modificarPuntosPiloto(path: Path, idPiloto: Int, nuevosPuntos: Double) {
    FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE).use { canal ->
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
        var encontrado = false

        while (canal.read(buffer) > 0 && !encontrado) {
            val posicionActual = canal.position()
            buffer.flip()
            val id = buffer.getInt()

            if (id == idPiloto) {
                val posicionPuntos = posicionActual - TAMANO_REGISTRO + TAMANO_ID + TAMANO_NOMBRE + TAMANO_ESCUDERIA
                val bufferPuntos = ByteBuffer.allocate(TAMANO_PUNTOS)
                bufferPuntos.putDouble(nuevosPuntos)
                bufferPuntos.flip()

                canal.write(bufferPuntos, posicionPuntos)
                encontrado = true
            }
            buffer.clear()
        }

        if (encontrado) {
            println("Puntos del piloto con ID $idPiloto modificados a $nuevosPuntos")
        } else {
            println("No se encontró el piloto con ID $idPiloto")
        }
    }
}
fun importarJSON_Binario() {
    val rutaJSON: Path = Paths.get("Datos/pilotos_f1.json")  // Cámbiala si tu JSON tiene otro nombre
    val rutaBin: Path = Paths.get("Datos/pilotos.bin")

    if (!Files.exists(rutaJSON)) {
        println("El archivo JSON no existe en la ruta: $rutaJSON")
        return
    }

    try {
        val jsonString = Files.readString(rutaJSON)
        val listaPilotos: List<Piloto> = Json.decodeFromString(jsonString)

        listaPilotos.forEach { p ->
            val puntos = p.PuntosMedios ?: 0.0 // Si viene null, se pone a 0.0
            anadirPiloto(rutaBin, p.ID, p.Nombre, p.Escuderia, puntos)
        }

        println("Importación desde JSON completada.")
    } catch (e: Exception) {
        println("Error al importar desde JSON: ${e.message}")
    }
}
fun importarXML_Binario() {
    val rutaXML: Path = Paths.get("Datos/DatosF1.xml")  // Ajusta si se llama distinto
    val rutaBin: Path = Paths.get("Datos/pilotos.bin")

    if (!Files.exists(rutaXML)) {
        println("El archivo XML no existe en la ruta: $rutaXML")
        return
    }

    try {
        val xmlMapper = XmlMapper()

        // Si el XML tiene una lista dentro de una etiqueta principal, por ejemplo:
        // <Pilotos>
        //    <Piloto>...</Piloto>
        //    <Piloto>...</Piloto>
        // </Pilotos>
        //
        // Necesitarías una "wrapper class" o usar un tipo genérico:
        val listaPilotos: List<Piloto> = xmlMapper.readValue(rutaXML.toFile())

        listaPilotos.forEach { p ->
            // Usamos 0.0 si PuntosMedios es null
            val puntos = p.PuntosMedios ?: 0.0
            anadirPiloto(rutaBin, p.ID, p.Nombre, p.Escuderia, puntos)
        }

        println("Importación desde XML completada.")
    } catch (e: Exception) {
        println("Error al importar desde XML: ${e.message}")
    }
}
fun vaciarCrearFichero(path: Path) {
    try {
        FileChannel.open(
            path,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        ).close()
        println("El fichero '${path.fileName}' existe y está vacío.")
    } catch (e: Exception) {
        println("Error al vaciar o crear el fichero: ${e.message}")
    }
}
fun anadirPiloto(path: Path, id: Int, nombre: String, escuderia: String, puntos: Double) {
    val nuevoPiloto = PilotoBinario(id, nombre, escuderia, puntos)

    try {
        FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND).use { canal ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

            buffer.putInt(nuevoPiloto.id)

            val nombreBytes = nuevoPiloto.nombre.padEnd(TAMANO_NOMBRE, ' ').toByteArray(Charset.defaultCharset())
            buffer.put(nombreBytes, 0, TAMANO_NOMBRE)

            val escuderiaBytes = nuevoPiloto.escuderia.padEnd(TAMANO_ESCUDERIA, ' ').toByteArray(Charset.defaultCharset())
            buffer.put(escuderiaBytes, 0, TAMANO_ESCUDERIA)

            buffer.putDouble(nuevoPiloto.puntos_Medios)

            buffer.flip()
            while (buffer.hasRemaining()) canal.write(buffer)

            println("Piloto '${nuevoPiloto.nombre}' añadido con éxito.")
        }
    } catch (e: Exception) {
        println("Error al añadir el piloto: ${e.message}")
    }
}
fun leerPilotos(path: Path): List<PilotoBinario> {
    val pilotos = mutableListOf<PilotoBinario>()

    FileChannel.open(path, StandardOpenOption.READ).use { canal ->
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

        while (canal.read(buffer) > 0) {
            buffer.flip()

            val id = buffer.getInt()

            val nombreBytes = ByteArray(TAMANO_NOMBRE)
            buffer.get(nombreBytes)
            val nombre = String(nombreBytes, Charset.defaultCharset()).trim()

            val escuderiaBytes = ByteArray(TAMANO_ESCUDERIA)
            buffer.get(escuderiaBytes)
            val escuderia = String(escuderiaBytes, Charset.defaultCharset()).trim()

            val puntos = buffer.getDouble()

            pilotos.add(PilotoBinario(id, nombre, escuderia, puntos))

            buffer.clear()
        }
    }
    return pilotos
}
fun importarCSV_Binario(){
    val rutaBin: Path = Paths.get("Datos/pilotos.bin")
    val rutaCSV: Path = Paths.get("Datos/DatosF1_A_Binario.csv")
    if (!Files.isReadable(rutaCSV)) {
        println("Error: No se puede leer el fichero en la ruta: $rutaCSV")
    } else{
        val reader = csvReader {
            delimiter = ';'
        }

        val filas: List<List<String>> = reader.readAll(rutaCSV.toFile())

        filas.forEach { columnas ->
            if (columnas.size >= 4) {
                try {
                    val id = columnas[0].toInt()
                    val nombre = columnas[1]
                    val escuderia = columnas[2]
                    val puntMed = columnas[3].toDouble()
                    anadirPiloto(rutaBin, id, nombre, escuderia, puntMed)
                } catch (e: Exception) {
                    println("Fila inválida ignorada: $columnas -> Error: ${e.message}")
                }
            } else {
                println("Fila con formato incorrecto ignorada: $columnas")
            }
        }
    }
}
fun eliminarPiloto(path: Path, idPiloto: Int) {
    val pathTemporal = Paths.get(path.toString() + ".tmp")
    var pilotoEncontrado = false

    FileChannel.open(path, StandardOpenOption.READ).use { canalLectura ->
        FileChannel.open(pathTemporal, StandardOpenOption.WRITE, StandardOpenOption.CREATE).use { canalEscritura ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

            while (canalLectura.read(buffer) > 0) {
                buffer.flip()
                val id = buffer.getInt()

                if (id == idPiloto) {
                    pilotoEncontrado = true
                } else {
                    buffer.rewind()
                    canalEscritura.write(buffer)
                }
                buffer.clear()
            }
        }
    }

    if (pilotoEncontrado) {
        Files.move(pathTemporal, path, StandardCopyOption.REPLACE_EXISTING)
        println("Piloto con ID $idPiloto eliminado con éxito.")
    } else {
        Files.delete(pathTemporal)
        println("No se encontró ningún piloto con ID $idPiloto.")
    }
}

fun opBinaria() {
    val archivoPath: Path = Paths.get("Datos/pilotos.bin")
    val archivoCSV: Path = Paths.get("Datos/DatosF1_A_Binario.csv")
    asegurarCarpeta(archivoPath)
    importarCSV_Binario()
    val lista = listOf(
        PilotoBinario(1, "Fernando Alonso", "Aston Martin", 8.75),
        PilotoBinario(2, "Max Verstappen", "Red Bull", 9.90),
        PilotoBinario(3, "Charles Leclerc", "Ferrari", 8.30)
    )

    // Vaciar o crear el fichero
//    vaciarCrearFichero(archivoPath)
//
//    // Añadir pilotos
//    lista.forEach { piloto ->
//        anadirPiloto(archivoPath, piloto.id, piloto.nombre, piloto.escuderia, piloto.puntos_Medios)
//    }

    // Leer pilotos
    println("Pilotos leídos del fichero:")
    leerPilotos(archivoPath).forEach { p ->
        println(" - ID: ${p.id}, Nombre: ${p.nombre}, Escudería: ${p.escuderia}, Puntos Medios: ${"%.2f".format(p.puntos_Medios)}")
    }

    // Modificar puntos
    modificarPuntosPiloto(archivoPath, 2, 9.50)

    // Leer después de modificar
    println("Pilotos después de modificación:")
    leerPilotos(archivoPath).forEach { p ->
        println(" - ID: ${p.id}, Nombre: ${p.nombre}, Escudería: ${p.escuderia}, Puntos Medios: ${"%.2f".format(p.puntos_Medios)}")
    }

    // Eliminar piloto
    eliminarPiloto(archivoPath, 3)

    // Leer después de eliminar
    println("Pilotos después de eliminación:")
    leerPilotos(archivoPath).forEach { p ->
        println(" - ID: ${p.id}, Nombre: ${p.nombre}, Escudería: ${p.escuderia}, Puntos Medios: ${"%.2f".format(p.puntos_Medios)}")
    }
}

fun mostrarTodo(path: Path) {
    val pilotos = leerPilotos(path)
    if (pilotos.isEmpty()) {
        println("No hay registros en el fichero.")
    } else {
        println("\n--- LISTADO DE PILOTOS ---")
        pilotos.forEach { p ->
            println("ID: ${p.id}, Nombre: ${p.nombre}, Escudería: ${p.escuderia}, Puntos medios: ${"%.2f".format(p.puntos_Medios)}")
        }
    }
}

fun nuevoReg(path: Path) {
    try {
        print("Introduce ID (entero): ")
        val id = readLine()!!.toInt()

        print("Introduce nombre: ")
        val nombre = readLine()!!.trim()
        if (nombre.isEmpty()) {
            println("Nombre inválido.")
            return
        }

        print("Introduce escudería: ")
        val escuderia = readLine()!!.trim()
        if (escuderia.isEmpty()) {
            println("Escudería inválida.")
            return
        }

        print("Introduce puntos medios (decimal): ")
        val puntos = readLine()!!.toDouble()

        anadirPiloto(path, id, nombre, escuderia, puntos)

    } catch (e: NumberFormatException) {
        println("Error: formato numérico incorrecto.")
    } catch (e: Exception) {
        println("Error añadiendo registro: ${e.message}")
    }
}

fun modificar(path: Path) {
    try {
        print("Introduce el ID del piloto a modificar: ")
        val id = readLine()!!.toInt()

        print("Introduce los nuevos puntos medios (decimal): ")
        val nuevosPuntos = readLine()!!.toDouble()

        modificarPuntosPiloto(path, id, nuevosPuntos)

    } catch (e: NumberFormatException) {
        println("Error: formato numérico incorrecto.")
    } catch (e: Exception) {
        println("Error modificando registro: ${e.message}")
    }
}

fun eliminar(path: Path) {
    try {
        print("Introduce el ID del piloto a eliminar: ")
        val id = readLine()!!.toInt()
        eliminarPiloto(path, id)
    } catch (e: NumberFormatException) {
        println("Error: formato numérico incorrecto.")
    } catch (e: Exception) {
        println("Error eliminando registro: ${e.message}")
    }
}

fun main() {
    val archivoPath: Path = Paths.get("Datos/pilotos.bin")
    asegurarCarpeta(archivoPath)

    while (true) {
        println(
            """
            -------------------------
            MENÚ PRINCIPAL
            1. Mostrar todos los registros
            2. Añadir un nuevo registro
            3. Modificar un registro (por ID)
            4. Eliminar un registro (por ID)
            5. Salir
            -------------------------
            """
        )
        print("Opción: ")

        try {
            val opcion = readLine()!!.toInt()

            when (opcion) {
                1 -> mostrarTodo(archivoPath)
                2 -> nuevoReg(archivoPath)
                3 -> modificar(archivoPath)
                4 -> eliminar(archivoPath)
                5 -> {
                    println("Saliendo del programa...")
                    break
                }
                else -> println("Opción no válida, intenta de nuevo.")
            }

        } catch (e: NumberFormatException) {
            println("Por favor, introduce un número válido.")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        println()
    }
}


