package ar.edu.unq.eperdemic.servicesTest.MongoDB

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exceptions.DistritoSuperpuesto
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
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
class DistritoServiceTest {
    @Autowired private lateinit var service: DistritoService
    @Autowired private lateinit var patogenoService: PatogenoService
    @Autowired private lateinit var vectorService: VectorService
    @Autowired private lateinit var ubicacionService: UbicacionService
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var mosca: Vector
    private lateinit var patogeno: Patogeno
    private lateinit var coordenadaLujan: Coordenada
    private lateinit var coordenadaQuilmes: Coordenada
    private lateinit var coordenadaRosario: Coordenada
    private lateinit var lujan: Ubicacion
    private lateinit var quilmes: Ubicacion
    private lateinit var rosario: Ubicacion

    lateinit var amba: Distrito
    lateinit var grosario: Distrito

    @BeforeEach
    fun setUp() {
        //Modelo
        amba = Distrito("AMBA", GeoJsonPolygon(listOf(
            GeoJsonPoint(-57.801031650712986, -34.967122018431695),
            GeoJsonPoint(-58.716728040198376, -33.934761836564604),
            GeoJsonPoint(-59.48239507773465,  -34.83559140382412),
            GeoJsonPoint(-57.801031650712986, -34.967122018431695)
        )))
        grosario = Distrito("Gran Rosario", GeoJsonPolygon(listOf(
            GeoJsonPoint(-60.68822879161071, -32.86866266373788),
            GeoJsonPoint(-60.80135216849199, -32.94918159785567),
            GeoJsonPoint(-60.68085118007511, -33.106909667096694),
            GeoJsonPoint(-60.55420218204462, -33.037873947881295)
        )))
        service.crear(amba)
        service.crear(grosario)

        coordenadaLujan = Coordenada(-34.57028, -59.105)
        coordenadaQuilmes = Coordenada(-34.72904, -58.26374)
        coordenadaRosario = Coordenada(-32.96051454063553,-60.66284450695416)
        lujan = ubicacionService.crearUbicacion("Lujan", coordenadaLujan)
        quilmes = ubicacionService.crearUbicacion("Quilmes", coordenadaQuilmes)
        rosario = ubicacionService.crearUbicacion("Rosario", coordenadaRosario)
    }

    @Test
    fun sePersisteUnDistrito(){
        val distritoRecuperado = service.recuperar(amba.nombre)
        Assertions.assertNotNull(distritoRecuperado.id)
    }

    @Test
    fun test_seRecuperaUnDistritoPersistido(){
        val distritoRecuperado = service.recuperar(amba.nombre)
        Assertions.assertEquals(distritoRecuperado.nombre, amba.nombre)
    }

    @Test
    fun  test_noSeRecuperaUnDistritoNoPersistida(){
        cleaner.cleanDB()

        Assertions.assertThrows(EmptyResultDataAccessException::class.java) {
            service.recuperar(amba.nombre)
        }
    }

    // ================ distritoMasEnfermo ================
    @Test
    fun test_noHayDistritoEnfermo(){
        Assertions.assertThrows(NotFoundException::class.java){
            service.distritoMasEnfermo()
        }
    }

    @Test
    fun test_seConoceElDistritoMasEnfermoEnLasUbicacionesInfectadas(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, quilmes.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        patogenoService.agregarEspecie(mosca.id!!, "Gripe-A", quilmes.id!!)

        val distritoRecuperado = service.distritoMasEnfermo()
        Assertions.assertEquals(amba.nombre, distritoRecuperado.nombre)
    }

    @Test
    fun test_seConoceElDistritoMasEnfermoEnVariasUbicacionesInfectadas(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, quilmes.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, lujan.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, lujan.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", lujan.id!!)

        val distritoRecuperado = service.distritoMasEnfermo()
        Assertions.assertEquals(amba.nombre, distritoRecuperado.nombre)
    }

    @Test
    fun test_seConoceElDistritoMasEnfermoEntreDosConUbicacionesInfectadas(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, quilmes.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, lujan.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, rosario.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", lujan.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", rosario.id!!)

        val distritoRecuperado = service.distritoMasEnfermo()
        Assertions.assertEquals(amba.nombre, distritoRecuperado.nombre)
    }

    @Test
    fun dosDistritosNoPuedenSuperponerse(){
        val rosarioS = Distrito("Rosario Separatista", GeoJsonPolygon(listOf(
            GeoJsonPoint(-60.870046138550094, -33.05162789563115),
            GeoJsonPoint(-60.80486665970518, -33.128451180359),
            GeoJsonPoint(-60.65042462875836, -33.036566189096206),
            GeoJsonPoint( -60.72079170794744, -32.96605605761919)
        )))

        Assertions.assertThrows(DistritoSuperpuesto::class.java){service.crear(rosarioS)}

    }

    @Test
    fun dosDistritosPuedenLimitar(){
        val pRosario = Distrito("Pequenio Rosario", GeoJsonPolygon(listOf(
            GeoJsonPoint(-60.680861765301984, -33.106928129131326),
            GeoJsonPoint(-60.68192286687008, -33.1055245399236),
            GeoJsonPoint(-60.68346066545183, -33.1074648472973),
            GeoJsonPoint( -60.6796701036821, -33.1082824584291)
        )))
        Assertions.assertNotNull(service.crear(pRosario).id)
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}