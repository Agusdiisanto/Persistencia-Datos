package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector

interface VectorService {
    fun infectar(vectorId: Long, especieId: Long)
    fun enfermedades(vectorId: Long): MutableSet<Especie>

    fun contagiar(vectorInfectado: Vector, vectores: MutableSet<Vector>)

    /* Operaciones CRUD */
    fun crearVector(tipo: TipoDeVector, ubicacionId: Long): Vector
    fun recuperarVector(vectorId: Long): Vector
    fun recuperarTodos(): MutableSet<Vector>
    fun borrarVector(vectorId: Long)
    fun clear()
}