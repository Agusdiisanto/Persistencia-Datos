package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.spring.controllers.DTO.MutacionDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@ServiceREST
@RequestMapping("/mutacion")
class MutacionControllerREST (private val mutacionService: MutacionService) {

    @PostMapping
    fun create(@RequestBody mutacionDTO: MutacionDTO): ResponseEntity<String> {
        return try {
            val mutacion = mutacionDTO.aModelo()
            mutacionService.agregarMutacion(mutacionDTO.especieId, mutacion)
            ResponseEntity.ok("La mutacion ${mutacion.tipoDeMutacion()} se creo correctamente")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al crear una Mutacion: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    fun recuperarMutacion(@PathVariable id: Long?) = MutacionDTO.desdeModelo(mutacionService.recuperarMutacion(id!!))


    @GetMapping("/mutaciones")
    fun recuperarTodos() = mutacionService.recuperarTodos().map{mutacion -> MutacionDTO.desdeModelo(mutacion)}
}