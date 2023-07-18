package ar.edu.unq.eperdemic.modeloTest

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.mutacion.*
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoTestContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.ModoTestMutacion
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.RandomMutacion
import ar.edu.unq.eperdemic.modelo.exceptions.InfectionRejectedException
import org.junit.jupiter.api.*


class MutacionTest {
    private lateinit var supresion : Mutacion
    private lateinit var bioalteracion0 : Mutacion
    private lateinit var bioalteracion : Mutacion
    private lateinit var bioalteracion1 : Mutacion
    private lateinit var especie1 : Especie
    private lateinit var especie2 : Especie
    private lateinit var persona : Vector
    private lateinit var insecto : Vector
    private lateinit var animal : Vector
    private lateinit var patogeno : Patogeno
    private lateinit var londres : Ubicacion
    private lateinit var china : Ubicacion
    private var service: RandomMutacion = RandomMutacion
    private var serviceContagio: RandomContagio = RandomContagio

    @BeforeEach
    fun setUp() {
        service.setModo(ModoTestMutacion(0, 0, 0, 2, 100))
        bioalteracion0 = BioalteracionGenetica()
        service.setModo(ModoTestMutacion(100, 0, 0, 2, 0))
        supresion = SupresionBiomecanica()
        bioalteracion = BioalteracionGenetica()
        bioalteracion1 = BioalteracionGenetica()
        patogeno = Patogeno("Virus")
        serviceContagio.setModo(ModoTestContagio(0, 0, 0, 100, 0))
        especie1 = Especie(patogeno, "HongoA", "China")
        bioalteracion.especie = especie1
        service.setModo(ModoTestMutacion(100, 4, 1, 2, 0))
        especie2 = Especie(patogeno, "HongoB", "Inglaterra")
        londres = Ubicacion("Londres")
        china = Ubicacion("China")
        persona = Vector(TipoDeVector.Persona, londres)
        insecto = Vector(TipoDeVector.Insecto, china)
        animal = Vector(TipoDeVector.Animal, china)
    }

    @Test
    fun test01_CuandoUnVectorMutaConSupresionBiomecanicaLoTieneEnMutaciones(){
        supresion.mutar(persona)

        val mutacionesDeVector = persona.mutaciones

        Assertions.assertTrue(mutacionesDeVector.contains(supresion))
        Assertions.assertTrue(supresion.vectores.contains(persona))
    }

    @Test
    fun test02_alMutarConSupresionBiomecanicaUnVectorEliminaASusEspecies(){
        especie1.infectaA(persona)

        Assertions.assertEquals(1, persona.especies.size)

        supresion.mutar(persona)
        Assertions.assertTrue(persona.mutaciones.contains(supresion))
        Assertions.assertEquals(0, persona.especies.size)
    }

    @Test
    fun test03_alMutarConSupresionBiomecanicaUnVectorImpideAfectarseConEspeciesMasDebiles(){
        supresion.mutar(persona)

        Assertions.assertThrows(InfectionRejectedException::class.java) { especie1.infectarSiCorresponde(persona) }
    }

    @Test
    fun test04_alMutarConSupresionBiomecanicaNoImpideAfectarseConEspeciesMasFuertes(){
        supresion.mutar(persona)
        especie2.infectarSiCorresponde(persona)

        Assertions.assertTrue(especie2.vectores.contains(persona))
    }

    @Test
    fun test05_alMutarConSupresionBiomecanicaNoEliminaLasEspeciesMasFuertesDelVector(){
        especie2.infectarSiCorresponde(persona)

        Assertions.assertTrue(persona.especies.contains(especie2))

        supresion.mutar(persona)

        Assertions.assertTrue(especie2.vectores.contains(persona))
    }

    @Test
    fun test06_CuandoUnVectorMutaConBioalteracionGeneticaLoTieneEnMutaciones(){
        bioalteracion.mutar(persona)

        val mutacionesDeVector = persona.mutaciones

        Assertions.assertTrue(mutacionesDeVector.contains(bioalteracion))
        Assertions.assertTrue(bioalteracion.vectores.contains(persona))
    }

    @Test
    fun test07_CuandoUnaPersonaMutaConBioalteracionGeneticaPuedeContagiarAnimales(){
        especie1.infectaA(persona)
        bioalteracion.mutar(persona)
        persona.contagiarA(animal)

        val especiesDelAnimal = animal.especies

        Assertions.assertTrue(especiesDelAnimal.contains(especie1))
    }

    @Test
    fun test08_CuandoUnaPersonaMutaConBioalteracionGeneticaNoPuedeContagiarAnimales(){
        service.setModo(ModoTestMutacion(0, 0, 2, 2, 100))
        val bioalteracionInsecto = BioalteracionGenetica()
        especie2.agregarMutacion(bioalteracionInsecto)
        especie2.infectaA(persona)
        bioalteracionInsecto.mutar(persona)
        persona.contagiarA(animal)

        val especiesDelAnimal = animal.especies

        Assertions.assertFalse(especiesDelAnimal.contains(especie2))
    }

    @Test
    fun test09_CuandoUnAnimalMutaConBioalteracionGeneticaPuedeContagiarAnimales(){
        service.setModo(ModoTestMutacion(0, 0, 0, 2, 100))
        val bioalteracionAnimal = BioalteracionGenetica()
        val animal2 = Vector(TipoDeVector.Animal, londres)
        especie2.agregarMutacion(bioalteracionAnimal)
        especie2.infectaA(animal)
        bioalteracionAnimal.mutar(animal)
        animal.contagiarA(animal2)

        val especiesDelAnimal = animal2.especies

        Assertions.assertTrue(especiesDelAnimal.contains(especie2))
    }

    @Test
    fun test10_CuandoUnAnimalMutaConBioalteracionGeneticaNoPuedeContagiarAnimales(){
        service.setModo(ModoTestMutacion(0, 0, 1, 2, 100))
        val bioalteracionPersona = BioalteracionGenetica()
        val animal2 = Vector(TipoDeVector.Animal, londres)
        especie2.agregarMutacion(bioalteracionPersona)
        especie2.infectaA(animal)
        bioalteracionPersona.mutar(animal)
        animal.contagiarA(animal2)

        val especiesDelAnimal = animal2.especies

        Assertions.assertFalse(especiesDelAnimal.contains(especie2))
    }
    @Test
    fun test11_CuandoUnInsectoMutaConBioalteracionGeneticaPuedeContagiarInsectos(){
        service.setModo(ModoTestMutacion(0, 0, 2, 2, 100))
        val bioalteracionInsecto = BioalteracionGenetica()
        val insecto2 = Vector(TipoDeVector.Insecto, londres)
        especie2.agregarMutacion(bioalteracionInsecto)
        especie2.infectaA(insecto)
        bioalteracionInsecto.mutar(insecto)
        insecto.contagiarA(insecto2)

        val especiesDelInsecto = insecto2.especies

        Assertions.assertTrue(especiesDelInsecto.contains(especie2))
    }

    @Test
    fun test12_CuandoUnInsectoMutaConBioalteracionGeneticaNoPuedeContagiarInsectos(){
        service.setModo(ModoTestMutacion(0, 0, 1, 2, 100))
        val bioalteracionPersona = BioalteracionGenetica()
        val insecto2 = Vector(TipoDeVector.Insecto, londres)
        especie2.agregarMutacion(bioalteracionPersona)
        especie2.infectaA(insecto)
        bioalteracionPersona.mutar(insecto)
        insecto.contagiarA(insecto2)

        val especiesDelInsecto = insecto2.especies

        Assertions.assertFalse(especiesDelInsecto.contains(especie2))
    }

    @Test
    fun test13_UnVectorPuedeTenerVariasMutacionesPeroDeDistintasEspecies(){
        val bioalteracion2 = BioalteracionGenetica()
        bioalteracion2.especie = especie2
        bioalteracion.mutar(persona)
        bioalteracion2.mutar(persona)

        val mutacionesDePersona = persona.mutaciones

        Assertions.assertEquals(2, mutacionesDePersona.size)
    }

    @Test
    fun test14_UnVectorNoMutaAlContagiarConUnaEspecieSinMutaciones(){
        especie1.infectaA(persona)
        persona.contagiarA(insecto)

        val especiesDeInsecto = insecto.especies
        val mutacionesDePersona = persona.mutaciones

        Assertions.assertEquals(1, especiesDeInsecto.size)
        Assertions.assertEquals(0, mutacionesDePersona.size)
    }

    @Test
    fun test15_UnVectorMutaAlContagiarConUnaEspecieConMutaciones(){
        especie1.agregarMutacion(bioalteracion)
        especie1.infectaA(persona)
        persona.contagiarA(insecto)

        val especiesDeInsecto = insecto.especies
        val mutacionesDePersona = persona.mutaciones

        Assertions.assertEquals(1, especiesDeInsecto.size)
        Assertions.assertEquals(1, mutacionesDePersona.size)
    }

    @Test
    fun test16_UnVectorNoMutaAlContagiarConUnaEspecieConMutaciones(){
        service.setModo(ModoTestMutacion(0, 4, 1, 2, 100))
        val especie3 = Especie(patogeno, "HongoC", "Japon")
        especie3.agregarMutacion(bioalteracion0)
        especie3.infectaA(persona)
        persona.contagiarA(insecto)

        val especiesDeInsecto = insecto.especies
        val mutacionesDePersona = persona.mutaciones

        Assertions.assertEquals(1, especiesDeInsecto.size)
        Assertions.assertEquals(0, mutacionesDePersona.size)
    }

    @Test
    fun test17_UnVectorTieneMuchasMutacionesRepetidasParaDistintasEspecies(){

        especie1.agregarMutacion(bioalteracion)
        especie2.agregarMutacion(bioalteracion1)
        especie1.infectaA(persona)
        especie2.infectaA(persona)

        persona.contagiarA(insecto)

        val bioalteraciones = persona.mutaciones.filter { m -> m.esBioalteracionGenetica()}

        Assertions.assertEquals(2, bioalteraciones.size)
        Assertions.assertTrue(bioalteraciones.size == bioalteraciones.distinctBy { it.especie }.size)

    }
}