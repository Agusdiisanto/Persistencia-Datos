package ar.edu.unq.eperdemic.modelo.utils.RandomEspecie

class ModoDefaultContagio : IRandomEspecie {
    override fun contagioPersona(): Int {
        return (0 until 100).random()
    }

    override fun contagioAnimal(): Int {
        return (0 until 100).random()
    }

    override fun contagioInsecto(): Int {
        return (0 until 100).random()
    }

    override fun random(): Int {
        return (0 until 10).random()
    }

    override fun randomPosibildad(): Int {
        return (0 until 100).random()
    }
}