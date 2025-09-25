import java.nio.file.Files
import java.nio.file.Path
// Clases de la librería oficial de Kotlin para la serialización/deserialización.
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
//Usamos una 'data class' para representar la estructura de una planta e indicamos que es serializable



fun main() {
    val entradaJSON = Path.of("Datos/pilotos_f1.json")
    val salidaJSON = Path.of("Datos/pilotos_f1_2.json")
    val datos: List<Piloto>
    datos = leerDatosInicialesJSON(entradaJSON)
    for (dato in datos) {
        println(" - ID: ${dato.ID}, Nombre: ${dato.Nombre}, Escuderia: ${dato.Escuderia}, Dorsal: ${dato.Dorsal}, Victorias: ${dato.Victorias}, Podios: ${dato.Podios}, Puntos Medios por Temporada: ${dato.PuntosMedios}")
    }
    escribirDatosJSON(salidaJSON, datos)
}
fun leerDatosInicialesJSON(ruta: Path): List<Piloto> {
    var pilotos: List<Piloto> =emptyList()
    val jsonString = Files.readString(ruta)
    pilotos = Json.decodeFromString<List<Piloto>>(jsonString)
    return pilotos
}
fun escribirDatosJSON(ruta: Path, pilotos: List<Piloto>) {
    try {
        /* La librería `kotlinx.serialization`
        toma la lista de objetos `Planta` (`List<Planta>`) y la convierte en una
        única cadena de texto con formato JSON.
        `prettyPrint` formatea el JSON para que sea legible. */
        val json = Json { prettyPrint = true }.encodeToString(pilotos)
// Con `Files.writeString` escribimos el String JSON en el fichero de salida
        Files.writeString(ruta, json)
        println("\nInformación guardada en: $ruta")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}