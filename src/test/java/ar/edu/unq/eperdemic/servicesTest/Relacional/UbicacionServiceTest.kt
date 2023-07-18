package ar.edu.unq.eperdemic.servicesTest.Relacional

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoTestContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import ar.edu.unq.eperdemic.services.exceptions.UniqueException
import ar.edu.unq.eperdemic.services.impl.Cleaner
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionServiceTest {
    private lateinit var serviceTesting: RandomContagio
    @Autowired private lateinit var service: UbicacionService
    @Autowired private lateinit var patogenoService: PatogenoService
    @Autowired private lateinit var vectorService: VectorService
    @Autowired private lateinit var serviceDistrito : DistritoService
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var berazategui: Ubicacion
    private lateinit var bernal: Ubicacion
    private lateinit var mosca: Vector
    private lateinit var mosquito: Vector
    private lateinit var patogeno: Patogeno
    private lateinit var especie: Especie
    private lateinit var distrito : Distrito
    private lateinit var coordenada : Coordenada

    @BeforeEach
    fun crearModelo() {
        this.serviceTesting = RandomContagio

        distrito = Distrito("Amba", GeoJsonPolygon(listOf(GeoJsonPoint(24.0, 12.2), GeoJsonPoint(10.0,8.0), GeoJsonPoint(4.0,6.0))))
        serviceDistrito.crear(distrito)

        //Modelo
        coordenada = Coordenada(12.2, 24.0)
        bernal = service.crearUbicacion("Bernal", coordenada)
        berazategui = service.crearUbicacion("berazategui", coordenada)
        mosca = vectorService.crearVector(TipoDeVector.Insecto, berazategui.id!!)
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, berazategui.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", berazategui.id!!)
        service.conectar(berazategui.nombre, bernal.nombre, "tierra")
    }

    @Test
    fun sePersisteUnaUbicacion(){
        val quilmes = service.crearUbicacion("Quilmes",coordenada)
        Assertions.assertDoesNotThrow { service.recuperarUbicacion(quilmes.nombre) }
    }

    @Test
    fun test_NoSePuedePersistirDosUbicacionesConMismoNombre(){
        Assertions.assertThrows(
            UniqueException::class.java,
            { service.crearUbicacion("Bernal",coordenada) },
            "No se puede guardar el objeto debido a que ya existe otro objeto con el mismo nombre"
        )
    }

    @Test
    fun test_seRecuperaUnaUbicacionPersistida(){
        val ubicacionRecuperada = service.recuperarUbicacion(bernal.nombre)
        val nombreDeUbicacion = bernal.nombre
        Assertions.assertEquals(ubicacionRecuperada.nombre, nombreDeUbicacion)
    }

    @Test
    fun test_seIntentaRecuperarUnaUbicacionNoPersistida(){
        val wilde = service.crearUbicacion("Wilde",coordenada)
        cleaner.cleanDB()

        Assertions.assertThrows(EmptyResultDataAccessException::class.java) {
            service.recuperarUbicacion(wilde.nombre)
        }

    }

    @Test
    fun test_seRecuperanTodasLasUbicaciones(){
        val ubicaciones = service.recuperarTodos()
        val ubicacionBerazategui = ubicaciones.find { u -> u.id == berazategui.id }
        Assertions.assertEquals(2, ubicaciones.size)
        Assertions.assertEquals("berazategui", ubicacionBerazategui!!.nombre)
    }

   @Test
    fun test_seMueveUnVector(){
        service.mover(mosca.id!!, bernal.nombre)
        val moscaPersistida = vectorService.recuperarVector(mosca.id!!)
        Assertions.assertEquals("Bernal", moscaPersistida.ubicacion.nombre)
    }

    @Test
    fun test_seIntentaExpandirEnUnaUbicacionSinVectoresInfectados(){
        vectorService.crearVector(TipoDeVector.Persona, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        service.expandir(bernal.nombre)

        val bernalPersistido = service.recuperarUbicacion(bernal.nombre)
        Assertions.assertTrue(bernalPersistido.vectoresInfectados().isEmpty())
    }

    @Test
    fun test_seMueveUnVectorYSeExpandeATodos(){
        serviceTesting.setModo(ModoTestContagio(100,100,100,0,0))
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Sars", berazategui.id!!)
        val especie2 = patogenoService.agregarEspecie(patogeno.id!!, "Vinchuca", berazategui.id!!)
        val piojo = vectorService.crearVector(TipoDeVector.Insecto, berazategui.id!!)
        val persona = vectorService.crearVector(TipoDeVector.Persona, bernal.id!!)
        val pulga = vectorService.crearVector(TipoDeVector.Persona, bernal.id!!)
        vectorService.infectar(piojo.id!!, especie2.id!!)
        vectorService.infectar(piojo.id!!, especie.id!!)
        service.mover(piojo.id!!, bernal.nombre)

        val vectoresBernal = mutableListOf(vectorService.recuperarVector(persona.id!!), vectorService.recuperarVector(pulga.id!!))
        Assertions.assertTrue(vectoresBernal.all { v -> v.contieneEspecie(especie.id!!) })
    }

    @Test
    fun test_seExpandeEnUnaUbicacionConVectoresInfectados(){
        serviceTesting.setModo(ModoTestContagio(100,100,100,0,0))
        val especie2 = patogenoService.agregarEspecie(patogeno.id!!, "Viruela", berazategui.id!!)
        val vectorHumano = vectorService.crearVector(TipoDeVector.Persona, bernal.id!!)

        vectorService.crearVector(TipoDeVector.Insecto, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Persona, bernal.id!!)
        vectorService.infectar(vectorHumano.id!!, especie2.id!!)

        service.expandir(bernal.nombre)

        val bernalPersistido = service.recuperarUbicacion(bernal.nombre)
        Assertions.assertTrue(bernalPersistido.vectores.size == bernalPersistido.vectoresInfectados().size)
        Assertions.assertTrue(bernalPersistido.vectores.all { v-> v.contieneEspecie(especie2.id!!) })
    }

    @AfterEach
    fun clearUp(){
         cleaner.cleanDB()
    }
}