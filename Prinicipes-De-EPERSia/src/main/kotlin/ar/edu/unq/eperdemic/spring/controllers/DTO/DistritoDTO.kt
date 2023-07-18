package ar.edu.unq.eperdemic.spring.controllers.DTO

import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon

class DistritoDTO(
  val nombre : String ,
  val area : List<DoubleArray>
) {

  fun aModelo(): Distrito {
    return Distrito(nombre, GeoJsonPolygon(area.map{c -> GeoJsonPoint(c[0], c[1]) }))
  }

  companion object {
    fun desdeModelo(distrito: Distrito) =
      DistritoDTO(
        nombre = distrito.nombre,
        area =  distrito.area.points.map{p -> doubleArrayOf(p.y, p.x) }
      )
  }

}