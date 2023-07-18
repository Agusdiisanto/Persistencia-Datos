package ar.edu.unq.eperdemic.modelo.ubicacion

import ar.edu.unq.eperdemic.modelo.exceptions.EmptyNameException
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.data.mongodb.core.mapping.Document

@Document("Distrito")
class Distrito(var nombre: String, var area: GeoJsonPolygon) {
    @Id
    var id : String? = null

    init {
        validarDistrito()
        validarArea()
    }

    private fun validarArea() {
        val puntos = area.points.toMutableList()
        val primerPunto = puntos.first()
        val ultimoPunto = puntos.last()
        if (primerPunto != ultimoPunto){
            puntos.add(primerPunto)
            area = GeoJsonPolygon(puntos)
        }
    }

    private fun validarDistrito() {
        if (this.nombre.isBlank()) {
            throw EmptyNameException("El distrito le falta un nombre")
        }
    }
}