package ar.edu.unq.eperdemic.modelo.utils.RandomMutacion

class ModoDefaultMutacion : IRandomMutacion {
    override fun randomCapacidadBiomecanizacion(): Int {
        return (0 until 100).random()
    }

    override fun randomDefensa(): Int {
        return (0 until 100).random()
    }

    override fun randomPotencia(): Int {
        return (0 until 100).random()
    }

    override fun randomTipoDeVector(): Int {
        return (0 until 2).random()
    }

    override fun randomProbabilidadDeMutacion(): Int {
        return (0 until 100).random()
    }
}