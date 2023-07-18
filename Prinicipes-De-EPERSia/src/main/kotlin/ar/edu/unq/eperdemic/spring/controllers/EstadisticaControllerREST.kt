package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.EstadistaService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.spring.controllers.DTO.EspecieDTO
import ar.edu.unq.eperdemic.spring.controllers.DTO.EspecieLiderDTO
import ar.edu.unq.eperdemic.spring.controllers.DTO.ReporteDeContagiosDTO
import org.springframework.web.bind.annotation.*
@CrossOrigin
@ServiceREST
@RequestMapping("/estadistica")
class EstadisticaControllerREST (private val estadisticaService: EstadistaService, private val patogenoService: PatogenoService) {

    @GetMapping("/especieLider")
    fun getEspecieLider(): EspecieLiderDTO {
        val especieLider = estadisticaService.especieLider()!!
        val nombre = especieLider.nombre
        val tipoPatogeno = especieLider.patogeno.tipo
        val cantidadInfectados = patogenoService.cantidadDeInfectados(especieLider.id!!)
        val esPandemia = patogenoService.esPandemia(especieLider.id!!)

        return EspecieLiderDTO.desdeModelo(nombre, tipoPatogeno, cantidadInfectados, esPandemia)
    }

    @GetMapping("/lideres")
    fun getLideres() = estadisticaService.lideres().map { especie -> EspecieDTO.desdeModelo(especie) }

    @GetMapping("/reporteDeContagios/{nombreUbicacion}")
    fun getReporteDeContagios( @PathVariable nombreUbicacion : String) =
        ReporteDeContagiosDTO.desdeModelo(estadisticaService.reporteDeContagios(nombreUbicacion), nombreUbicacion , "Principes De Persia")

}