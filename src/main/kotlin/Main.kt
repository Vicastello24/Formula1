import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.nio.file.Files
import java.nio.file.Path
import java.io.File
// Librería específica de Kotlin para leer y escribir ficheros CSV.
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
//Usamos una 'data class' para representar la estructura de una planta.
data class Piloto
    (@JacksonXmlProperty(localName = "ID")
     val ID: Int,
     @JacksonXmlProperty(localName = "Nombre")
     val Nombre: String,
     @JacksonXmlProperty(localName = "Escuderia")
     val Escudería: String,
     @JacksonXmlProperty(localName = "Dorsal")
     val Dorsal: Int,
     @JacksonXmlProperty(localName = "Victorias")
     val Victorias: Int,
     @JacksonXmlProperty(localName = "Podios")
     val Podios: Int,
     @JacksonXmlProperty(localName = "PuntosMedios")
     val PuntosMedios: Double)
@JacksonXmlProperty(localName = "pilotos")
//nombre del elemento raíz
fun main() {
    val entradaCSV = Path.of("Datos/DatosF1.csv")
    val salidaCSV = Path.of("Datos/DatosF1_2.csv")
    val datos: List<Piloto>
    datos = leerDatosInicialesCSV(entradaCSV)
    16
    for (dato in datos) {
        println(" - ID: ${dato.ID}, Nombre: ${dato.Nombre}, Escudería: ${dato.Escudería}, Dorsal: ${dato.Dorsal}, Victorias: ${dato.Victorias}, Podios: ${dato.Podios}, Puntos Medios por Temporada: ${dato.PuntosMedios}")
    }
    escribirDatosCSV(salidaCSV, datos)
}
fun leerDatosInicialesCSV(ruta: Path): List<Piloto>
{
    var pilotos: List<Piloto> =emptyList()
// Comprobar si el fichero es legible antes de intentar procesarlo.
    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero en la ruta: $ruta")
    } else{
// Configuramos el lector de CSV con el delimitador
        val reader = csvReader {
            delimiter = ';'
        }

        val filas: List<List<String>> = reader.readAll(ruta.toFile())

        pilotos = filas.mapNotNull { columnas ->
// Validar si La fila tiene al menos 4 columnas.
            if (columnas.size >= 7) {
                try {
                    val id = columnas[0].toInt()
                    val nombre = columnas[1]
                    val escuderia = columnas[2]
                    val dorsal = columnas[3].toInt()
                    val vict = columnas[4].toInt()
                    val podios = columnas[5].toInt()
                    val puntMed = columnas[6].toDouble()
                    Piloto(id,nombre, escuderia, dorsal,
                        vict,podios,puntMed) //crear el objeto Planta
                } catch (e: Exception) {
                    /* Si ocurre un error en la conversión (ej: NumberFormatException),
                    capturamos la excepción, imprimimos un aviso (opcional)
                    y devolvemos `null` para que `mapNotNull` descarte esta fila. */
                    println("Fila inválida ignorada: $columnas -> Error: ${e.message}")
                    null
                }
            } else {
                17
// Si la fila no tiene suficientes columnas, es inválida. Devolvemos null.
                println("Fila con formato incorrecto ignorada: $columnas")
                null
            }
        }
    }
    return pilotos
}
fun escribirDatosCSV(ruta: Path, pilotos: List<Piloto>){
    try {
        val fichero: File = ruta.toFile()
        csvWriter {
            delimiter = ';'
        }.writeAll(
            pilotos.map { piloto ->
                listOf(piloto.ID.toString(),
                    piloto.Nombre,
                    piloto.Escudería,
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