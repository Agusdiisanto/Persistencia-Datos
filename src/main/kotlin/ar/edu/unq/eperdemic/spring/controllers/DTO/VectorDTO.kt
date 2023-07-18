package ar.edu.unq.eperdemic.spring.controllers.DTO

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion

class VectorDTO(
    val tipoDeVector: TipoDeVector,
    val nombreDeLaUbicacion: String?,
    val ubicacionId : Long ) {

    fun aModelo(ubicacion: Ubicacion): Vector {
        return Vector(tipoDeVector, ubicacion)
    }

    companion object {
           fun desdeModelo(vector : Vector) =
               VectorDTO(
                   tipoDeVector = vector.tipo,
                   nombreDeLaUbicacion = vector.ubicacion.nombre,
                   ubicacionId = vector.ubicacion.id!!
               )
    }

}