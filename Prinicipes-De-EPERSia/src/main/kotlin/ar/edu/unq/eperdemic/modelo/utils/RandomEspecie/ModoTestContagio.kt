package ar.edu.unq.eperdemic.modelo.utils.RandomEspecie

class ModoTestContagio(
    private var contagioPersona: Int,
    private var contagioAnimal: Int,
    private var contagioInsecto: Int,
    private var random: Int,
    private var randomPosibilidad: Int
) : IRandomEspecie {

    override fun contagioPersona(): Int {
       return contagioPersona
    }

    override fun contagioAnimal(): Int {
        return contagioAnimal
    }

    override fun contagioInsecto(): Int {
        return contagioInsecto
    }

    override fun random(): Int {
        return random
    }

    override fun randomPosibildad(): Int {
        return randomPosibilidad
    }
}