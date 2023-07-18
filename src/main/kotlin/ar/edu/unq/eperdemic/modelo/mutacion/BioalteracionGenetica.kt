package ar.edu.unq.eperdemic.modelo.mutacion

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.RandomMutacion
import javax.persistence.*

@Entity
@DiscriminatorValue("Bioalteracion")
class BioalteracionGenetica: Mutacion() {
    var tipoDeVector: TipoDeVector

    init{
        this.tipoDeVector = this.randomVector()
    }

    private fun randomVector() : TipoDeVector{
        val tipos = arrayOf(TipoDeVector.Animal, TipoDeVector.Persona, TipoDeVector.Insecto)
        val randomIndex = RandomMutacion.randomTipoDeVector()
        return tipos[randomIndex]
    }

    override fun mutar(vector: Vector){
        this.vectores.add(vector)
        vector.agregarMutacion(this)
    }
    override fun esBioalteracionGenetica(): Boolean {
        return true
    }

    override fun esBioalteracionParaEspecieYTipo(especie: Especie, tipo: TipoDeVector): Boolean {
        return this.tipoDeVector() == tipo && this.especie == especie
    }

    override fun tipoDeVector(): TipoDeVector {
        return tipoDeVector
    }

    override fun tipoDeMutacion(): TipoDeMutacion {
        return TipoDeMutacion.Bioalteracion_Genetica
    }
}