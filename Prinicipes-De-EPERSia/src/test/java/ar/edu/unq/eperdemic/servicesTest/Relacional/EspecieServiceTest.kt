package ar.edu.unq.eperdemic.servicesTest.Relacional

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
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
class EspecieServiceTest {
    // Service
    @Autowired private lateinit var service: EspecieService
    @Autowired private lateinit var serviceVector: VectorService
    @Autowired private lateinit var servicePatogeno: PatogenoService
    @Autowired private lateinit var serviceUbicacion: UbicacionService
    @Autowired private lateinit var serviceDistrito: DistritoService
    @Autowired private lateinit var cleaner: Cleaner

    // Modelo
    private lateinit var ubicacion: Ubicacion
    private lateinit var ubicacion2: Ubicacion
    private lateinit var patogeno: Patogeno
    private lateinit var especie: Especie
    private lateinit var especie1: Especie
    private lateinit var vector: Vector
    private lateinit var vector2: Vector
    private lateinit var coordenada: Coordenada
    private lateinit var distrito : Distrito

    @BeforeEach
    fun crearModelo() {

        distrito = Distrito("Amba", GeoJsonPolygon(listOf(GeoJsonPoint(24.0, 12.2), GeoJsonPoint(10.0,8.0), GeoJsonPoint(4.0,6.0))))
        serviceDistrito.crear(distrito)

        // Modelo
        patogeno = Patogeno("Bacteria")
        coordenada = Coordenada(12.2, 24.0)

        // Persistencia
        ubicacion = serviceUbicacion.crearUbicacion("Palermo",coordenada)
        ubicacion2 = serviceUbicacion.crearUbicacion("Lomas",coordenada)
        serviceVector.crearVector(TipoDeVector.Animal,ubicacion.id!!)
        vector = serviceVector.crearVector(TipoDeVector.Insecto,ubicacion2.id!!)
        vector2 = serviceVector.crearVector(TipoDeVector.Persona,ubicacion2.id!!)
        servicePatogeno.crearPatogeno(patogeno)
    }

    @Test
    fun test_alAgregarUnaEspecieSePersiste(){
        especie = servicePatogeno.agregarEspecie(patogeno.id!!,"Hongo-A",ubicacion.id!!)
        Assertions.assertEquals("Hongo-A",especie.nombre)
    }

    @Test
    fun test_alPersistirUnaEspecieSeGeneraID(){
        this.especie = servicePatogeno.agregarEspecie(patogeno.id!!,"Hongo-A",ubicacion.id!!)
        Assertions.assertNotNull(especie.id)
    }

    @Test
    fun test_seRecuperaUnaEspeciePersistida(){
        this.especie = servicePatogeno.agregarEspecie(patogeno.id!!,"Hongo-A",ubicacion.id!!)
        val especieRecuperada = service.recuperarEspecie(especie.id!!)

        Assertions.assertEquals(especie.nombre,especieRecuperada.nombre)
        Assertions.assertEquals(especie.paisDeOrigen,especieRecuperada.paisDeOrigen)
        Assertions.assertEquals(especie.patogeno.tipo,especieRecuperada.patogeno.tipo)
    }

    @Test
    fun test_seIntentaRecuperarUnaEspecieNoPersistido(){
        this.especie1 = servicePatogeno.agregarEspecie(patogeno.id!!,"Hongo-B",ubicacion.id!!)
        cleaner.cleanDB()

        val exception = Assertions.assertThrows(NotFoundException::class.java, {  service.recuperarEspecie(especie1.id!!) }, "No hay ningun Vector con ese ID")
        Assertions.assertEquals("No se pudo encontrar la entidad con ID ${especie1.id}", exception.message)
    }

    @Test
    fun test_seRecuperanTodosLasEspeciesPersistidas(){
        this.especie1 = servicePatogeno.agregarEspecie(patogeno.id!!,"Hongo-B",ubicacion.id!!)
        this.especie= servicePatogeno.agregarEspecie(patogeno.id!!,"Hongo-E",ubicacion.id!!)

        val especies = service.recuperarTodos()
        Assertions.assertEquals(2,especies.size)
    }

    @Test
    fun test_seIntentaRecuperarTodosLosEspeciesPeroNoHayNingunoPersistido(){
        val especies = service.recuperarTodos()
        Assertions.assertTrue(especies.isEmpty())
    }

    @Test
    fun test_seObtienenLaCantidadDeContagiosDeUnaEspecie(){
        this.especie = servicePatogeno.agregarEspecie(patogeno.id!!,"Hongo-A",ubicacion.id!!)
        serviceVector.infectar(vector.id!!, especie.id!!)
        serviceVector.infectar(vector2.id!!, especie.id!!)
        Assertions.assertEquals(3, service.cantidadInfectados(especie.id!!))
    }

    @Test
    fun test_laCantidadDeContagiosNoVariaSiSeInfectaDosVecesAlMismoVector(){
        this.especie = servicePatogeno.agregarEspecie(patogeno.id!!,"Hongo-A",ubicacion.id!!)
        serviceVector.infectar(vector.id!!, especie.id!!)
        serviceVector.infectar(vector2.id!!, especie.id!!)
        serviceVector.infectar(vector2.id!!, especie.id!!)
        Assertions.assertEquals(3, service.cantidadInfectados(especie.id!!))
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}