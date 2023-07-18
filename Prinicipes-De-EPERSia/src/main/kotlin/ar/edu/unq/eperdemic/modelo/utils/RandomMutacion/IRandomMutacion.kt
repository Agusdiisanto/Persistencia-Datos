package ar.edu.unq.eperdemic.modelo.utils.RandomMutacion

interface IRandomMutacion {
    fun randomCapacidadBiomecanizacion(): Int
    fun randomDefensa(): Int
    fun randomPotencia() : Int
    fun randomTipoDeVector() : Int
    fun randomProbabilidadDeMutacion() : Int
}