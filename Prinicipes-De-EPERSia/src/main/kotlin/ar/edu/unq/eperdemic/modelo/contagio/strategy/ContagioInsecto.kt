package ar.edu.unq.eperdemic.modelo.contagio.strategy

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector

class ContagioInsecto : Contagio() {
    override fun contagiar(vectorContagiado : Vector, vectorOriginario : Vector, especies: MutableSet<Especie>) {
        if (vectorOriginario.tipo != TipoDeVector.Insecto){
            especies.forEach{ e -> e.infectarSegunProbabilidad(vectorContagiado, vectorOriginario) }
        } else {
            contagiarEspeciesBioalteradas(especies, vectorOriginario, vectorContagiado)
        }
    }
}