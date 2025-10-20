import java.nio.file.Path
import java.nio.file.Paths

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
                5 -> vaciarCrearFichero(archivoPath)
                6 -> importarJSON_Binario()
                7 -> {
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
