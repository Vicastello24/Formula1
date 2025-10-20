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
@JacksonXmlRootElement(localName = "Pilotos")
data class PilotosWrapper(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Piloto")
    val listaPilotos: List<Piloto> = emptyList()
)


fun XmlAJson() {
    try {
        val entradaXML = Path.of("Datos/pilotos_f1.xml")
        val salidaJSON = Path.of("Datos/DatosF1_2.json")
        val datos: List<Piloto> = leerDatosInicialesXMLF1(entradaXML)
        for (dato in datos) {
            println(" - ID: ${dato.ID}, Nombre: ${dato.Nombre}, Escuderia: ${dato.Escuderia}, Dorsal: ${dato.Dorsal}, Victorias: ${dato.Victorias}, Podios: ${dato.Podios}, Puntos Medios por Temporada: ${dato.PuntosMedios}")
        }
        escribirDatosJSON(salidaJSON, datos)
    } catch (e: Exception) {
        e.printStackTrace() // Esto mostrará la causa real
    }
}
fun leerDatosInicialesXMLF1(ruta: Path): List<Piloto> {
    val fichero: File = ruta.toFile()
    val xmlMapper = XmlMapper().registerKotlinModule()
    val pilotosWrapper: PilotosWrapper = xmlMapper.readValue(fichero)
    return pilotosWrapper.listaPilotos
}
