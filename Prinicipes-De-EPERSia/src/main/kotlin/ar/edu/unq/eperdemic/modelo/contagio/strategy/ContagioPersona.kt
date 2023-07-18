package ar.edu.unq.eperdemic.modelo.contagio.strategy

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

class ContagioPersona : Contagio() {

    override fun contagiar(vectorContagiado : Vector, vectorOriginario : Vector, especies: MutableSet<Especie>) {
            especies.forEach { e -> e.infectarSegunProbabilidad(vectorContagiado, vectorOriginario) }
    }
}