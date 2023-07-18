package ar.edu.unq.eperdemic.spring.controllers.DTO

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno

class EspecieDTO(
    val nombre: String,
    val paisDeOrigen: String,
    val patogenoId: Long?
) {


    fun aModelo(patogeno: Patogeno) : Especie {
        val especie = Especie(patogeno,this.nombre,this.paisDeOrigen)
        return especie
    }

   companion object {
     fun desdeModelo(especie: Especie) =
         EspecieDTO(
             nombre = especie.nombre,
             paisDeOrigen = especie.paisDeOrigen,
             patogenoId = especie.patogeno.id
         )
   }

}
