package ar.edu.unq.eperdemic.modeloTest

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoTestContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import org.junit.jupiter.api.*

class VectorTest {
    private val service : RandomContagio = RandomContagio
    private lateinit var vector1: Vector
    private lateinit var vector2: Vector

    private val patogeno = Patogeno("Pepe")
    private val patogeno1 = Patogeno("Julieta")

    private val tokio = Ubicacion("Tokio")
    private val londres = Ubicacion("Londres")

    private val hongoA = Especie(patogeno1,"Agus",londres.nombre)
    private val hongoB = Especie(patogeno,"Agus",tokio.nombre)


    private lateinit var vectorPersonaInfectada : Vector
    private lateinit var vectorInsectoinfectado : Vector
    private lateinit var vectorAnimalInfectado : Vector

    private lateinit var vectorPersona : Vector
    private lateinit var vectorInsecto : Vector
    private lateinit var vectorAnimal : Vector

    // Especies

    private lateinit var especieCovid : Especie
    private lateinit var especieHantavirus : Especie

    private lateinit var especieSars : Especie
    private lateinit var especieEbola : Especie

    @BeforeEach
    fun modelo(){
        vector1 = Vector(TipoDeVector.Persona, tokio)
        vector2 = Vector(TipoDeVector.Animal, londres)
        vectorPersonaInfectada = Vector(TipoDeVector.Persona, tokio)
        vectorInsectoinfectado = Vector(TipoDeVector.Insecto, tokio)
        vectorAnimalInfectado = Vector(TipoDeVector.Animal, tokio)

        vectorPersona = Vector(TipoDeVector.Persona, tokio)
        vectorInsecto = Vector(TipoDeVector.Insecto, tokio)
        vectorAnimal = Vector(TipoDeVector.Animal, tokio)
        vector1.infectadoPor(hongoA)

        service.setModo(ModoTestContagio(99,99,99,10,10))
        especieCovid = Especie(patogeno, "Covid", tokio.nombre)
        especieHantavirus = Especie(patogeno, "Hantavirus", tokio.nombre)

        service.setModo(ModoTestContagio(1,1,1,2,99))
        especieSars = Especie(patogeno, "Sars", tokio.nombre)
        especieEbola = Especie(patogeno, "Ebola", tokio.nombre)
    }

    @Test
    fun test01_UnVectorTieneUnTipoDeVector(){
        Assertions.assertEquals(TipoDeVector.Persona, vector1.tipo)
    }

    @Test
    fun test02_UnVectorTieneUnaUbicacion(){
        Assertions.assertEquals(tokio, vector1.ubicacion)
        Assertions.assertTrue(tokio.vectores.contains(vector1))
    }

    @Test
    fun test03_UnVectorEsInfectadoPorUnaEspecie(){
        vector2.infectadoPor(hongoA)

        Assertions.assertTrue(vector2.especies.contains(hongoA))
    }
    @Test
    fun test04_UnVectorContieneUnaEspecie(){
        Assertions.assertTrue(vector1.especies.contains(hongoA))
    }

    @Test
    fun test05_UnVectorNoContieneUnaEspecie(){
        Assertions.assertFalse(vector1.especies.contains(hongoB))
    }

    @Test
    fun test06_UnVectorSeMueveAUnaNuevaUbicacion(){
        vector1.mover(londres)
        Assertions.assertTrue(londres.vectores.contains(vector1))
        Assertions.assertFalse(tokio.vectores.contains(vector1))
        Assertions.assertEquals(londres, vector1.ubicacion)
    }

    @Test
    fun test07_UnVectorEstaInfectado(){
        Assertions.assertTrue(vector1.estaInfectado())
    }

    @Test
    fun test08_UnVectorNoEstaInfectado(){
        Assertions.assertFalse(vector2.estaInfectado())
    }

    @Test
    fun test09_unVectorContagiaAOtroVector(){
        val testing = ModoTestContagio(100,100,100,10,0)
        service.setModo(testing)

        vector2.infectadoPor(hongoB)
        vector2.contagiarA(vector1)

        Assertions.assertTrue(vector1.especies.contains(hongoB))
    }

    @Test
    fun test10_SeExpandeEnUnaUbicacionATodosLosVectores(){
        val testing = ModoTestContagio(100,100,100,10,0)
        service.setModo(testing)
        val hongoA = Especie(patogeno1,"Agus",londres.nombre)
        val vector3 = Vector(TipoDeVector.Persona, londres)
        val vector4 = Vector(TipoDeVector.Persona, londres)
        val vector5 = Vector(TipoDeVector.Persona, londres)
        vector3.infectadoPor(hongoA)

        vector3.expandir()
        val vectoresInfectados = londres.vectores.filter{ v -> v.especies.contains(hongoA) }
        Assertions.assertEquals(3, vectoresInfectados.size)
    }

    @Test
    fun test11_SeExpandeEnUnaUbicacionANingunoDeLosVectores(){
        val testing = ModoTestContagio(0,100,100,0,100)
        service.setModo(testing)
        val hongoA = Especie(patogeno1,"Covid",londres.nombre)
        val vector3 = Vector(TipoDeVector.Persona, londres)

        vector3.infectadoPor(hongoA)

        vector3.expandir()
        val vectoresInfectados = londres.vectores.filter{ v -> v.especies.contains(hongoA) }
        Assertions.assertEquals(1, vectoresInfectados.size)
    }

    @Test
    fun test12_SeExpandeEnUnaUbicacionSinVectores(){
        val testing = ModoTestContagio(100,100,100,0,0)
        service.setModo(testing)
        val hongoA = Especie(patogeno1,"Agus",londres.nombre)
        val vector3 = Vector(TipoDeVector.Persona, londres)
        vector3.infectadoPor(hongoA)
        vector3.expandir()
        val vectoresInfectados = londres.vectores.filter{ v -> v.especies.contains(hongoA) }
        Assertions.assertEquals(1, vectoresInfectados.size)
    }

    @Test
    fun test_seContagiaUnaListaDeVectoresVacia(){
        val vectores = mutableSetOf<Vector>()

        vectorPersona.infectadoPor(especieCovid)
        vectorPersona.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{v -> v.especies.contains(especieCovid)})
    }

    // TESTS DE PERSONA CONTAGIANDO OTROS VECTORES
    @Test
    fun test_unaPersonaNoContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        vectorPersonaInfectada.infectadoPor(especieEbola)
        vectorPersonaInfectada.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{ v -> v.especies.contains(especieEbola) })
    }

    @Test
    fun test_unaPersonaNoContagiaUnaListaConUnaSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        vectorPersonaInfectada.infectadoPor(especieEbola)
        vectorPersonaInfectada.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{ v -> v.especies.contains(especieEbola) })
    }

    @Test
    fun test_unaPersonaNuncaContagiaUnaListaConUnSoloAnimal(){
        val vectores = mutableSetOf(vectorAnimal)

        vectorPersonaInfectada.infectadoPor(especieCovid)
        vectorPersonaInfectada.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{ v -> v.especies.contains(especieCovid) })
    }

    @Test
    fun test_unaPersonaContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        vectorPersonaInfectada.infectadoPor(especieCovid)
        vectorPersonaInfectada.contagiarATodos(vectores)

        Assertions.assertTrue(vectores.any{ v -> v.especies.contains(especieCovid) })
        Assertions.assertEquals(1, vectores.filter{ v -> v.especies.contains(especieCovid) }.size)
    }

    @Test
    fun test_unaPersonaContagiaUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        vectorPersonaInfectada.infectadoPor(especieCovid)
        vectorPersonaInfectada.contagiarATodos(vectores)

        Assertions.assertTrue(vectores.any{v -> v.especies.contains(especieCovid)})
        Assertions.assertEquals(1, vectores.filter{v -> v.especies.contains(especieCovid)}.size)
    }

    // TESTS DE INSECTO CONTAGIANDO OTROS VECTORES
    @Test
    fun test_unInsectoNoContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        vectorInsectoinfectado.infectadoPor(especieEbola)
        vectorInsectoinfectado.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{v -> v.especies.contains(especieEbola)})
    }

    @Test
    fun test_unInsectoNoContagiaUnaListaConUnSoloAnimal(){
        val vectores = mutableSetOf(vectorAnimal)

        vectorInsectoinfectado.infectadoPor(especieEbola)
        vectorInsectoinfectado.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{v -> v.especies.contains(especieEbola)})
    }

    @Test
    fun test_unInsectoNuncaContagiaUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        vectorInsectoinfectado.infectadoPor(especieCovid)
        vectorInsectoinfectado.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{v -> v.especies.contains(especieCovid)})
    }

    @Test
    fun test_unInsectoContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        vectorInsectoinfectado.infectadoPor(especieCovid)
        vectorInsectoinfectado.contagiarATodos(vectores)

        Assertions.assertTrue(vectores.any{v -> v.especies.contains(especieCovid)})
        Assertions.assertEquals(1, vectores.filter{v -> v.especies.contains(especieCovid)}.size)
    }

    @Test
    fun test_unInsectoContagiaUnaListaConUnSoloAnimal(){
        val vectores = mutableSetOf(vectorAnimal)

        vectorInsectoinfectado.infectadoPor(especieCovid)
        vectorInsectoinfectado.contagiarATodos(vectores)

        Assertions.assertTrue(vectores.any{v -> v.especies.contains(especieCovid)})
        Assertions.assertEquals(1, vectores.filter{v -> v.especies.contains(especieCovid)}.size)
    }

    // TESTS DE ANIMAL CONTAGIANDO OTROS VECTORES
    @Test
    fun test_unAnimalNoContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        vectorAnimalInfectado.infectadoPor(especieEbola)
        vectorAnimalInfectado.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{v -> v.especies.contains(especieEbola)})
    }

    @Test
    fun test_unAnimalNoContagiaUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        vectorAnimalInfectado.infectadoPor(especieEbola)
        vectorAnimalInfectado.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{v -> v.especies.contains(especieEbola)})
    }

    @Test
    fun test_unAnimalNuncaContagiaUnaListaConUnSoloAnimal(){
        val vectores = mutableSetOf(vectorAnimal)

        vectorAnimalInfectado.infectadoPor(especieCovid)
        vectorAnimalInfectado.contagiarATodos(vectores)

        Assertions.assertFalse(vectores.any{v -> v.especies.contains(especieCovid)})
    }

    @Test
    fun test_unAnimalContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        vectorAnimalInfectado.infectadoPor(especieCovid)
        vectorAnimalInfectado.contagiarATodos(vectores)

        Assertions.assertTrue(vectores.any{v -> v.especies.contains(especieCovid)})
        Assertions.assertEquals(1, vectores.filter{v -> v.especies.contains(especieCovid)}.size)
    }

    @Test
    fun test_unAnimalContagiaUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        vectorAnimalInfectado.infectadoPor(especieCovid)
        vectorAnimalInfectado.contagiarATodos(vectores)

        Assertions.assertTrue(vectores.any{v -> v.especies.contains(especieCovid)})
        Assertions.assertEquals(1, vectores.filter{v -> v.especies.contains(especieCovid)}.size)
    }

    // Con varias especies
    @Test
    fun test_unAnimalContagiaConDosEspeciesAUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        vectorAnimalInfectado.infectadoPor(especieCovid)
        vectorAnimalInfectado.infectadoPor(especieHantavirus)
        vectorAnimalInfectado.contagiarATodos(vectores)

        Assertions.assertTrue(vectores.any{v -> v.especies.contains(especieCovid)})
        Assertions.assertTrue(vectores.any{v -> v.especies.contains(especieHantavirus)})
        Assertions.assertEquals(1, vectores.filter{v -> v.especies.contains(especieCovid)}.size)
    }

    // Infectar a varios a la vez
    @Test
    fun test_unAnimalContagiaUnaListaDeVectoresDeDistintoTipoConDosEspeciesSiCorresponde(){
        val vectores = mutableSetOf(vectorPersona, vectorInsecto, vectorAnimal)

        vectorAnimalInfectado.infectadoPor(especieCovid)
        vectorAnimalInfectado.infectadoPor(especieHantavirus)
        vectorAnimalInfectado.contagiarATodos(vectores)

        Assertions.assertEquals(2, vectores.filter{v -> v.estaInfectado()}.size)
        Assertions.assertFalse(vectorAnimal.estaInfectado())
    }

    @Test
    fun test_unAnimalContagiaUnaListaDePersonasSiCorresponde(){
        val vectorPersona2 = Vector(TipoDeVector.Persona, tokio)
        val vectores = mutableSetOf(vectorPersona, vectorPersona2)

        vectorAnimalInfectado.infectadoPor(especieCovid)
        vectorAnimalInfectado.infectadoPor(especieEbola)
        vectorAnimalInfectado.contagiarATodos(vectores)

        Assertions.assertEquals(2, vectores.filter{v -> v.especies.contains(especieCovid)}.size)
        Assertions.assertEquals(0, vectores.filter{v -> v.especies.contains(especieEbola)}.size)
    }

    @Test
    fun test_unInsectoContagiaUnaListaDeAnimalesSiCorresponde(){
        val vector3 = Vector(TipoDeVector.Animal, tokio)
        val vectores = mutableSetOf(vectorAnimal, vector3)

        vectorInsectoinfectado.infectadoPor(especieCovid)
        vectorInsectoinfectado.infectadoPor(especieEbola)
        vectorInsectoinfectado.contagiarATodos(vectores)

        Assertions.assertEquals(2, vectores.filter{v -> v.especies.contains(especieCovid)}.size)
        Assertions.assertEquals(0, vectores.filter{v -> v.especies.contains(especieEbola)}.size)
    }

    @Test
    fun test_unInsectoContagiaAlgunosVectoresDeUnaLista(){
        val vector3 = Vector(TipoDeVector.Animal, tokio)
        val vectores = mutableSetOf(vectorAnimal, vector3)

        vectorInsectoinfectado.infectadoPor(especieCovid)
        vectorInsectoinfectado.infectadoPor(especieEbola)
        vectorInsectoinfectado.contagiarATodos(vectores)

        Assertions.assertEquals(2, vectores.filter{v -> v.especies.contains(especieCovid)}.size)
        Assertions.assertEquals(0, vectores.filter{v -> v.especies.contains(especieEbola)}.size)
    }

    @Test
    fun test_unInsectoContagiaUnaListaDeVectoresDeDistintoTipoConDosEspeciesSiCorresponde(){
        val vectores = mutableSetOf(vectorPersona, vectorInsecto, vectorAnimal)

        vectorInsectoinfectado.infectadoPor(especieCovid)
        vectorInsectoinfectado.infectadoPor(especieHantavirus)
        vectorInsectoinfectado.contagiarATodos(vectores)

        Assertions.assertEquals(2, vectores.filter{v -> v.estaInfectado()}.size)
        Assertions.assertFalse(vectorInsecto.estaInfectado())
    }

    @Test
    fun test_unaPersonaContagiaUnaListaDeVectoresDeDistintoTipoConDosEspeciesSiCorresponde(){
        val vectores = mutableSetOf(vectorPersona, vectorInsecto, vectorAnimal)

        vectorPersonaInfectada.infectadoPor(especieCovid)
        vectorPersonaInfectada.infectadoPor(especieHantavirus)
        vectorPersonaInfectada.contagiarATodos(vectores)

        Assertions.assertEquals(2, vectores.filter{v -> v.estaInfectado()}.size)
        Assertions.assertFalse(vectorAnimal.estaInfectado())
    }
}