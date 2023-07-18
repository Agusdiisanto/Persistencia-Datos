package ar.edu.unq.eperdemic.modelo.utils.RandomEspecie

interface IRandomEspecie {
    fun contagioPersona(): Int
    fun contagioAnimal(): Int
    fun contagioInsecto(): Int
    fun random(): Int
    fun randomPosibildad(): Int
}