package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.exceptions.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.spring.controllers.DTO.ConectarDTO
import ar.edu.unq.eperdemic.spring.controllers.DTO.UbicacionDTO
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException

@CrossOrigin
@ServiceREST
@RequestMapping("/ubicacion")
class UbicacionControllerREST (private val ubicacionService: UbicacionService) {

    // Funciona
    @PostMapping
    fun create(@RequestBody dto: UbicacionDTO): ResponseEntity<String> {
        return try {
            val ubicacion = dto.aModelo()
            ubicacionService.crearUbicacion(ubicacion.nombre, Coordenada(dto.latitud!!, dto.longitud!!))
            ResponseEntity.ok("La ubicacion se ha creado correctamente")
        }catch(e: NullPointerException){
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Campos 'latitud' o 'longitud' no especificados.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al crear una ubicacion: ${e.message}")
        }
    }


    // Funciona
    @GetMapping("/{ubicacionNombre}")
    fun recuperarUbicacion(@PathVariable ubicacionNombre : String):UbicacionDTO{
        val ubicacion = ubicacionService.recuperarUbicacion(ubicacionNombre)
        val coordenada = ubicacionService.recuperarCoordenadas(ubicacion.nombre)
        val alerta = ubicacionService.recuperarAlerta(ubicacion.nombre)
        return(UbicacionDTO.desdeModelo(ubicacion, coordenada, alerta))
    }

    // Funciona
    @GetMapping("/ubicaciones")
    fun getUbicaciones() = ubicacionService.recuperarTodos().map{ubi -> UbicacionDTO.desdeModelo(ubi, ubicacionService.recuperarCoordenadas(ubi.nombre), ubicacionService.recuperarAlerta(ubi.nombre))}


    @PutMapping("/mover/{vectorId}/{ubicacionNombre}")
    fun moverVector(@PathVariable vectorId : Long, @PathVariable ubicacionNombre : String): ResponseEntity<String> {
        return try {
            ubicacionService.mover(vectorId, ubicacionNombre)
            ResponseEntity.ok("El vector se movió correctamente")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al mover el vector: ${e.message}")
        }
    }

    @PutMapping("/moverMasCorto/{vectorId}/{ubicacionNombre}")
    fun moverMasCorto(@PathVariable vectorId : Long, @PathVariable ubicacionNombre : String) : ResponseEntity<String> {
        return try {
            val nombreUbicacion = ubicacionService.recuperarUbicacion(ubicacionNombre).nombre
            ubicacionService.moverMasCorto(vectorId, nombreUbicacion)
            ResponseEntity.ok("El vector se movió correctamente por el camino más corto.")
        } catch (e: EmptyResultDataAccessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("No existe la ubicación '${ubicacionNombre}'.")
        } catch (e: UbicacionNoAlcanzable) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("No hay caminos disponibles para el tipo de vector.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al mover el vector: ${e.message}")
        }
    }

    @PutMapping("/conectar")
    fun contectar(@RequestBody dto: ConectarDTO): ResponseEntity<String> {
        return try {
            ubicacionService.conectar(dto.ubicacionOrigen, dto.ubicacionDestino, dto.tipoDeCamino)
            ResponseEntity.ok("${dto.ubicacionOrigen} se conectó a la ${dto.ubicacionDestino} mediante el camino a ${dto.tipoDeCamino}")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al conectar las ubicaciones: ${e.message}")
        }
    }

    @GetMapping("/conectados/{ubicacionOrigen}")
    fun conectados(@PathVariable ubicacionOrigen : String) : List<UbicacionDTO>{
        return ubicacionService.conectados(ubicacionOrigen).map{ubi -> UbicacionDTO.desdeModelo(ubi, ubicacionService.recuperarCoordenadas(ubi.nombre), ubicacionService.recuperarAlerta(ubi.nombre))}
    }

    @PutMapping("expandir/{ubicacionNombre}")
    fun expandirEnUbicacion(@PathVariable ubicacionNombre: String): ResponseEntity<String> {
        return try {
            ubicacionService.expandir(ubicacionNombre)
            ResponseEntity.ok("La ubicación se expandió correctamente")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al expandir la ubicación: ${e.message}")
        }
    }

}