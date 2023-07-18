package ar.edu.unq.eperdemic.servicesTest.Relacional

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.mutacion.SupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoTestContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.ModoTestMutacion
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.RandomMutacion
import ar.edu.unq.eperdemic.services.impl.Cleaner
import ar.edu.unq.eperdemic.modelo.exceptions.InfectionRejectedException
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.services.*
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
class PatogenoServiceTest {
    @Autowired private lateinit var service: PatogenoService
    @Autowired private lateinit var serviceUbicacion: UbicacionService
    @Autowired private lateinit var serviceVector: VectorService
    @Autowired private lateinit var serviceMutacion : MutacionService
    @Autowired private lateinit var serviceDistrito : DistritoService
    @Autowired private lateinit var cleaner: Cleaner
    private var randomContagio: RandomContagio = RandomContagio
    private var randomMutacion: RandomMutacion = RandomMutacion
    private lateinit var supresion : Mutacion
    private lateinit var persona2 : Vector


    private lateinit var patogeno: Patogeno
    private lateinit var patogeno2: Patogeno
    private lateinit var distrito : Distrito
    private lateinit var coordenada : Coordenada
    private lateinit var ubicacion: Ubicacion
    private lateinit var ubicacion2: Ubicacion


    @BeforeEach
    fun setUp(){
        this.patogeno = Patogeno("Covid")
        this.patogeno2 = Patogeno("Gripe")

        distrito = Distrito("Amba", GeoJsonPolygon(listOf(GeoJsonPoint(24.0, 12.2), GeoJsonPoint(10.0,8.0), GeoJsonPoint(4.0,6.0))))
        serviceDistrito.crear(distrito)

        coordenada = Coordenada(12.2, 24.0)

        // Persistencia
        ubicacion2 = serviceUbicacion.crearUbicacion("Flores",coordenada)
        patogeno2 = service.crearPatogeno(patogeno2)
        ubicacion = serviceUbicacion.crearUbicacion("Palermo",coordenada)
        randomMutacion.setModo(ModoTestMutacion(100, 0, 0, 100, 0))
        randomContagio.setModo(ModoTestContagio(100, 100, 100, 10, 0))
        supresion = SupresionBiomecanica()

        persona2 = serviceVector.crearVector(TipoDeVector.Persona, ubicacion2.id!!)

    }

    @Test
    fun sePersisteUnPatogeno(){
        val patogenoPersistido = service.crearPatogeno(patogeno)
        Assertions.assertEquals(patogeno.tipo,patogenoPersistido.tipo)
    }

    @Test
    fun test_AlCrearUnPatogenoTieneUnID(){
        Assertions.assertNull(patogeno.id)
        val patogeno = service.crearPatogeno(patogeno)
        Assertions.assertNotNull(patogeno.id)
    }

    @Test
    fun test_RecuperarUnPatogenoPersistido(){
        val patogenoPersistido = service.recuperarPatogeno(patogeno2.id!!)
        Assertions.assertEquals(patogeno2.tipo,patogenoPersistido.tipo)
    }

    @Test
    fun test_RecuperarUnPatogenoQueNoEstaPersistido(){
        val patogenoPersistido = service.crearPatogeno(patogeno)
        cleaner.cleanDB()

        val exception = Assertions.assertThrows(NotFoundException::class.java, {  service.recuperarPatogeno(patogenoPersistido.id!!) }, "No hay ningun Vector con ese ID")
        Assertions.assertEquals("No se pudo encontrar la entidad con ID ${patogenoPersistido.id}", exception.message)
    }

    @Test
    fun test_RecuperarTodosLosPatogenos(){
        service.crearPatogeno(patogeno)
        val patogenos = service.recuperarATodosLosPatogenos()
        Assertions.assertEquals(2,patogenos.size)
    }

    @Test
    fun test_alAgregarUnaEspecieAlPatogenoSePersisteDichaEspecie(){
        serviceVector.crearVector(TipoDeVector.Animal,ubicacion.id!!)
        val especie = service.agregarEspecie(patogeno2.id!!,"A",ubicacion.id!!)
        Assertions.assertNotNull(especie.id)
    }

    @Test
    fun test_alAgregarUnaEspecieSeInfectaUnVectorAlAzar(){
        serviceVector.crearVector(TipoDeVector.Persona, ubicacion.id!!)
        serviceVector.crearVector(TipoDeVector.Animal, ubicacion.id!!)
        val especie = service.agregarEspecie(patogeno2.id!!,"Hongo-A",ubicacion.id!!)
        val vectoresPersistidos = serviceVector.recuperarTodos()
        Assertions.assertTrue(vectoresPersistidos.any{v -> v.contieneEspecie(especie.id!!)})
    }

    @Test
    fun test_alAgregarUnaEspecieEnUnaUbicacionSinVectoresSeLanzaUnaExcepcion(){
        service.crearPatogeno(patogeno)
        Assertions.assertThrows(NotFoundException::class.java) {
            service.agregarEspecie(patogeno.id!!, "Hongo-A", ubicacion.id!!)
        }
    }

    @Test
    fun test_AlAgregarUnaEspecieEnUnaUbicacionSinVectoresInfectablesSeLanzaUnaExcepcion(){
        var persona1 = serviceVector.crearVector(TipoDeVector.Persona, ubicacion.id!!)
        val especie = service.agregarEspecie(patogeno2.id!!, "Viruela", ubicacion.id!!)
        serviceMutacion.agregarMutacion(especie.id!!, supresion)
        persona1 = serviceVector.recuperarVector(persona1.id!!)
        val personas = mutableSetOf(serviceVector.recuperarVector(persona2.id!!))
        serviceVector.contagiar(persona1, personas)

        Assertions.assertThrows(InfectionRejectedException::class.java) {
            service.agregarEspecie(patogeno2.id!!, "Hongo-A", ubicacion.id!!)
        }


    }

    @Test
    fun test_unPatogenoSabeLaCantidadDeInfectadosDeUnaEspecie() {
        val patogenoPersistido = service.crearPatogeno(patogeno)
        serviceVector.crearVector(TipoDeVector.Insecto, ubicacion.id!!)
        val especie = service.agregarEspecie(patogenoPersistido.id!!, "Gripe", ubicacion.id!!)

        val cantInfectados = service.cantidadDeInfectados(especie.id!!)

        Assertions.assertEquals(1, cantInfectados)
    }

    @Test
    fun test_esPandemiaCuandoSeEncuentraEnMasDeLaMitadDeUbicaciones(){
        val patogenoPersistido = service.crearPatogeno(patogeno)
        serviceVector.crearVector(TipoDeVector.Insecto, ubicacion.id!!)
        val insecto2 = serviceVector.crearVector(TipoDeVector.Insecto, ubicacion2.id!!)
        val especie = service.agregarEspecie(patogenoPersistido.id!!, "Gripe", ubicacion.id!!)
        serviceVector.infectar(insecto2.id!!, especie.id!!)
        Assertions.assertTrue(service.esPandemia(especie.id!!))
    }

    @Test
    fun test_noEsPandemiaCuandoNoSeEncuentraEnMasDeLaMitadDeUbicaciones(){
        serviceUbicacion.crearUbicacion("Buenos Aires",coordenada)
        serviceUbicacion.crearUbicacion("Cordoba",coordenada)
        serviceUbicacion.crearUbicacion("Neuquen",coordenada)

        val patogenoPersistido = service.crearPatogeno(patogeno)
        serviceVector.crearVector(TipoDeVector.Insecto, ubicacion.id!!)
        val especie = service.agregarEspecie(patogenoPersistido.id!!, "Gripe", ubicacion.id!!)

        Assertions.assertFalse(service.esPandemia(especie.id!!))
    }

    @Test
    fun test_seObtienenLasEspeciesDelPatogeno(){
        val virus = service.crearPatogeno(patogeno)
        serviceVector.crearVector(TipoDeVector.Insecto, ubicacion.id!!)
        val gripe = service.agregarEspecie(virus.id!!, "Gripe", ubicacion.id!!)
        val covid = service.agregarEspecie(virus.id!!, "Covid", ubicacion.id!!)

        val especies = service.especiesDePatogeno(virus.id!!)

        Assertions.assertTrue(especies.any{e -> e.id == gripe.id && e.nombre == gripe.nombre})
        Assertions.assertTrue(especies.any{e -> e.id == covid.id && e.nombre == covid.nombre})
        Assertions.assertEquals(2, especies.size)
    }

    @Test
    fun test_seObtienenLasEspeciesDelPatogenoSinEspecies(){
        val virus = service.crearPatogeno(patogeno)
        Assertions.assertEquals(0, service.especiesDePatogeno(virus.id!!).size)
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}