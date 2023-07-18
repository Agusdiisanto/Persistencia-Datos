package ar.edu.unq.eperdemic.spring.controllers.DTO

import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.mutacion.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.mutacion.SupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.mutacion.TipoDeMutacion
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.RandomMutacion

class MutacionDTO(
    val tipoDeMutacion: TipoDeMutacion,
    val especieId: Long,
    val tipo : TipoDeVector?,
    val poderDeMutacion: Int?) {

    fun aModelo(): Mutacion {
        return when (tipoDeMutacion) {
            TipoDeMutacion.Supresion_Biomecanica -> SupresionBiomecanica().apply{
                potencia = poderDeMutacion?.let {
                    when {
                        it < 0 -> 0
                        it > 100 -> 100
                        else -> it
                    }
                } ?: RandomMutacion.randomPotencia()
            }
            TipoDeMutacion.Bioalteracion_Genetica -> BioalteracionGenetica().apply {
                tipoDeVector = tipo ?: TipoDeVector.values()[RandomMutacion.randomTipoDeVector()]
            }
        }
    }

    companion object {
        fun desdeModelo(mutacion: Mutacion): MutacionDTO {
            return MutacionDTO(
                tipoDeMutacion = mutacion.tipoDeMutacion(),
                especieId = mutacion.especie!!.id!!,
                tipo = mutacion.tipoDeVector(),
                poderDeMutacion = mutacion.potencia()
            )
        }
    }

}









