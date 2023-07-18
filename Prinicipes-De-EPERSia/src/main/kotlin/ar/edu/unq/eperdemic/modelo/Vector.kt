package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.contagio.strategy.ContagioAnimal
import ar.edu.unq.eperdemic.modelo.contagio.strategy.ContagioInsecto
import ar.edu.unq.eperdemic.modelo.contagio.strategy.ContagioPersona
import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.RandomMutacion
import javax.persistence.*

@Entity
class Vector(var tipo: TipoDeVector,@ManyToOne var ubicacion: Ubicacion) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToMany(fetch = FetchType.EAGER)
    var especies: MutableSet<Especie> = mutableSetOf()

    @ManyToMany(mappedBy = "vectores", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var mutaciones: MutableSet<Mutacion> = mutableSetOf()

    init {
        ubicacion.vectores.add(this)
    }

    fun contieneEspecie(especieId: Long): Boolean {
        return especies.any { e: Especie -> e.id == especieId }
    }

    fun mover(nuevaUbicacion: Ubicacion) {
        ubicacion.vectores.remove(this)
        ubicacion = nuevaUbicacion
        ubicacion.vectores.add(this)
    }

    fun estaInfectado(): Boolean {
        return this.especies.isNotEmpty()
    }

    fun infectadoPor(especie: Especie) {
        especies.add(especie)
    }

    fun contagiarA(vector: Vector) {
        val contagioMap = mapOf(
            TipoDeVector.Persona to ContagioPersona(),
            TipoDeVector.Animal to ContagioAnimal(),
            TipoDeVector.Insecto to ContagioInsecto()
        )
        val contagio = contagioMap[vector.tipo] ?: error("No se encontró la estrategia para ${vector.tipo}")
        contagio.contagiar(vector, this, especies)
    }

    fun expandir() {
        contagiarATodos((ubicacion.vectores.filter { v -> v !== this }).toMutableSet())
    }

    fun contagiarATodos(vectores: MutableSet<Vector>) {
        vectores.forEach { vector ->
            this.contagiarA(vector)
        }
    }

    fun mutarSegunProbabilidad(mutacion : Mutacion, capacidadDeBiomecanizacion : Int){
        if(capacidadDeBiomecanizacion > RandomMutacion.randomProbabilidadDeMutacion()){
            mutacion.mutar(this)
        }
    }

    fun agregarMutacion(mutacion : Mutacion) {
        mutaciones.add(mutacion)
    }

    fun contieneMutacionSupresion(especie: Especie): Boolean {
        return mutaciones.any { m -> m.puedeSuprimirA(especie) }
    }

    fun tieneBioalteracionPara(especie: Especie, tipo: TipoDeVector): Boolean {
        return this.mutaciones.any { m -> m.esBioalteracionParaEspecieYTipo(especie, tipo) }
    }
}
