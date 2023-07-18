package ar.edu.unq.eperdemic.modelo.contagio.strategy

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

abstract class Contagio {
    abstract fun contagiar(vectorContagiado : Vector, vectorOriginario : Vector, especies: MutableSet<Especie>)

    protected fun contagiarEspeciesBioalteradas(
        especies: MutableSet<Especie>,
        vectorOriginario: Vector,
        vectorContagiado: Vector
    ) {
        especies.forEach { e ->
            if (vectorOriginario.tieneBioalteracionPara(e, vectorContagiado.tipo)) {
                e.infectarSegunProbabilidad(vectorContagiado, vectorOriginario)
            }
        }
    }
}