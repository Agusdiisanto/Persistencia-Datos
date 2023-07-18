package ar.edu.unq.eperdemic.modelo.utils.RandomMutacion

class ModoTestMutacion (
    private var capacidadDeBiomecanizacion: Int,
    private var defensaMicroorganismos: Int,
    private var randomTipoVector: Int,
    private var randomPotecia: Int,
    private var randomProbabilidadMutacion: Int
) : IRandomMutacion{

    override fun randomCapacidadBiomecanizacion(): Int {
        return capacidadDeBiomecanizacion
    }

    override fun randomDefensa(): Int {
        return defensaMicroorganismos
    }

    override fun randomPotencia(): Int {
        return randomPotecia
    }

    override fun randomTipoDeVector(): Int {
        return randomTipoVector
    }

    override fun randomProbabilidadDeMutacion(): Int {
        return randomProbabilidadMutacion
    }
}