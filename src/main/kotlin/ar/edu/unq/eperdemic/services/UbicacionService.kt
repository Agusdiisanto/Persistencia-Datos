package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.ubicacion.Alerta
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion

interface UbicacionService {
    fun mover(vectorId: Long, ubicacionNombre: String)
    fun expandir(ubicacionNombre: String)
    fun conectar(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: String)
    fun conectados(nombreDeUbicacion: String): List<Ubicacion>
    fun moverMasCorto(vectorId: Long, nombreDeUbicacion: String)
    /* Operaciones CRUD*/
    fun crearUbicacion(nombreUbicacion: String, coordenada: Coordenada): Ubicacion
    fun recuperarTodos(): List<Ubicacion>
    fun recuperarUbicacion(ubicacionNombre: String): Ubicacion
    fun clear()
    fun recuperarCoordenadas(ubicacionNombre: String): Coordenada

    fun recuperarAlerta(nombre: String): String
    fun ubicacionesConAlerta(alerta: Alerta): List<Ubicacion>
}