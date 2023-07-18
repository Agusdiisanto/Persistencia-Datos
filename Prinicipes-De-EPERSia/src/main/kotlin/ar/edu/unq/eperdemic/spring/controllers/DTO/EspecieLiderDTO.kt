package ar.edu.unq.eperdemic.spring.controllers.DTO

class EspecieLiderDTO(
    val especie_nombre: String?,
    val especie_patogeno: String?,
    val cantidadInfectados: Int?,
    val esPandemia: Boolean?
) {

    companion object {
        fun desdeModelo(
            especieNombre: String?,
            patogenoNombre: String?,
            cantidadDeInfectados: Int?,
            esPandemia: Boolean?
        ) = EspecieLiderDTO(
            especie_nombre = especieNombre,
            especie_patogeno = patogenoNombre,
            cantidadInfectados = cantidadDeInfectados,
            esPandemia = esPandemia
        )
    }
}