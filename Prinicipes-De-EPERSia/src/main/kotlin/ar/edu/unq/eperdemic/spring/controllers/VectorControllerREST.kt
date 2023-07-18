package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.spring.controllers.DTO.EspecieDTO
import ar.edu.unq.eperdemic.spring.controllers.DTO.VectorDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@ServiceREST
@RequestMapping("/vector")
class VectorControllerREST (private val vectorService: VectorService, private val ubicacionService: UbicacionService) {

    @PostMapping
    fun create(@RequestBody dto: VectorDTO): ResponseEntity<String> {
        return try {
            val vector = dto.aModelo(ubicacionService.recuperarUbicacion(dto.nombreDeLaUbicacion!!))
            vectorService.crearVector(vector.tipo, vector.ubicacion.id!!)
            ResponseEntity.ok("El vector se a creado correctamente")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al crear un vector: ${e.message}")
        }
    }

    @GetMapping("/{vectorId}")
    fun recuperarVector(@PathVariable vectorId : Long) = VectorDTO.desdeModelo(vectorService.recuperarVector(vectorId))

    @GetMapping("/vectores")
    fun getVectores() = vectorService.recuperarTodos().map{vector -> VectorDTO.desdeModelo(vector)}

    @DeleteMapping("/deleteVector/{vectorId}")
    fun deleteVector(@PathVariable vectorId: Long): ResponseEntity<String> {
        return try {
            vectorService.borrarVector(vectorId)
            ResponseEntity("Vector $vectorId eliminado", HttpStatus.ACCEPTED)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al eliminar el vector: ${e.message}")
        }
    }

    @PutMapping("/infectar/{vectorId}/{especieId}")
    fun infectarVector(@PathVariable vectorId: Long, @PathVariable especieId: Long): ResponseEntity<String> {
        return try {
            vectorService.infectar(vectorId, especieId)
            ResponseEntity.ok("Se infectó el vector correctamente")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al infectar el vector: ${e.message}")
        }
    }

    @GetMapping("/enfermedades/{vectorId}")
    fun enfermedadesDeVector(@PathVariable vectorId : Long) = vectorService.enfermedades(vectorId).map { especie -> EspecieDTO.desdeModelo(especie) }

    @PutMapping("contagiar/{vectorId}")
    fun contagiarVectores(@PathVariable vectorId: Long, @RequestBody ids: List<Long>): ResponseEntity<String> {
        return try {
            var vectores = vectorService.recuperarTodos()
            vectores = vectores.filter { v -> ids.contains(v.id!!) }.toMutableSet()
            val vector = vectorService.recuperarVector(vectorId)
            vectorService.contagiar(vector, vectores)
            ResponseEntity("Contagio exitoso desde el vector $vectorId", HttpStatus.ACCEPTED)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al contagiar los vectores: ${e.message}")
        }
    }
}