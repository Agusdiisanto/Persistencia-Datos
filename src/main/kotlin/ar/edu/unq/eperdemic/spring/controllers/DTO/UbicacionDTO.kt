package ar.edu.unq.eperdemic.spring.controllers.DTO

import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion



class UbicacionDTO(val nombreDeLaUbicacion: String, val latitud : Double?, val longitud : Double?, var alerta: String? ){

    fun aModelo(): Ubicacion {
        return Ubicacion(nombreDeLaUbicacion)
    }

    companion object {
        fun desdeModelo(ubicacion: Ubicacion, coordenada: Coordenada, alerta: String) : UbicacionDTO {
            return UbicacionDTO(nombreDeLaUbicacion = ubicacion.nombre,
                latitud = coordenada.latitud!!,
                longitud = coordenada.longitud!!,
                alerta = alerta
            )
        }
    }
}