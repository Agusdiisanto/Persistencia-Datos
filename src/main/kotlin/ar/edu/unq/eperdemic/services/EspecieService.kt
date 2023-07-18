package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.modelo.Vector
import org.springframework.stereotype.Component


interface EspecieService {
    fun recuperarEspecie(id: Long): Especie
    fun recuperarTodos(): List<Especie>
    fun cantidadInfectados(especieId : Long): Int
    fun clear()
}