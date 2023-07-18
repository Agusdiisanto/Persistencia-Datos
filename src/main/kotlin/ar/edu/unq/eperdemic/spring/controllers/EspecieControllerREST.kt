package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.spring.controllers.DTO.EspecieDTO
import org.springframework.web.bind.annotation.*

@CrossOrigin
@ServiceREST
@RequestMapping("/especie")
class EspecieControllerREST (private val especieService: EspecieService){

    @GetMapping("/{especieId}")
    fun recuperarEspecie(@PathVariable especieId : Long) = EspecieDTO.desdeModelo(especieService.recuperarEspecie(especieId))


    @GetMapping("/especies")
    fun getEspecies() = especieService.recuperarTodos().map{especie -> EspecieDTO.desdeModelo(especie)}


    @GetMapping("/cantidadIfectados/{especieId}")
    fun getCantidadInfectados(@PathVariable especieId: Long) = especieService.cantidadInfectados(especieId)

}