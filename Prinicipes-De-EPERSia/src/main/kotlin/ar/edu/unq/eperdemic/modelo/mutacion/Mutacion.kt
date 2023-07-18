package ar.edu.unq.eperdemic.modelo.mutacion

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipoDeMutacion", discriminatorType = DiscriminatorType.STRING)

abstract class Mutacion{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    @ManyToOne
    var especie: Especie? = null

    @ManyToMany(fetch = FetchType.EAGER)
    var vectores: MutableSet<Vector> = mutableSetOf()
    
    abstract fun mutar(vector: Vector)

    open fun esSupresionBiomecanica(): Boolean {
        return false
    }

    open fun puedeSuprimirA(especie: Especie): Boolean{
        return false
    }

    open fun esBioalteracionGenetica(): Boolean {
        return false
    }

    open fun esBioalteracionParaEspecieYTipo(especie: Especie, tipo: TipoDeVector): Boolean {
        return false
    }

    open fun potencia(): Int? {
        return null
    }

    open fun tipoDeVector(): TipoDeVector? {
        return null
    }

    fun contieneVector(vector : Vector): Boolean{
        return vectores.any{v -> v.id == vector.id}
    }

    abstract fun tipoDeMutacion(): TipoDeMutacion
}