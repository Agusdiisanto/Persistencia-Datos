package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionFire

interface PatogenoService {
    fun crearPatogeno(patogeno: Patogeno): Patogeno
    fun recuperarPatogeno(id: Long): Patogeno
    fun recuperarATodosLosPatogenos(): List<Patogeno>
    fun agregarEspecie(patogenoId: Long, nombre: String, ubicacionId: Long): Especie
    fun especiesDePatogeno(patogenoId: Long): List<Especie>
    fun cantidadDeInfectados (especieId: Long): Int
    fun esPandemia (especieId: Long): Boolean
    fun clear()

}