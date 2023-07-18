package ar.edu.unq.eperdemic.modelo

enum class TipoDeVector(val rutasTransitables: List<String>) {
    Animal(listOf("RUTA_TERRESTRE", "RUTA_MARITIMA", "RUTA_AEREA")),
    Persona(listOf("RUTA_TERRESTRE", "RUTA_MARITIMA")),
    Insecto(listOf("RUTA_TERRESTRE", "RUTA_AEREA"))
}