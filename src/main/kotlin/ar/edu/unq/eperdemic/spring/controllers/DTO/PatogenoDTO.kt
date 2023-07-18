package ar.edu.unq.eperdemic.spring.controllers.DTO

import ar.edu.unq.eperdemic.modelo.Patogeno

class PatogenoDTO (val patogenoID:Long?, val tipo : String, val cantidadDeEspecies:Int?){


    fun aModelo(): Patogeno {
        val patogeno = Patogeno(this.tipo)
        if (this.patogenoID != null) {
            patogeno.setId(patogenoID)
        }
        return patogeno
    }

    companion object {
        fun desdeModelo(patogeno: Patogeno) =
            PatogenoDTO(
                patogenoID = patogeno.id,
                tipo = patogeno.tipo,
                cantidadDeEspecies = patogeno.especies.size
            )
    }
}