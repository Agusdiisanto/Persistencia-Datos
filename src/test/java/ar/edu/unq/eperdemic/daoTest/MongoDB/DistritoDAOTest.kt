package ar.edu.unq.eperdemic.daoTest.MongoDB

import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO.DistritoDAO
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
class DistritoDAOTest {
    private lateinit var utils : EntentyUtils

    @Autowired private lateinit var distritoDAO: DistritoDAO
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var quilmes: Distrito
    private lateinit var varela: Distrito
    private lateinit var amba: Distrito

    @BeforeEach
    fun crearModelo() {
        utils = EntentyUtils()
        quilmes = Distrito("Quilmes", GeoJsonPolygon(listOf(
            GeoJsonPoint(5.0, 6.0),
            GeoJsonPoint(10.0, 12.0),
            GeoJsonPoint(12.0, 2.0)
        )))
        amba = Distrito("AMBA", GeoJsonPolygon(listOf(
            GeoJsonPoint(-57.801031650712986, -34.967122018431695),
            GeoJsonPoint(-58.716728040198376, -33.934761836564604),
            GeoJsonPoint(-59.48239507773465,  -34.83559140382412),
            GeoJsonPoint(-57.801031650712986, -34.967122018431695)
        )))
        varela = Distrito("Varela", GeoJsonPolygon(listOf(
            GeoJsonPoint(10.0, 12.0),
            GeoJsonPoint(9.0, 15.0),
            GeoJsonPoint(14.0, 15.0)
        )))
    }

    @Test
    fun test_alPersistirUnDistritoSeGeneraUnId(){
        distritoDAO.save(quilmes)
        Assertions.assertNotNull(quilmes.id)
    }

    @Test
    fun test_losDistritosSePersistenConDiferentesId() {
        distritoDAO.save(varela)
        distritoDAO.save(quilmes)
        Assertions.assertTrue(quilmes.id != varela.id)
    }

    @Test
    fun test_sePersisteUnDistrito(){
        distritoDAO.save(quilmes)
        val quilmesRecuperado = distritoDAO.findByIdOrNull(quilmes.id)!!
        Assertions.assertEquals(quilmes.nombre, quilmesRecuperado.nombre)
    }

    @Test
    fun test_noSeRecuperaUnDistritoNoPersistido() {
        distritoDAO.save(quilmes)
        distritoDAO.deleteAll()

        Assertions.assertThrows(NotFoundException::class.java) {
            distritoDAO.findByIdOrNull(quilmes.id) ?:
            throw NotFoundException("No se pudo encontrar la entidad con ID ${quilmes.id!!}")
        }
    }

    @Test
    fun test_sePersistenVariosDistritosYSeLosRecupera() {
        distritoDAO.save(quilmes)
        distritoDAO.save(varela)

        val distritos = distritoDAO.findAll()
        Assertions.assertEquals(2, distritos.count())
    }

    @Test
    fun test_seRecuperanTodosLosDistritosSinDistritosPersistidos() {
        val distritos = distritoDAO.findAll()
        Assertions.assertEquals(0, distritos.count())
    }

    // ================ existeDistritoEnArea ================
    @Test
    fun test_seIndicaQueHayDistritoEnAreaDada(){
        distritoDAO.save(quilmes)
        val ezpeleta = Distrito("Ezpeleta", GeoJsonPolygon(listOf(
            GeoJsonPoint(5.0, 6.0),
            GeoJsonPoint(10.0, 12.0),
            GeoJsonPoint(12.0, 2.0)
        )))
        Assertions.assertTrue(distritoDAO.existeDistritoEnArea(ezpeleta.area))
    }

    @Test
    fun test_seIndicaQueNoHayDistritoEnAreaDada(){
        val ezpeleta = Distrito("Ezpeleta", GeoJsonPolygon(listOf(
            GeoJsonPoint(5.0, 6.0),
            GeoJsonPoint(10.0, 12.0),
            GeoJsonPoint(12.0, 2.0)
        )))
        Assertions.assertFalse(distritoDAO.existeDistritoEnArea(ezpeleta.area))
    }

    // ================ findDistritoIdByAreaWithin ================
    @Test
    fun distritoContienePunto(){
        distritoDAO.save(amba)
        val punto = GeoJsonPoint(-57.95153904885443,-34.9178414201112)
        Assertions.assertEquals(amba.id, distritoDAO.findDistritoIdByAreaWithin(punto)!!.id)
    }

    @Test
    fun distritoNoContienePunto(){
        distritoDAO.save(amba)
        val punto = GeoJsonPoint(-47.92569063040435, -15.845979999316498)
        Assertions.assertNull(distritoDAO.findDistritoIdByAreaWithin(punto))
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}