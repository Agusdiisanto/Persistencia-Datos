package ar.edu.unq.eperdemic.servicesTest.Firebase

import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.ubicacion.*
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO.UbicacionFirestoreDAOImpl
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.Cleaner
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.junit.jupiter.api.*
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import java.util.*


@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionServiceFirebaseTest {
    private lateinit var utils : EntentyUtils
    @Autowired private lateinit var FirebaseDAO: UbicacionFirestoreDAOImpl
    @Autowired private lateinit var ubicacionService: UbicacionServiceImpl
    @Autowired private lateinit var vectorService: VectorService
    @Autowired private lateinit var serviceDistrito : DistritoService
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var quilmes : Ubicacion
    private lateinit var bernal : Ubicacion
    private lateinit var bosques : Ubicacion
    private lateinit var mosca: Vector
    private lateinit var mosquito: Vector
    private lateinit var vaca: Vector
    private lateinit var iguana: Vector
    private lateinit var murcielago: Vector
    private lateinit var mono: Vector
    private lateinit var mapache: Vector
    private lateinit var conejo: Vector
    private lateinit var abeja: Vector
    private lateinit var oveja: Vector
    private lateinit var lobo: Vector
    private lateinit var perro: Vector
    private lateinit var gato: Vector
    private lateinit var hipopotamo: Vector
    private lateinit var vibora: Vector
    private lateinit var carpincho: Vector
    private lateinit var puma: Vector

    private lateinit var coordenada : Coordenada
    private lateinit var distrito : Distrito

    @BeforeEach
    fun crearModelo(){
        distrito = Distrito("Parque", GeoJsonPolygon(listOf(GeoJsonPoint(-122.4194, 37.7749), GeoJsonPoint(-122.4085, 37.7756), GeoJsonPoint(-122.4300,37.7833))))
        serviceDistrito.crear(distrito)

        utils = EntentyUtils()

        coordenada = Coordenada(37.7749, -122.4194)
        quilmes = ubicacionService.crearUbicacion("Quilmes", coordenada)
        bosques = ubicacionService.crearUbicacion("Bosques", coordenada)
        bernal = ubicacionService.crearUbicacion("Bernal", coordenada)
        mosca = vectorService.crearVector(TipoDeVector.Insecto, bernal.id!!)
    }

    // ============== tests de crearUbicacion ==============
    @Test
    fun test_sePersisteUnaUbicacion(){
        val berazategui = ubicacionService.crearUbicacion("Berazategui",coordenada)
        val ubicacionRecuperada = FirebaseDAO.findByName(berazategui.nombre)
        Assertions.assertEquals(berazategui.nombre, ubicacionRecuperada.nombre)
    }

    // ============== tests de recuperarAlerta ==============
    @Test
    fun test_seRecuperaLaAlertaVerdeDeUnaUbicacion(){
        val alertaRecuperada = ubicacionService.recuperarAlerta(quilmes.nombre)
        Assertions.assertEquals("Verde", alertaRecuperada)
    }

    @Test
    fun test_seRecuperaLaAlertaAmarillaDeUnaUbicacion(){
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        vaca = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        iguana = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        murcielago = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mono = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mapache = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        conejo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        abeja = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        oveja = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        lobo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        val alertaRecuperada = ubicacionService.recuperarAlerta(bosques.nombre)
        Assertions.assertEquals("Amarillo", alertaRecuperada)
    }

    @Test
    fun test_seRecuperaLaAlertaRojaDeUnaUbicacion(){
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        vaca = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        iguana = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        murcielago = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mono = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mapache = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        conejo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        abeja = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        oveja = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        lobo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        perro = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        gato = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        hipopotamo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        vibora = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        carpincho = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        puma = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        val alertaRecuperada = ubicacionService.recuperarAlerta(bosques.nombre)
        Assertions.assertEquals("Rojo", alertaRecuperada)
    }

    // ============== tests de mover y cantidadDeVectores ==============
    @Test
    fun test_seMueveUnVectorHaciaUnaUbicacionYSeAumentaCantidadDeVectoresDeUbicacionFinal(){
        ubicacionService.conectar(bernal.nombre, quilmes.nombre, "Tierra")

        ubicacionService.mover(mosca.id!!, quilmes.nombre)
        val vectorRecuperado = vectorService.recuperarVector(mosca.id!!)
        val ubicacionRecuperada = FirebaseDAO.findByName(quilmes.nombre)

        Assertions.assertEquals(quilmes.nombre, vectorRecuperado.ubicacion.nombre)
        Assertions.assertEquals(1, ubicacionRecuperada.cantidadVectores)

    }

    @Test
    fun test_seMueveUnVectorHaciaUnaUbicacionSeDecrementaLaCantidadDeVectoresEnLaUbicacionInicial(){
        ubicacionService.conectar(bernal.nombre, quilmes.nombre, "Tierra")
        val bernalRecuperada = FirebaseDAO.findByName(bernal.nombre)
        Assertions.assertEquals(1, bernalRecuperada.cantidadVectores)

        ubicacionService.mover(mosca.id!!, quilmes.nombre)
        val vectorRecuperado = vectorService.recuperarVector(mosca.id!!)
        val ubicacionRecuperada = FirebaseDAO.findByName(bernal.nombre)

        Assertions.assertEquals(quilmes.nombre, vectorRecuperado.ubicacion.nombre)
        Assertions.assertEquals(0, ubicacionRecuperada.cantidadVectores)

    }

    // ============== tests de moverMasCorto ==============
    @Test
    fun test_seMueveUnVectorPorElCaminoMasCortoYSeAumentaLaCantidadDeVectoresDeLaUbicacionFinal(){
        ubicacionService.conectar(bernal.nombre, quilmes.nombre, "Tierra")

        ubicacionService.moverMasCorto(mosca.id!!, quilmes.nombre)
        val vectorRecuperado = vectorService.recuperarVector(mosca.id!!)
        val ubicacionRecuperada = FirebaseDAO.findByName(quilmes.nombre)

        Assertions.assertEquals(quilmes.nombre, vectorRecuperado.ubicacion.nombre)
        Assertions.assertEquals(1, ubicacionRecuperada.cantidadVectores)

    }

    @Test
    fun test_seMueveUnVectorPorElCaminoMasCortoYSeDecrementaLaCantidadDeVectoresDeLaUbicacionInicial(){
        ubicacionService.conectar(bernal.nombre, quilmes.nombre, "Tierra")
        val bernalRecuperada = FirebaseDAO.findByName(bernal.nombre)
        Assertions.assertEquals(1, bernalRecuperada.cantidadVectores)

        ubicacionService.moverMasCorto(mosca.id!!, quilmes.nombre)
        val vectorRecuperado = vectorService.recuperarVector(mosca.id!!)
        val ubicacionRecuperada = FirebaseDAO.findByName(bernal.nombre)

        Assertions.assertEquals(quilmes.nombre, vectorRecuperado.ubicacion.nombre)
        Assertions.assertEquals(0, ubicacionRecuperada.cantidadVectores)

    }

    // ============== tests de ubicacionesConAlerta ==============
    @Test
    fun seObtienenLasUbicacionesConAlertaVerde(){
        val ubicacionesVerdes = ubicacionService.ubicacionesConAlerta(Alerta.Verde)
        Assertions.assertEquals(3, ubicacionesVerdes.size)
        Assertions.assertTrue(ubicacionesVerdes.any{u -> u.nombre == quilmes.nombre})
        Assertions.assertTrue(ubicacionesVerdes.any{u -> u.nombre == bernal.nombre})
    }

    @Test
    fun seObtienenLasUbicacionesConAlertaAmarilla(){
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        vaca = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        iguana = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        murcielago = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mono = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mapache = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        conejo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        abeja = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        oveja = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        lobo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        val ubicacionesAmarillas = ubicacionService.ubicacionesConAlerta(Alerta.Amarillo)
        Assertions.assertEquals(1, ubicacionesAmarillas.size)
        Assertions.assertTrue(ubicacionesAmarillas.any{u -> u.nombre == bosques.nombre})

    }

    @Test
    fun seObtienenLasUbicacionesConAlertaRoja(){
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        vaca = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        iguana = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        murcielago = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mono = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mapache = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        conejo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        abeja = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        oveja = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        lobo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        perro = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        gato = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        hipopotamo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        vibora = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        carpincho = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        puma = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        val ubicacionesRojas = ubicacionService.ubicacionesConAlerta(Alerta.Rojo)
        Assertions.assertEquals(1, ubicacionesRojas.size)
        Assertions.assertTrue(ubicacionesRojas.any{u -> u.nombre == bosques.nombre})

    }

    @Test
    fun test_vectoresSeMuevenDeUbicacionYLaUbicacionInicialCambiaAAlertaAmarilla(){
        ubicacionService.conectar(bosques.nombre, quilmes.nombre, "Tierra")
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        vaca = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        iguana = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        murcielago = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mono = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mapache = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        conejo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        abeja = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        oveja = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        lobo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        perro = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        gato = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        hipopotamo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        vibora = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        carpincho = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        puma = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        val ubicacionesRojas = ubicacionService.ubicacionesConAlerta(Alerta.Rojo)
        Assertions.assertEquals(1, ubicacionesRojas.size)
        Assertions.assertTrue(ubicacionesRojas.any{u -> u.nombre == bosques.nombre})

        ubicacionService.mover(vibora.id!!, quilmes.nombre)
        ubicacionService.mover(carpincho.id!!, quilmes.nombre)
        ubicacionService.mover(puma.id!!, quilmes.nombre)
        ubicacionService.mover(hipopotamo.id!!, quilmes.nombre)

        val ubicacionesRecuperadasRojas = ubicacionService.ubicacionesConAlerta(Alerta.Rojo)

        Assertions.assertEquals(0, ubicacionesRecuperadasRojas.size)
        Assertions.assertFalse(ubicacionesRecuperadasRojas.any{u -> u.nombre == bosques.nombre})

    }

    @Test
    fun test_vectoresSeMuevenDeUbicacionYLaUbicacionInicialCambiaAAlertaVerde(){
        ubicacionService.conectar(bosques.nombre, quilmes.nombre, "Tierra")
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        vaca = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        iguana = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        murcielago = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mono = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mapache = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        conejo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        abeja = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        oveja = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        lobo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        val ubicacionesAmarillas = ubicacionService.ubicacionesConAlerta(Alerta.Amarillo)
        Assertions.assertEquals(1, ubicacionesAmarillas.size)
        Assertions.assertTrue(ubicacionesAmarillas.any{u -> u.nombre == bosques.nombre})

        ubicacionService.mover(lobo.id!!, quilmes.nombre)
        ubicacionService.mover(oveja.id!!, quilmes.nombre)
        ubicacionService.mover(abeja.id!!, quilmes.nombre)
        ubicacionService.mover(conejo.id!!, quilmes.nombre)
        ubicacionService.mover(mapache.id!!, quilmes.nombre)
        ubicacionService.mover(mono.id!!, quilmes.nombre)

        val ubicacionesRecuperadaAmarillas = ubicacionService.ubicacionesConAlerta(Alerta.Amarillo)

        Assertions.assertEquals(0, ubicacionesRecuperadaAmarillas.size)
        Assertions.assertFalse(ubicacionesRecuperadaAmarillas.any{u -> u.nombre == bosques.nombre})

    }

    @Test
    fun test_vectoresSeMuevenDeUbicacionYLaUbicacionFinalCambiaAAlertaAmarilla(){
        ubicacionService.conectar(bosques.nombre, quilmes.nombre, "Tierra")
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        vaca = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        iguana = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        murcielago = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mono = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        mapache = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        conejo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        abeja = vectorService.crearVector(TipoDeVector.Insecto, bosques.id!!)
        oveja = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        lobo = vectorService.crearVector(TipoDeVector.Animal, bosques.id!!)
        val ubicacionesAmarillas = ubicacionService.ubicacionesConAlerta(Alerta.Amarillo)
        Assertions.assertEquals(1, ubicacionesAmarillas.size)
        Assertions.assertTrue(ubicacionesAmarillas.any{u -> u.nombre == bosques.nombre})

        ubicacionService.mover(lobo.id!!, quilmes.nombre)
        ubicacionService.mover(oveja.id!!, quilmes.nombre)
        ubicacionService.mover(abeja.id!!, quilmes.nombre)
        ubicacionService.mover(conejo.id!!, quilmes.nombre)
        ubicacionService.mover(mapache.id!!, quilmes.nombre)
        ubicacionService.mover(mono.id!!, quilmes.nombre)
        ubicacionService.mover(iguana.id!!, quilmes.nombre)

        val ubicacionesRecuperadaAmarillas = ubicacionService.ubicacionesConAlerta(Alerta.Amarillo)

        Assertions.assertEquals(1, ubicacionesRecuperadaAmarillas.size)
        Assertions.assertTrue(ubicacionesRecuperadaAmarillas.any{u -> u.nombre == quilmes.nombre})
        Assertions.assertFalse(ubicacionesRecuperadaAmarillas.any{u -> u.nombre == bosques.nombre})


    }

    @AfterEach
    fun clearUp() {
        cleaner.cleanDB()
    }

}