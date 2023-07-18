package ar.edu.unq.eperdemic.modelo.utils.RandomEspecie

object RandomContagio {
    private var service: IRandomEspecie = ModoDefaultContagio()

    fun setModo(modo : IRandomEspecie){
        service = modo
    }

    fun randomContagioPersona(): Int{
        return service.contagioPersona()
    }

    fun randomContagioAnimal(): Int {
        return service.contagioAnimal()
    }

    fun randomContagioInsecto(): Int {
        return service.contagioInsecto()
    }

    fun random(): Int {
        return service.random()
    }

    fun randomPosibilidad(): Int {
        return service.randomPosibildad()
    }
}