package ar.edu.unq.eperdemic.servicesTest.MongoDB

import ar.edu.unq.eperdemic.modelo.Especie
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
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import ar.edu.unq.eperdemic.services.exceptions.UbicacionMuyLejana
import ar.edu.unq.eperdemic.services.exceptions.UbicacionNoAlcanzable
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
class UbicacionServiceMongoTest {
    @Autowired private lateinit var service: UbicacionService
    @Autowired private lateinit var patogenoService: PatogenoService
    @Autowired private lateinit var vectorService: VectorService
    @Autowired private lateinit var distritoService: DistritoService
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var laPlata: Ubicacion
    private lateinit var lujan: Ubicacion
    private lateinit var quilmes: Ubicacion
    private lateinit var hornos: Ubicacion
    private lateinit var coordenadaLaPlata: Coordenada
    private lateinit var coordenadaLujan: Coordenada
    private lateinit var coordenadaQuilmes: Coordenada
    private lateinit var coordenadaHornos: Coordenada
    private lateinit var distrito: Distrito

    private lateinit var mosca: Vector
    private lateinit var patogeno: Patogeno
    private lateinit var especie: Especie

    @BeforeEach
    fun crearModelo() {
        //Modelo
        distrito = Distrito("AMBA", GeoJsonPolygon(listOf(
            GeoJsonPoint(-57.801031650712986, -34.967122018431695),
            GeoJsonPoint(-58.716728040198376, -33.934761836564604),
            GeoJsonPoint(-59.48239507773465,  -34.83559140382412),
            GeoJsonPoint(-57.801031650712986, -34.967122018431695)
        )))
        distritoService.crear(distrito)
        coordenadaLaPlata = Coordenada(-34.92145, -57.95453)
        coordenadaLujan = Coordenada(-34.57028, -59.105 )
        coordenadaQuilmes = Coordenada(-34.72904, -58.26374)
        coordenadaHornos = Coordenada(-34.89562966854183,-58.02120006418485)
        lujan = service.crearUbicacion("Lujan", coordenadaLujan)
        laPlata = service.crearUbicacion("La Plata", coordenadaLaPlata)
        quilmes = service.crearUbicacion("Quilmes", coordenadaQuilmes)
        hornos = service.crearUbicacion("Hornos", coordenadaHornos)
    }

    @Test
    fun test_alPersistirUnaUbicacionSeGeneraUnId(){
        Assertions.assertNotNull(quilmes.id)
    }

    @Test
    fun test_seRecuperaUnaUbicacionPersistida(){
        val coordenadaBernal = Coordenada(-34.71713267823277, -58.29955754505498)
        val bernal = service.crearUbicacion("bernal", coordenadaBernal)
        val ubicacionRecuperada = service.recuperarUbicacion(bernal.nombre)
        Assertions.assertEquals(ubicacionRecuperada.nombre, bernal.nombre)
    }

    @Test
    fun test_noSeRecuperaUnaUbicacionNoPersistida(){
        val coordenadaBernal = Coordenada(-34.71713267823277, -58.29955754505498)
        val bernal = service.crearUbicacion("bernal", coordenadaBernal)
        cleaner.cleanDB()

        Assertions.assertThrows(EmptyResultDataAccessException::class.java) {
            service.recuperarUbicacion(bernal.nombre)
        }
    }

    @Test
    fun test_seRecuperanTodasLasUbicacionesPersistidas(){
        val ubicacionesRecuperadas = service.recuperarTodos()
        Assertions.assertEquals(4, ubicacionesRecuperadas.size)
    }

    @Test
    fun seRecuperanLasCoordenadasDeLaUbicacion(){
        val coordenadas = service.recuperarCoordenadas(lujan.nombre)
        Assertions.assertEquals(coordenadaLujan.latitud, coordenadas.latitud)
        Assertions.assertEquals(coordenadaLujan.longitud, coordenadas.longitud)
    }

    // ======================= mover =======================
    @Test
    fun test_seIntentaMoverUnVectorAUnaUbicacionAMasDe100kmNoConectada(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, hornos.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", hornos.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", hornos.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", hornos.id!!)

        Assertions.assertThrows(UbicacionMuyLejana::class.java){
            service.mover(mosca.id!!, laPlata.nombre)
        }
    }

    @Test
    fun test_seIntentaMoverUnVectorAUnaUbicacionCercanaNoConectada(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, quilmes.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", quilmes.id!!)

        Assertions.assertThrows(UbicacionMuyLejana::class.java){
            service.mover(mosca.id!!, laPlata.nombre)
        }
    }

    @Test
    fun test_seIntentaMoverUnVectorAUnaUbicacionAMasDe100kmConectada(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, lujan.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", lujan.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", lujan.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", lujan.id!!)
        service.conectar(lujan.nombre, quilmes.nombre, "TIERRA")

        Assertions.assertThrows(UbicacionMuyLejana::class.java){
            service.mover(mosca.id!!, laPlata.nombre)
        }
    }

    @Test
    fun test_seMueveUnVectorAUnaUbicacionCercanaConectada(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, quilmes.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", quilmes.id!!)
        service.conectar(quilmes.nombre, laPlata.nombre, "TIERRA")

        Assertions.assertDoesNotThrow{service.mover(mosca.id!!, laPlata.nombre)}
    }

    @Test
    fun test_alMoverUnVectorCambiaDeUbicacion(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, quilmes.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", quilmes.id!!)
        service.conectar(quilmes.nombre, laPlata.nombre, "TIERRA")

        service.mover(mosca.id!!, laPlata.nombre)
        mosca = vectorService.recuperarVector(mosca.id!!)
        Assertions.assertEquals(laPlata.nombre, mosca.ubicacion.nombre)
    }

    @Test
    fun test_noSePuedeMoverUnInsectoAUnaUbicacionCercanaConectadaPorMar(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, quilmes.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", quilmes.id!!)
        service.conectar(quilmes.nombre, laPlata.nombre, "MAR")

        Assertions.assertThrows(UbicacionNoAlcanzable::class.java){
            service.mover(mosca.id!!, laPlata.nombre)
        }
    }

    @Test
    fun test_seIntentaMoverUnVectorAUnaUbicacionAMasDe100kmConectadaIndirectamente(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, lujan.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", lujan.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", lujan.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", lujan.id!!)
        service.conectar(lujan.nombre, quilmes.nombre, "TIERRA")
        service.conectar(quilmes.nombre, laPlata.nombre, "TIERRA")

        Assertions.assertThrows(UbicacionMuyLejana::class.java){
            service.mover(mosca.id!!, laPlata.nombre)
        }
    }

    @Test
    fun test_seIntentaMoverUnVectorAUnaUbicacionCercanaConectadaIndirectamente(){
        val wilde = service.crearUbicacion("Wilde", Coordenada(-34.7041, -58.3206))
        mosca = vectorService.crearVector(TipoDeVector.Insecto, wilde.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", wilde.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", wilde.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", wilde.id!!)
        service.conectar(wilde.nombre, quilmes.nombre, "TIERRA")
        service.conectar(quilmes.nombre, laPlata.nombre, "TIERRA")

        Assertions.assertThrows(UbicacionMuyLejana::class.java){
            service.mover(mosca.id!!, laPlata.nombre)
        }
    }

    // ======================= moverMasCorto =======================
    @Test
    fun test_sePuedeMoverMasCortoAUnaUbicacionAMasDe100kmConectada(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, hornos.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", hornos.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", hornos.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", hornos.id!!)
        service.conectar(hornos.nombre, quilmes.nombre, "TIERRA")

        Assertions.assertDoesNotThrow{service.moverMasCorto(mosca.id!!, quilmes.nombre)}
    }

    @Test
    fun test_alMoverMasCortoAMasDe100kmElVectorCambiaDeUbicacion(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, hornos.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", hornos.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", hornos.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", hornos.id!!)
        service.conectar(hornos.nombre, quilmes.nombre, "TIERRA")

        service.moverMasCorto(mosca.id!!, quilmes.nombre)
        mosca = vectorService.recuperarVector(mosca.id!!)
        Assertions.assertEquals(quilmes.nombre, mosca.ubicacion.nombre)
    }

    @Test
    fun test_sePuedeMoverMasCortoAUnaUbicacionAMenosDe100KMConectadaIndirectamente(){
        val wilde = service.crearUbicacion("Wilde", Coordenada(-34.7041, -58.3206))
        mosca = vectorService.crearVector(TipoDeVector.Insecto, wilde.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", wilde.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", wilde.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", wilde.id!!)
        service.conectar(wilde.nombre, quilmes.nombre, "TIERRA")
        service.conectar(quilmes.nombre, laPlata.nombre, "TIERRA")

        Assertions.assertDoesNotThrow{service.moverMasCorto(mosca.id!!, laPlata.nombre)}
    }
    @Test
    fun noEsPosibleCrearUbicacionSiNoExisteElArea(){
        Assertions.assertThrows(NotFoundException::class.java, {service.crearUbicacion("Tokio", Coordenada(35.6895, 139.69171))})
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}
