package ar.edu.unq.eperdemic.modelo.utils.RandomMutacion

object RandomMutacion {
    private var service: IRandomMutacion = ModoDefaultMutacion()

    fun randomCapacidadBiomecanizacion(): Int{
        return service.randomCapacidadBiomecanizacion()
    }

    fun randomDefenseMicroorganismo(): Int {
        return service.randomDefensa()
    }

    fun randomTipoDeVector() : Int {
        return service.randomTipoDeVector()
    }

    fun randomPotencia() : Int{
        return service.randomPotencia()
    }

    fun randomProbabilidadDeMutacion(): Int {
        return service.randomProbabilidadDeMutacion()
    }

    fun setModo(modo : IRandomMutacion){
        service = modo
    }
}
