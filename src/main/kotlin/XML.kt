import java.nio.file.Path
import java.io.File
// Anotaciones y clases de la librería Jackson para el mapeo a XML.
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
/*Representa la estructura de una única planta. La propiedad 'id_planta' será la
etiqueta <id_planta>...</id_planta> (así todas) */
//nombre del elemento raíz
// Data class que representa el elemento raíz del XML.

data class Pilotos(
    @JacksonXmlElementWrapper(useWrapping = false) // No necesitamos la etiqueta <plantas> aquí
    @JacksonXmlProperty(localName = "piloto")
    val listaPilotos: List<Piloto> = emptyList()
)


fun main() {
    val entradaXML = Path.of("Datos/pilotos_f1.xml")
    val salidaXML = Path.of("Datos/DatosF1_2.xml")
    val datos: List<Piloto>
    datos = leerDatosInicialesXML(entradaXML)
    for (dato in datos) {
        println(" - ID: ${dato.ID}, Nombre: ${dato.Nombre}, Escuderia: ${dato.Escuderia}, Dorsal: ${dato.Dorsal}, Victorias: ${dato.Victorias}, Podios: ${dato.Podios}, Puntos Medios por Temporada: ${dato.PuntosMedios}")
    }
    escribirDatosXML(salidaXML, datos)
}
fun leerDatosInicialesXML(ruta: Path): List<Piloto> {
//var plantas: List<Planta> =emptyList()
    val fichero: File = ruta.toFile()
// Deserializar el XML a objetos Kotlin
    val xmlMapper = XmlMapper().registerKotlinModule()
// 'readValue' convierte el contenido XML en una instancia de la clase 'Plantas'
    val pilotosWrapper: Pilotos = xmlMapper.readValue(fichero)
    return pilotosWrapper.listaPilotos
}
fun escribirDatosXML(ruta: Path,pilotos: List<Piloto>) {
    try {
        val fichero: File = ruta.toFile()
// Creamos instancia de la clase 'Plantas' (raíz del XML).
        val contenedorXml = Pilotos(pilotos)
// Configuramos el 'XmlMapper' (motor de Jackson) para la conversión a XML.
        val xmlMapper = XmlMapper().registerKotlinModule()
// Convertimos 'contenedorXml' en un String con formato XML.
// .writerWithDefaultPrettyPrinter() formatea con indentación y saltos de línea
        val xmlString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contenedorXml)
// escribir un String en un fichero con 'writeText'
        fichero.writeText(xmlString)
        println("\nInformación guardada en: $fichero")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}