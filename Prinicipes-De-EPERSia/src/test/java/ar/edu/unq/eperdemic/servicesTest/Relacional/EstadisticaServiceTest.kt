package ar.edu.unq.eperdemic.servicesTest.Relacional

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.persistence.NoResultException

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EstadisticaServiceTest {
    //Service
    @Autowired private lateinit var service: EstadistaService
    @Autowired private lateinit var serviceUbicacion: UbicacionService
    @Autowired private lateinit var patogenoService: PatogenoService
    @Autowired private lateinit var vectorService: VectorService
    @Autowired private lateinit var distritoService: DistritoService
    @Autowired private lateinit var cleaner: Cleaner

    //Modelo
    private lateinit var vector: Vector
    private lateinit var vector2: Vector
    private lateinit var vector3: Vector
    private lateinit var vector4: Vector
    private lateinit var vector5: Vector
    private lateinit var vector6: Vector
    private lateinit var vectorX: Vector

    private lateinit var patogeno: Patogeno
    private lateinit var especie: Especie
    private lateinit var especie2: Especie
    private lateinit var especie3: Especie
    private lateinit var especie4: Especie
    private lateinit var especie5: Especie
    private lateinit var especie6: Especie
    private lateinit var especie7: Especie
    private lateinit var especie8: Especie
    private lateinit var especie9: Especie
    private lateinit var especie10: Especie
    private lateinit var especie11: Especie
    private lateinit var especie12: Especie

    private lateinit var ubicacion: Ubicacion
    private lateinit var ubicacion2: Ubicacion
    private lateinit var ubicacion3: Ubicacion
    private lateinit var ubicacion4: Ubicacion

    private lateinit var coordenada: Coordenada
    private lateinit var distrito : Distrito

    @BeforeEach
    fun crearModelo(){
        //Modelo
        patogeno = Patogeno("Virus")

        distrito = Distrito("Amba", GeoJsonPolygon(listOf(GeoJsonPoint(24.0, 12.2), GeoJsonPoint(10.0,8.0), GeoJsonPoint(4.0,6.0))))
        distritoService.crear(distrito)
        coordenada = Coordenada(12.2, 24.0)

        //Persistencia Data Service
        ubicacion = serviceUbicacion.crearUbicacion("Buenos Aires",coordenada)
        ubicacion2 = serviceUbicacion.crearUbicacion("Rosario",coordenada)
        ubicacion3 = serviceUbicacion.crearUbicacion("Cordoba",coordenada)
        ubicacion4 = serviceUbicacion.crearUbicacion("Bernal",coordenada)
        patogeno = patogenoService.crearPatogeno(patogeno)

        vector = vectorService.crearVector(TipoDeVector.Persona, ubicacion.id!!)
        vector2 = vectorService.crearVector(TipoDeVector.Persona, ubicacion.id!!)
        vector3 = vectorService.crearVector(TipoDeVector.Animal, ubicacion2.id!!)
        vector4 = vectorService.crearVector(TipoDeVector.Animal, ubicacion2.id!!)
        vector5 = vectorService.crearVector(TipoDeVector.Insecto, ubicacion2.id!!)
        vector6 = vectorService.crearVector(TipoDeVector.Insecto, ubicacion2.id!!)
        vectorX = vectorService.crearVector(TipoDeVector.Insecto, ubicacion4.id!!)

        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", ubicacion4.id!!)
        especie2 = patogenoService.agregarEspecie(patogeno.id!!, "Dengue", ubicacion4.id!!)
        especie3 = patogenoService.agregarEspecie(patogeno.id!!, "HongoA", ubicacion4.id!!)
        especie4 = patogenoService.agregarEspecie(patogeno.id!!, "HongoB", ubicacion4.id!!)
        especie5 = patogenoService.agregarEspecie(patogeno.id!!, "HongoC", ubicacion4.id!!)
        especie6 = patogenoService.agregarEspecie(patogeno.id!!, "HongoD", ubicacion4.id!!)
        especie7 = patogenoService.agregarEspecie(patogeno.id!!, "HongoE", ubicacion4.id!!)
        especie8 = patogenoService.agregarEspecie(patogeno.id!!, "HongoF", ubicacion4.id!!)
        especie9 = patogenoService.agregarEspecie(patogeno.id!!, "HongoG", ubicacion4.id!!)
        especie10 = patogenoService.agregarEspecie(patogeno.id!!, "HongoH", ubicacion4.id!!)
        especie11 = patogenoService.agregarEspecie(patogeno.id!!, "HongoI", ubicacion4.id!!)
        especie12 = patogenoService.agregarEspecie(patogeno.id!!, "HongoJ", ubicacion4.id!!)

        vectorService.infectar(vector3.id!!, especie3.id!!)
        vectorService.infectar(vector4.id!!, especie3.id!!)

        vectorService.infectar(vector4.id!!, especie4.id!!)
        vectorService.infectar(vector3.id!!, especie5.id!!)
        vectorService.infectar(vector4.id!!, especie6.id!!)
        vectorService.infectar(vector3.id!!, especie7.id!!)
        vectorService.infectar(vector4.id!!, especie8.id!!)
        vectorService.infectar(vector3.id!!, especie9.id!!)
        vectorService.infectar(vector4.id!!, especie10.id!!)
        vectorService.infectar(vector5.id!!, especie11.id!!)
        vectorService.infectar(vector6.id!!, especie12.id!!)
    }

    @Test
    fun test_seObtieneEspecieLider(){
        vectorService.infectar(vector.id!!, especie3.id!!)
        vectorService.infectar(vector2.id!!, especie3.id!!)
        vectorService.infectar(vector2.id!!, especie3.id!!)
        Assertions.assertEquals(especie3.id, service.especieLider()!!.id)
    }

    @Test
    fun test_noHayEspecieLiderSiNoHayVectoresHumanos(){
        vectorService.infectar(vector3.id!!, especie2.id!!)

        Assertions.assertThrows(NoResultException::class.java) {service.especieLider()}
    }

    @Test
    fun test_especiesLideresConMayorContagioAVectoresHumanosYAnimales(){
        vectorService.infectar(vector.id!!, especie.id!!)
        vectorService.infectar(vector2.id!!, especie.id!!)
        vectorService.infectar(vector3.id!!, especie.id!!)
        vectorService.infectar(vector4.id!!, especie.id!!)

        vectorService.infectar(vector2.id!!, especie2.id!!)
        vectorService.infectar(vector3.id!!, especie2.id!!)
        vectorService.infectar(vector4.id!!, especie2.id!!)

        val especiesLideres = service.lideres()
        val especieMaxInfecto = especiesLideres[0]
        val especieNoLider = especiesLideres.filter {e -> e.id == especie12.id}

        Assertions.assertEquals(especie.id,especieMaxInfecto.id)
        Assertions.assertTrue(especieNoLider.isEmpty())
    }

    @Test
    fun test_especiesLideresSeOrdenanDeFormaDescendente(){
        vectorService.infectar(vector.id!!, especie.id!!)
        vectorService.infectar(vector2.id!!, especie.id!!)
        vectorService.infectar(vector3.id!!, especie.id!!)
        vectorService.infectar(vector4.id!!, especie.id!!)

        vectorService.infectar(vector2.id!!, especie2.id!!)
        vectorService.infectar(vector3.id!!, especie2.id!!)
        vectorService.infectar(vector4.id!!, especie2.id!!)

        val especiesLideres = service.lideres()

        Assertions.assertEquals(especie.id, especiesLideres[0].id)
        Assertions.assertEquals(especie2.id, especiesLideres[1].id)
        Assertions.assertEquals(especie3.id, especiesLideres[2].id)
        Assertions.assertEquals(10, especiesLideres.size)
    }

    @Test
    fun test_especiesLideresSoloSeObtienenMaximo10Especies(){
        vectorService.infectar(vector.id!!, especie.id!!)
        vectorService.infectar(vector2.id!!, especie.id!!)
        vectorService.infectar(vector3.id!!, especie.id!!)
        vectorService.infectar(vector4.id!!, especie.id!!)

        vectorService.infectar(vector2.id!!, especie2.id!!)
        vectorService.infectar(vector3.id!!, especie2.id!!)
        vectorService.infectar(vector4.id!!, especie2.id!!)

        val especiesLideres = service.lideres()

        Assertions.assertEquals(especie.id, especiesLideres[0].id)
        Assertions.assertEquals(10, especiesLideres.size)
    }

    @Test
    fun test_reporteDeContagiosDeUnaUbicacionSinContagiosExistentes(){
        vectorService.infectar(vector.id!!, especie.id!!)
        vectorService.infectar(vector2.id!!, especie.id!!)
        vectorService.infectar(vector2.id!!, especie2.id!!)

        val reporte = service.reporteDeContagios(ubicacion3.nombre)

        Assertions.assertEquals(0, reporte.vectoresPresentes)
        Assertions.assertEquals("No hay vectores", reporte.nombreDeEspecieMasInfecciosa)
    }

    @Test
    fun test_reporteDeContagiosDeUnaUbicacionConContagiosExistentes(){
        vectorService.infectar(vector.id!!, especie.id!!)
        vectorService.infectar(vector2.id!!, especie.id!!)

        val reporte = service.reporteDeContagios(ubicacion.nombre)

        Assertions.assertEquals(2, reporte.vectoresPresentes)
        Assertions.assertEquals(2, reporte.vectoresInfectados)
        Assertions.assertEquals("Covid", reporte.nombreDeEspecieMasInfecciosa)
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}


