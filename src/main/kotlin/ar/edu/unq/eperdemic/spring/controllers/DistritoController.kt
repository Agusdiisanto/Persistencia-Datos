package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.spring.controllers.DTO.DistritoDTO
import ar.edu.unq.eperdemic.spring.controllers.DTO.UbicacionDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@ServiceREST
@RequestMapping("/distrito")
class DistritoController(private val distritoService: DistritoService) {

  // Funciona
  @PostMapping
  fun crear(@RequestBody distritoDTO: DistritoDTO): ResponseEntity<String> {
    return try {
      val distrito = distritoDTO.aModelo()
      distritoService.crear(distrito)
      ResponseEntity.ok("El distrito ${distrito.nombre} se creo correctamente")
    } catch (e: Exception) {
      ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Ocurrió un error al crear el Distrito: ${e.message}")
    }
  }

  // Funciona
  @GetMapping("/masEnfermo")
  fun distritoMasEnfermo(): ResponseEntity<DistritoDTO> {
    val distritoMasEnfermo = distritoService.distritoMasEnfermo()
    val distritoDTO = DistritoDTO.desdeModelo(distritoMasEnfermo)
    return ResponseEntity.ok(distritoDTO)
  }


  @GetMapping("/distritos")
  fun getDistritos() = distritoService.recuperarTodos().map{dis -> DistritoDTO.desdeModelo(dis)}


}