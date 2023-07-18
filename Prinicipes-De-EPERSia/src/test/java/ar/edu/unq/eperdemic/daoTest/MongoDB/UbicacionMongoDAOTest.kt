package ar.edu.unq.eperdemic.daoTest.MongoDB

import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionMongoDB
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO.UbicacionMongoDAO
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import ar.edu.unq.eperdemic.services.impl.Cleaner
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionMongoDAOTest {
    private lateinit var utils: EntentyUtils

    // Modelo
    private lateinit var quilmes: UbicacionMongoDB
    private lateinit var laPlata: UbicacionMongoDB
    private lateinit var lujan: UbicacionMongoDB
    private lateinit var avellaneda: Distrito
    private lateinit var varela: Distrito
    private val km100ARadianes: Double = 100.0 / 6371

    // DAO
    @Autowired private lateinit var dao: UbicacionMongoDAO
    @Autowired private lateinit var distritoDAO: DistritoDAO
    @Autowired private lateinit var cleaner: Cleaner

    @BeforeEach
    fun crearModelo(){
        utils = EntentyUtils()
        laPlata = UbicacionMongoDB("La Plata", Coordenada(-34.92145, -57.95453))
        quilmes = UbicacionMongoDB("Quilmes", Coordenada(-34.72904, -58.26374))
        lujan = UbicacionMongoDB("Lujan", Coordenada(-34.57028, -59.105 ))
        avellaneda = Distrito("Avellaneda", GeoJsonPolygon(listOf(
            GeoJsonPoint(5.0, 10.0),
            GeoJsonPoint(5.0, 15.0),
            GeoJsonPoint(10.0, 15.0),
            GeoJsonPoint(10.0, 10.0),
            GeoJsonPoint(5.0, 10.0)
        )))
        varela = Distrito("Varela", GeoJsonPolygon(listOf(
            GeoJsonPoint(15.0, 10.0),
            GeoJsonPoint(15.0, 15.0),
            GeoJsonPoint(20.0, 15.0),
            GeoJsonPoint(20.0, 10.0),
            GeoJsonPoint(15.0, 10.0)
        )))
        distritoDAO.save(avellaneda)
        distritoDAO.save(varela)
    }

    @Test
    fun test_alPersistirUnaUbicacionSeGeneraUnID(){
        dao.save(quilmes)
        Assertions.assertNotNull(quilmes.id)
    }

    @Test
    fun test_lasUbicacionesSePersistenConDiferenteID(){
        dao.save(quilmes)
        dao.save(laPlata)
        Assertions.assertTrue(quilmes.id != laPlata.id)
    }

    @Test
    fun test_sePersisteUnaUbicacion() {
        dao.save(quilmes)
        val quilmesRecuperado = dao.findByIdOrNull(quilmes.id!!)!!
        Assertions.assertEquals(quilmes.nombre, quilmesRecuperado.nombre)
        Assertions.assertEquals(quilmes.id!!, quilmesRecuperado.id)
    }

    @Test
    fun test_noSeRecuperaUnaUbicacionNoPersistida() {
        dao.save(laPlata)
        cleaner.cleanDB()

        Assertions.assertThrows(NotFoundException::class.java) {
            dao.findByIdOrNull(laPlata.id!!) ?:
            throw NotFoundException("No se pudo encontrar la entidad con ID ${laPlata.id!!}")
        }
    }

    @Test
    fun test_sePersistenVariasUbicacionesYSeLasRecupera() {
        dao.save(laPlata)
        dao.save(quilmes)

        val ubicaciones = dao.findAll()
        Assertions.assertEquals(2, ubicaciones.count())
    }

    @Test
    fun test_seRecuperanTodasLasUbicacionesSinUbicacionesPersistidas() {
        val ubicaciones = dao.findAll()
        Assertions.assertEquals(0, ubicaciones.count())
    }

    // ================ distritoMasEnfermo ================
    @Test
    fun test_seObtieneElDistritoMasEnfermo() {
        val distritoRecuperado = distritoDAO.findByIdOrNull(avellaneda.id)!!
        val wilde = UbicacionMongoDB("Wilde", Coordenada(6.0, 11.0))
        val sarandi = UbicacionMongoDB("Sarandi", Coordenada(7.0, 11.0))
        wilde.actualizarDistrito(distritoRecuperado)
        sarandi.actualizarDistrito(distritoRecuperado)
        dao.save(wilde)
        dao.save(sarandi)
        dao.save(laPlata)
        dao.save(quilmes)
        val ubicacionesInfectadas = listOf("La Plata", "Wilde", "Sarandi")
        Assertions.assertEquals(avellaneda.id, dao.distritoMasEnfermo(ubicacionesInfectadas))
    }

    @Test
    fun test_seObtienePosiblesDistritosEnfermos() {
        val distrito1Recuperado = distritoDAO.findByIdOrNull(varela.id)!!
        laPlata.actualizarDistrito(distrito1Recuperado)
        quilmes.actualizarDistrito(distrito1Recuperado)
        val distrito2Recuperado = distritoDAO.findByIdOrNull(avellaneda.id)!!
        val wilde = UbicacionMongoDB("Wilde", Coordenada(6.0, 11.0))
        val sarandi = UbicacionMongoDB("Sarandi", Coordenada(7.0, 11.0))
        wilde.actualizarDistrito(distrito2Recuperado)
        sarandi.actualizarDistrito(distrito2Recuperado)
        dao.save(wilde)
        dao.save(sarandi)
        dao.save(laPlata)
        dao.save(quilmes)
        val ubicacionesInfectadas = listOf("Quilmes", "Bernal", "Wilde", "Sarandi")
        val distritoMasEnfermo = dao.distritoMasEnfermo(ubicacionesInfectadas)
        val posiblesDistritosEnfermos = listOf(avellaneda.id, varela.id)
        Assertions.assertTrue(
            posiblesDistritosEnfermos.contains(distritoMasEnfermo)
        )
    }

    // ================ estaDentroDelRadio ================
    @Test
    fun testLaPlataEstaAMenosDe100KMDeQuilmes2() {
        dao.save(laPlata)
        dao.save(quilmes)
        val longLaPlata = laPlata.coordenada.x
        val latLaPlata = laPlata.coordenada.y

        val estaAMenosDe100KM = dao.estaAMenorDistanciaDe(longLaPlata, latLaPlata,km100ARadianes, quilmes.nombre)
        Assertions.assertTrue(estaAMenosDe100KM)
    }

    @Test
    fun testLaPlataEstaAMasDe100KMDeLujan() {
        dao.save(laPlata)
        dao.save(lujan)
        val longLaPlata = laPlata.coordenada.x
        val latLaPlata = laPlata.coordenada.y

        val estaAMenosDe100KM = dao.estaAMenorDistanciaDe(longLaPlata, latLaPlata,km100ARadianes, lujan.nombre)
        Assertions.assertFalse(estaAMenosDe100KM)
    }

    @Test
    fun seObtienenLasUbicacionesDeUnArea(){
        dao.save(laPlata)
        dao.save(lujan)
        dao.save(quilmes)
        val zonaAmba =  GeoJsonPolygon(listOf(
                GeoJsonPoint(-57.801031650712986, -34.967122018431695),
                GeoJsonPoint(-58.716728040198376, -33.934761836564604),
                GeoJsonPoint(-59.48239507773465,  -34.83559140382412),
                GeoJsonPoint(-57.801031650712986, -34.967122018431695)
        ))
        val ubicacionesAmba = dao.findByCoordenadaWithin(zonaAmba)
        Assertions.assertEquals(3, ubicacionesAmba.size )
        Assertions.assertTrue(ubicacionesAmba.any{u -> u.nombre == quilmes.nombre})
    }
    @Test
    fun seExcluyenUbicacionesDentroDeUnArea(){
        dao.save(laPlata)
        dao.save(lujan)
        dao.save(quilmes)
        val zonaAmba =  GeoJsonPolygon(listOf(
                GeoJsonPoint(-60.68822879161071, -32.86866266373788),
                GeoJsonPoint(-60.80135216849199, -32.94918159785567),
                GeoJsonPoint(-60.68085118007511, -33.106909667096694),
                GeoJsonPoint(-60.55420218204462, -33.037873947881295),
                GeoJsonPoint(-60.68822879161071, -32.86866266373788)
                ))
        val ubicacionesAmba = dao.findByCoordenadaWithin(zonaAmba)
        Assertions.assertEquals(0, ubicacionesAmba.size )
    }

    @AfterEach
    fun clearUp() {
        cleaner.cleanDB()
    }
}
