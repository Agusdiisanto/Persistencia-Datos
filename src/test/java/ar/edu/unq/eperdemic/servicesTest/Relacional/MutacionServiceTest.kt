package ar.edu.unq.eperdemic.servicesTest.Relacional

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.mutacion.*
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoTestContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.ModoTestMutacion
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.RandomMutacion
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.Cleaner
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MutacionServiceTest {
    private var randomContagio: RandomContagio = RandomContagio
    private var randomMutacion: RandomMutacion = RandomMutacion
    @Autowired private lateinit var serviceMutacion : MutacionService
    @Autowired private lateinit var serviceVector : VectorService
    @Autowired private lateinit var serviceUbicacion : UbicacionService
    @Autowired private lateinit var servicePatogeno : PatogenoService
    @Autowired private lateinit var serviceEspecie : EspecieService
    @Autowired private lateinit var serviceDistrito : DistritoService
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var persona : Vector
    private lateinit var insecto : Vector
    private lateinit var animal : Vector
    private lateinit var distrito : Distrito
    private lateinit var coordenada : Coordenada
    private lateinit var buenosAires : Ubicacion
    private lateinit var londres : Ubicacion
    private lateinit var resistencia : Ubicacion
    private lateinit var patogeno : Patogeno
    private lateinit var sars : Especie
    private lateinit var covid : Especie
    private lateinit var supresion : Mutacion
    private lateinit var bioalteracion : Mutacion

    @BeforeEach
    fun crearModelo(){
        randomMutacion.setModo(ModoTestMutacion(100, 0, 0, 2, 0))
        randomContagio.setModo(ModoTestContagio(100, 100, 100, 10, 0))

        distrito = Distrito("Amba", GeoJsonPolygon(listOf(GeoJsonPoint(24.0, 12.2), GeoJsonPoint(10.0,8.0), GeoJsonPoint(4.0,6.0))))
        serviceDistrito.crear(distrito)

        //Modelo
        patogeno = Patogeno("Virus")
        supresion = SupresionBiomecanica()
        bioalteracion = BioalteracionGenetica()

        coordenada = Coordenada(12.2, 24.0)

        //Persistencia
        servicePatogeno.crearPatogeno(patogeno)
        buenosAires = serviceUbicacion.crearUbicacion("Buenos Aires",coordenada)
        londres = serviceUbicacion.crearUbicacion("Londres",coordenada)
        resistencia = serviceUbicacion.crearUbicacion("Resistencia",coordenada)
        persona = serviceVector.crearVector(TipoDeVector.Persona, buenosAires.id!!)
        sars = servicePatogeno.agregarEspecie(patogeno.id!!, "Sars", buenosAires.id!!)
        randomMutacion.setModo(ModoTestMutacion(100, 0, 0, 2, 0))

        covid = servicePatogeno.agregarEspecie(patogeno.id!!, "Covid", buenosAires.id!!)
        insecto = serviceVector.crearVector(TipoDeVector.Insecto, resistencia.id!!)
        animal = serviceVector.crearVector(TipoDeVector.Animal, resistencia.id!!)
        serviceMutacion.agregarMutacion(sars.id!!, supresion)
        serviceMutacion.agregarMutacion(covid.id!!, bioalteracion)
    }

    @Test
    fun test_alAgregarUnaMutacionSePersiste(){
        Assertions.assertNotNull(supresion.id!!)
    }

    @Test
    fun test_seRecuperaUnaMutacionPersistida(){
        val supresionPersistida = serviceMutacion.recuperarMutacion(supresion.id!!)
        Assertions.assertEquals(supresion.especie!!.id, supresionPersistida.especie!!.id)
    }

    @Test
    fun test_alAgregarUnaMutacionSePersisteAsociadoAUnaEspecie(){
        val supresionPersistida = serviceMutacion.recuperarMutacion(supresion.id!!)
        val especieRecuperada = serviceEspecie.recuperarEspecie(sars.id!!)
        Assertions.assertTrue(supresionPersistida.especie!!.id == especieRecuperada.id)
        Assertions.assertTrue(especieRecuperada.mutaciones.any{ m -> m.id == supresionPersistida.id })
    }

    @Test
    fun test_noSeRecuperaUnaMutacionNoPersistida(){
        cleaner.cleanDB()
        val exception = Assertions.assertThrows(NotFoundException::class.java, { serviceMutacion.recuperarMutacion(supresion.id!!) }, "No se pudo encontrar la entidad con ID ${supresion.id!!}")
        Assertions.assertEquals("No se pudo encontrar la entidad con ID ${supresion.id!!}", exception.message)

    }

    @Test
    fun test_seRecuperanTodasLasMutacionesPersistidas(){
        val mutaciones = serviceMutacion.recuperarTodos()
        Assertions.assertEquals(2, mutaciones.size)
        Assertions.assertTrue(mutaciones.any { m -> m.id == supresion.id })
        Assertions.assertTrue(mutaciones.any { m -> m.id == bioalteracion.id })
    }

    @Test
    fun test_unVectorContagiaYMutaConUnaDeLasMutacionDeSuEspecie() {
        val vectores = mutableSetOf(insecto)
        val personaRecuperada = serviceVector.recuperarVector(persona.id!!)
        serviceVector.contagiar(personaRecuperada, vectores)

        val mutaciones = serviceMutacion.recuperarTodos()

        Assertions.assertTrue(mutaciones.any { m -> m.contieneVector(personaRecuperada) })
        Assertions.assertTrue(vectores.all { v -> v.contieneEspecie(sars.id!!) })
    }

    @Test
    fun test_AlMutarConSupresionSeEliminanLasEspeciesConMenorDefensa(){
        persona = serviceVector.recuperarVector(persona.id!!)
        Assertions.assertTrue(persona.especies.isNotEmpty())

        val vectores = mutableSetOf(insecto)
        serviceVector.contagiar(persona, vectores)
        val personaRecuperada = serviceVector.recuperarVector(persona.id!!)

        Assertions.assertTrue(personaRecuperada.mutaciones.any{m -> m.esSupresionBiomecanica()})
        Assertions.assertFalse(personaRecuperada.especies.any{e -> e.id == sars.id })
    }

    @Test
    fun test_AlMutarConSupresionNoSePuedeContagiarConEspeciesMasDebiles(){
        var personaRecuperada = serviceVector.recuperarVector(persona.id!!)
        var insectoRecuperado = serviceVector.recuperarVector(insecto.id!!)

        val vectores = mutableSetOf(insectoRecuperado)
        serviceVector.contagiar(personaRecuperada, vectores)

        personaRecuperada = serviceVector.recuperarVector(persona.id!!)
        insectoRecuperado = serviceVector.recuperarVector(insecto.id!!)
        val vectoresPersona = mutableSetOf(personaRecuperada)

        serviceVector.contagiar(insectoRecuperado, vectoresPersona)
        personaRecuperada =  serviceVector.recuperarVector(persona.id!!)

        Assertions.assertTrue(personaRecuperada.especies.isEmpty())
        Assertions.assertTrue(personaRecuperada.mutaciones.any{m -> m.esSupresionBiomecanica()})
    }

    @Test
    fun test_AlMutarConSupresionSePuedeContagiarConEspeciesMasFuertes(){
        randomMutacion.setModo(ModoTestMutacion(100, 100, 0, 2, 0))
        val viruela = servicePatogeno.agregarEspecie(patogeno.id!!, "Viruela", resistencia.id!!)
        serviceVector.infectar(insecto.id!!, viruela.id!!)
        var personaRecuperada = serviceVector.recuperarVector(persona.id!!)
        var insectoRecuperado = serviceVector.recuperarVector(insecto.id!!)

        val vectores = mutableSetOf(insectoRecuperado)

        serviceVector.contagiar(personaRecuperada, vectores)

        personaRecuperada = serviceVector.recuperarVector(persona.id!!)
        insectoRecuperado = serviceVector.recuperarVector(insecto.id!!)
        val vectoresPersona = mutableSetOf(personaRecuperada)

        serviceVector.contagiar(insectoRecuperado, vectoresPersona)

        personaRecuperada =  serviceVector.recuperarVector(persona.id!!)

        Assertions.assertTrue(personaRecuperada.mutaciones.any{m -> m.esSupresionBiomecanica()})
        Assertions.assertTrue(personaRecuperada.contieneEspecie(viruela.id!!))
    }

    @Test
    fun test_AlMutarConBioalteracionUnAnimalPuedeContagiarOtroAnimal(){
        persona = serviceVector.recuperarVector(persona.id!!)
        serviceVector.infectar(animal.id!!, covid.id!!)
        var animalRecuperado = serviceVector.recuperarVector(animal.id!!)
        val vectores = mutableSetOf(persona)
        serviceVector.contagiar(animalRecuperado, vectores)

        val animal2 = serviceVector.crearVector(TipoDeVector.Animal, resistencia.id!!)
        val vectores2 = mutableSetOf(animal2)
        animalRecuperado = serviceVector.recuperarVector(animal.id!!)
        serviceVector.contagiar(animalRecuperado, vectores2)

        val animal2Recuperado = serviceVector.recuperarVector(animal2.id!!)

        Assertions.assertTrue(animal2Recuperado.contieneEspecie(covid.id!!))
    }

    @Test
    fun test_AlMutarConBioalteracionUnInsectoPuedeContagiarOtroInsecto(){
        randomMutacion.setModo(ModoTestMutacion(100, 0, 2, 2, 0))
        val viruela = servicePatogeno.agregarEspecie(patogeno.id!!, "Viruela", resistencia.id!!)
        val bioalteracion2 = BioalteracionGenetica()
        serviceMutacion.agregarMutacion(viruela.id!!, bioalteracion2)

        insecto = serviceVector.recuperarVector(insecto.id!!)
        serviceVector.infectar(insecto.id!!, viruela.id!!)
        var insectoRecuperado = serviceVector.recuperarVector(insecto.id!!)
        val vectores = mutableSetOf(persona)
        serviceVector.contagiar(insectoRecuperado, vectores)

        val insecto2 = serviceVector.crearVector(TipoDeVector.Insecto, resistencia.id!!)
        val vectores2 = mutableSetOf(insecto2)
        insectoRecuperado = serviceVector.recuperarVector(insecto.id!!)
        serviceVector.contagiar(insectoRecuperado, vectores2)

        val insecto2Recuperado = serviceVector.recuperarVector(insecto2.id!!)

        Assertions.assertTrue(insecto2Recuperado.contieneEspecie(viruela.id!!))
    }

    @Test
    fun test_AlMutarConBioalteracionUnaPersonaPuedeContagiarUnAnimal(){
        randomMutacion.setModo(ModoTestMutacion(100, 0, 0, 2, 0))
        val viruela = servicePatogeno.agregarEspecie(patogeno.id!!, "Viruela", resistencia.id!!)
        val bioalteracion3 = BioalteracionGenetica()
        var persona2 = serviceVector.crearVector(TipoDeVector.Persona, resistencia.id!!)
        serviceMutacion.agregarMutacion(viruela.id!!, bioalteracion3)
        serviceVector.infectar(persona2.id!!, viruela.id!!)

        persona2 = serviceVector.recuperarVector(persona2.id!!)
        val vectores = mutableSetOf(serviceVector.recuperarVector(insecto.id!!))
        serviceVector.contagiar(persona2, vectores)

        val personaRecuperada = serviceVector.recuperarVector(persona2.id!!)
        animal = serviceVector.recuperarVector(animal.id!!)
        val vectores2 = mutableSetOf(animal)
        serviceVector.contagiar(personaRecuperada, vectores2)

        val animalRecuperado = serviceVector.recuperarVector(animal.id!!)

        Assertions.assertTrue(animalRecuperado.contieneEspecie(viruela.id!!))
    }

    @Test
    fun unVectorPuedeMutarPorCadaUnaDeSusEspecies(){
        serviceVector.infectar(insecto.id!!, sars.id!!)
        serviceVector.infectar(insecto.id!!, covid.id!!)
        var vectorInsecto = serviceVector.recuperarVector(insecto.id!!)
        val personas = mutableSetOf(serviceVector.recuperarVector(persona.id!!))
        serviceVector.contagiar(vectorInsecto, personas)

        vectorInsecto = serviceVector.recuperarVector(insecto.id!!)
        Assertions.assertTrue(vectorInsecto.mutaciones.any{ m -> m.esSupresionBiomecanica() })
        Assertions.assertTrue(vectorInsecto.mutaciones.any{ m -> m.esBioalteracionGenetica() })
    }

    @Test
    fun unVectorSoloPuedeMutarConUnaDeLasMutacionesDeCadaEspecie(){
        val bioalteracion2 = BioalteracionGenetica()
        serviceMutacion.agregarMutacion(sars.id!!, bioalteracion2)
        serviceVector.infectar(insecto.id!!, sars.id!!)
        var vectorInsecto = serviceVector.recuperarVector(insecto.id!!)
        val personas = mutableSetOf(serviceVector.recuperarVector(persona.id!!))
        serviceVector.contagiar(vectorInsecto, personas)

        val sarsRecuperado = serviceEspecie.recuperarEspecie(sars.id!!)
        vectorInsecto = serviceVector.recuperarVector(insecto.id!!)

        Assertions.assertEquals(1, vectorInsecto.mutaciones.size)
        Assertions.assertEquals(2, sarsRecuperado.mutaciones.size)
    }

    @Test
    fun test_AlMutarConBioalteracionDeAnimalNoPuedeContagiarUnInsecto(){
      randomMutacion.setModo(ModoTestMutacion(100, 0, 0, 2, 0))
      val viruela = servicePatogeno.agregarEspecie(patogeno.id!!, "Viruela", resistencia.id!!)
      val bioalteracion2 = BioalteracionGenetica()
      serviceMutacion.agregarMutacion(viruela.id!!, bioalteracion2)

      insecto = serviceVector.recuperarVector(insecto.id!!)
      serviceVector.infectar(insecto.id!!, viruela.id!!)
      var insectoRecuperado = serviceVector.recuperarVector(insecto.id!!)
      val vectores = mutableSetOf(persona)
      serviceVector.contagiar(insectoRecuperado, vectores)

      val insecto2 = serviceVector.crearVector(TipoDeVector.Insecto, resistencia.id!!)
      val vectores2 = mutableSetOf(insecto2)
      insectoRecuperado = serviceVector.recuperarVector(insecto.id!!)
      serviceVector.contagiar(insectoRecuperado, vectores2)

      val insecto2Recuperado = serviceVector.recuperarVector(insecto2.id!!)

      Assertions.assertFalse(insecto2Recuperado.contieneEspecie(viruela.id!!))
    }

    @Test
    fun test_AlMutarConBioalteracionUnAnimalNoPuedeContagiarOtroAnimal(){
      randomMutacion.setModo(ModoTestMutacion(100, 0, 1, 2, 0))
      val viruela = servicePatogeno.agregarEspecie(patogeno.id!!, "Viruela", resistencia.id!!)
      val bioalteracion2 = BioalteracionGenetica()
      serviceMutacion.agregarMutacion(viruela.id!!, bioalteracion2)

      animal = serviceVector.recuperarVector(animal.id!!)
      serviceVector.infectar(animal.id!!, viruela.id!!)
      var animalRecuperado = serviceVector.recuperarVector(animal.id!!)
      val vectores = mutableSetOf(persona)
      serviceVector.contagiar(animalRecuperado, vectores)

      val animal2 = serviceVector.crearVector(TipoDeVector.Animal, resistencia.id!!)
      val vectores2 = mutableSetOf(animal2)
      animalRecuperado = serviceVector.recuperarVector(animal.id!!)
      serviceVector.contagiar(animalRecuperado, vectores2)

      val animal2Recuperado = serviceVector.recuperarVector(animal2.id!!)

      Assertions.assertFalse(animal2Recuperado.contieneEspecie(viruela.id!!))
    }

    @Test
    fun test_AlMutarConBioalteracionUnaPersonaNoPuedeContagiarUnAnimal(){
        randomMutacion.setModo(ModoTestMutacion(100, 0, 1, 2, 0))
        val viruela = servicePatogeno.agregarEspecie(patogeno.id!!, "Viruela", buenosAires.id!!)
        val bioalteracion3 = BioalteracionGenetica()
        var persona2 = serviceVector.crearVector(TipoDeVector.Persona, resistencia.id!!)
        serviceMutacion.agregarMutacion(viruela.id!!, bioalteracion3)
        serviceVector.infectar(persona2.id!!, viruela.id!!)

        persona2 = serviceVector.recuperarVector(persona2.id!!)
        val vectores = mutableSetOf(serviceVector.recuperarVector(insecto.id!!))
        serviceVector.contagiar(persona2, vectores)

        val personaRecuperada = serviceVector.recuperarVector(persona2.id!!)
        animal = serviceVector.recuperarVector(animal.id!!)
        val vectores2 = mutableSetOf(animal)
        serviceVector.contagiar(personaRecuperada, vectores2)

        val animalRecuperado = serviceVector.recuperarVector(animal.id!!)

        Assertions.assertFalse(animalRecuperado.contieneEspecie(viruela.id!!))
    }

    @Test
    fun test_UnVectorPuedeMutarConLaMismaMutacionParaDistintaEspecie(){
        val viruela = servicePatogeno.agregarEspecie(patogeno.id!!, "Viruela", buenosAires.id!!)
        val bioalteracion2 = BioalteracionGenetica()
        serviceMutacion.agregarMutacion(viruela.id!!, bioalteracion2)

        insecto = serviceVector.recuperarVector(insecto.id!!)
        serviceVector.infectar(insecto.id!!, viruela.id!!)
        serviceVector.infectar(insecto.id!!, covid.id!!)

        val insectoRecuperado = serviceVector.recuperarVector(insecto.id!!)
        persona = serviceVector.recuperarVector(persona.id!!)
        val vectores = mutableSetOf(persona)
        serviceVector.contagiar(insectoRecuperado, vectores)

        val mutacionesDeInsecto = insectoRecuperado.mutaciones

        Assertions.assertTrue(mutacionesDeInsecto.all{m -> m.esBioalteracionGenetica()})
        Assertions.assertTrue(mutacionesDeInsecto.size == mutacionesDeInsecto.distinctBy { it.id }.size)
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}