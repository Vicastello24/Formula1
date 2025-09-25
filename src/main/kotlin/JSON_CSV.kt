import java.nio.file.Files
import java.nio.file.Path
// Clases de la librería oficial de Kotlin para la serialización/deserialización.
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

//Usamos una 'data class' para representar la estructura de una planta e indicamos que es serializable



fun main() {
    val entradaJSON = Path.of("Datos/pilotos_f1.json")
    val salidaJSON = Path.of("Datos/pilotos_f1_deJson.csv")
    val datos: List<Piloto>
    datos = leerDatosInicialesJSONF1(entradaJSON)
    for (dato in datos) {
        println(" - ID: ${dato.ID}, Nombre: ${dato.Nombre}, Escuderia: ${dato.Escuderia}, Dorsal: ${dato.Dorsal}, Victorias: ${dato.Victorias}, Podios: ${dato.Podios}, Puntos Medios por Temporada: ${dato.PuntosMedios}")
    }
    escribirDatosCSVF1(salidaJSON, datos)
}
fun leerDatosInicialesJSONF1(ruta: Path): List<Piloto> {
    var pilotos: List<Piloto> =emptyList()
    val jsonString = Files.readString(ruta)
    pilotos = Json.decodeFromString<List<Piloto>>(jsonString)
    return pilotos
}
fun escribirDatosCSVF1(ruta: Path, pilotos: List<Piloto>){
    try {
        val fichero: File = ruta.toFile()
        csvWriter {
            delimiter = ';'
        }.writeAll(
            pilotos.map { piloto ->
                listOf(piloto.ID.toString(),
                    piloto.Nombre,
                    piloto.Escuderia,
                    piloto.Dorsal.toString(),
                    piloto.Victorias.toString(),
                    piloto.Podios.toString(),
                    piloto.PuntosMedios.toString())
            },
            fichero
        )
        println("\nInformación guardada en: $fichero")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}