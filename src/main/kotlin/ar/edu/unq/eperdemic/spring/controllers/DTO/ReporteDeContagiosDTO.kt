package ar.edu.unq.eperdemic.spring.controllers.DTO

import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

class ReporteDeContagiosDTO(val vectoresPresentes:Int,
                            val vectoresInfecatods:Int,
                            val nombreDeEspecieMasInfecciosa: String,
                            val nombreDeUbicacion: String,
                            val nombreDelEquipo: String) {


   companion object {
       fun desdeModelo(reporte: ReporteDeContagios, nombreDeUbicacion: String, nombreDelEquipo: String) =
           ReporteDeContagiosDTO(
               vectoresPresentes = reporte.vectoresPresentes,
               vectoresInfecatods = reporte.vectoresInfectados,
               nombreDeEspecieMasInfecciosa = reporte.nombreDeEspecieMasInfecciosa,
               nombreDeUbicacion = nombreDeUbicacion,
               nombreDelEquipo = nombreDelEquipo
           )
   }
}