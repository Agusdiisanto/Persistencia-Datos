package ar.edu.unq.eperdemic.modelo.mutacion

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.RandomMutacion
import javax.persistence.*

@Entity
@DiscriminatorValue("Supresion")
class SupresionBiomecanica: Mutacion() {
    var potencia: Int = 0

    init {
        potencia = RandomMutacion.randomPotencia()
    }

    private fun suprimirEspeciesDeVector(vector : Vector) {
        vector.especies = vector.especies.filter { e -> !vector.contieneMutacionSupresion(e) }.toMutableSet()
    }

    override fun mutar(vector: Vector) {
        vector.agregarMutacion(this)
        suprimirEspeciesDeVector(vector)
        this.vectores.add(vector)

    }

    override fun puedeSuprimirA(especie: Especie): Boolean {
       return this.potencia() > especie.defensaMicroorganismos
    }

    override fun potencia(): Int {
        return potencia
    }

    override fun tipoDeMutacion(): TipoDeMutacion {
        return TipoDeMutacion.Supresion_Biomecanica
    }

    override fun esSupresionBiomecanica(): Boolean {
        return true
    }
}