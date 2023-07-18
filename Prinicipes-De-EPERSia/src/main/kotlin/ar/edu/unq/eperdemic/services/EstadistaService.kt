package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

interface EstadistaService {
    fun especieLider(): Especie?
    fun lideres(): List<Especie>
    fun reporteDeContagios(nombreDeLaUbicacion: String): ReporteDeContagios
    fun clear()
}