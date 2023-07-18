package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.spring.controllers.DTO.EspecieDTO
import ar.edu.unq.eperdemic.spring.controllers.DTO.PatogenoDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@ServiceREST
@RequestMapping("/patogeno")
class PatogenoControllerREST(private val patogenoService: PatogenoService) {

  @PostMapping
  fun create(@RequestBody dto: PatogenoDTO): ResponseEntity<String> {
    return try {
      val patogeno = dto.aModelo()
      patogenoService.crearPatogeno(patogeno)
      ResponseEntity.ok("El Patogeno de tipo  ${patogeno.tipo} se creo correctamente")
    } catch (e: Exception) {
      ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Ocurrió un error al crear un Patogeno: ${e.message}")
    }
  }

  @PostMapping("/agregarEspecie/{ubicacionID}")
  fun agregarEspecie(@PathVariable ubicacionID: Long, @RequestBody especieDTO: EspecieDTO): ResponseEntity<String> {
    return try {
      var especie = especieDTO.aModelo(patogenoService.recuperarPatogeno(especieDTO.patogenoId!!))
      especie = patogenoService.agregarEspecie(especie.patogeno.id!!, especie.nombre, ubicacionID)
      val dto = EspecieDTO.desdeModelo(especie)
      ResponseEntity.ok("La especie ${dto.nombre} se agregó correctamente a la ubicación con ID $ubicacionID")
    } catch (e: Exception) {
      ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Ocurrió un error al agregar la especie a la ubicación: ${e.message}")
    }
  }

  @GetMapping("/{id}")
  fun getfindById(@PathVariable id: Long?) = PatogenoDTO.desdeModelo(patogenoService.recuperarPatogeno(id!!))

  @GetMapping("/patogenos")
  fun getAll() = patogenoService.recuperarATodosLosPatogenos().map{patogeno -> PatogenoDTO.desdeModelo(patogeno)}

  @GetMapping("/especies/{patogenoId}")
  fun getEspeciesDePatogeno(@PathVariable patogenoId : Long) = patogenoService.especiesDePatogeno(patogenoId).map { especie -> EspecieDTO.desdeModelo(especie) }

  @GetMapping("/cantidadInfectados/{especieId}")
  fun getCantidadInfectados(@PathVariable especieId: Long) = patogenoService.cantidadDeInfectados(especieId)

  @GetMapping("/esPandemia/{especieId}")
  fun esPandemia(@PathVariable especieId : Long) = patogenoService.esPandemia(especieId)






}