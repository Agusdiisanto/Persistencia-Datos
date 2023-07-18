package ar.edu.unq.eperdemic.daoTest.Neo4J

import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionNeo4j
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.Neo4JDAO.UbicacionNeo4jDAO
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import ar.edu.unq.eperdemic.services.impl.Cleaner
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionNeo4jDAOTest {
    private lateinit var utils : EntentyUtils

    @Autowired private lateinit var ubicacionNeo4jDAO: UbicacionNeo4jDAO
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var bernal: UbicacionNeo4j
    private lateinit var quilmes: UbicacionNeo4j

    @BeforeEach
    fun crearModelo() {
        utils = EntentyUtils()
        bernal = UbicacionNeo4j("Bernal")
        quilmes = UbicacionNeo4j("Quilmes")
    }

    @Test
    fun test_alPersistirUnaUbicacionSeGeneraUnId() {
        ubicacionNeo4jDAO.save(bernal)
        Assertions.assertNotNull(bernal.id)
    }

    @Test
    fun test_lasUbicacionesSePersistenConDiferentesId() {
        ubicacionNeo4jDAO.save(bernal)
        ubicacionNeo4jDAO.save(quilmes)
        Assertions.assertTrue(quilmes.id != bernal.id)
    }

    @Test
    fun test_sePersisteUnaUbicacion() {
        ubicacionNeo4jDAO.save(bernal)
        val bernalRecuperado = ubicacionNeo4jDAO.findByIdOrNull(bernal.id!!)!!
        Assertions.assertEquals(bernal.nombre, bernalRecuperado.nombre)
        Assertions.assertEquals(bernal.id!!, bernalRecuperado.id)
    }

    @Test
    fun test_noSeRecuperaUnaUbicacionNoPersistida() {
        ubicacionNeo4jDAO.save(bernal)
        cleaner.cleanDB()

        Assertions.assertThrows(NotFoundException::class.java) {
            ubicacionNeo4jDAO.findByIdOrNull(bernal.id!!) ?:
            throw NotFoundException("No se pudo encontrar la entidad con ID ${bernal.id!!}")
        }
    }

    @Test
    fun test_sePersistenVariasUbicacionesYSeLasRecupera() {
        ubicacionNeo4jDAO.save(bernal)
        ubicacionNeo4jDAO.save(quilmes)

        val ubicaciones = ubicacionNeo4jDAO.findAll()
        Assertions.assertEquals(2, ubicaciones.count())
    }

    @Test
    fun test_seRecuperanTodasLasUbicacionesSinUbicacionesPersistidas() {
        val ubicaciones = ubicacionNeo4jDAO.findAll()
        Assertions.assertEquals(0, ubicaciones.count())
    }

    // ================ findByNombre ================
    @Test
    fun test_seRecuperaUnaUbicacionSegunSuNombre() {
        ubicacionNeo4jDAO.save(bernal)

        val bernalRecuperada = ubicacionNeo4jDAO.findByNombre(bernal.nombre)
        Assertions.assertEquals(bernal.id, bernalRecuperada.id)
        Assertions.assertEquals(bernal.nombre, bernalRecuperada.nombre)
    }

    @Test
    fun test_noSeRecuperaUnaUbicacionNoPersistidaSegunSuNombre() {
        ubicacionNeo4jDAO.save(bernal)
        cleaner.cleanDB()

        Assertions.assertThrows(EmptyResultDataAccessException::class.java) {
            ubicacionNeo4jDAO.findByNombre(bernal.nombre)
        }
    }

    // ================ caminoMasCorto ================
    @Test
    fun test_noHayCaminoParaDosUbicacionesNoConectadas() {
        ubicacionNeo4jDAO.save(bernal)
        ubicacionNeo4jDAO.save(quilmes)

        val ubicacionesIntermedias = ubicacionNeo4jDAO
            .caminoMasCorto(bernal.nombre, quilmes.nombre, TipoDeVector.Persona.rutasTransitables)
        Assertions.assertNotNull(ubicacionesIntermedias)
        Assertions.assertEquals(0, ubicacionesIntermedias.size)
    }

    @Test
    fun test_noHayCaminoParaDosUbicacionesNoConectadasDeFormaTransitable() {
        val calzada = UbicacionNeo4j("Calzada")
        val varela = UbicacionNeo4j("Varela")
        val berazategui = UbicacionNeo4j("Berazategui")
        val wilde = UbicacionNeo4j("Wilde")
        varela.conectar(calzada, "TIERRA")
        berazategui.conectar(varela, "TIERRA")
        quilmes.conectar(berazategui, "TIERRA")
        quilmes.conectar(varela, "TIERRA")
        bernal.conectar(quilmes, "TIERRA")
        bernal.conectar(wilde, "TIERRA")
        ubicacionNeo4jDAO.save(bernal)

        val ubicacionesIntermedias = ubicacionNeo4jDAO
            .caminoMasCorto(berazategui.nombre, wilde.nombre, TipoDeVector.Persona.rutasTransitables)
        Assertions.assertNotNull(ubicacionesIntermedias)
        Assertions.assertEquals(0, ubicacionesIntermedias.size)
    }

    @Test
    fun test_hayCaminoEntreDosUbicacionesConectadas() {
        bernal.conectar(quilmes, "TIERRA")
        ubicacionNeo4jDAO.save(bernal)

        val ubicacionesIntermedias = ubicacionNeo4jDAO
            .caminoMasCorto(bernal.nombre, quilmes.nombre, TipoDeVector.Persona.rutasTransitables)
        Assertions.assertEquals(2, ubicacionesIntermedias.size)
        Assertions.assertTrue(ubicacionesIntermedias.any{ u -> u == bernal.nombre })
        Assertions.assertTrue(ubicacionesIntermedias.any{ u -> u == quilmes.nombre })
    }

    @Test
    fun test_hayCaminoEntreDosUbicacionesConectadasIndirectamente() {
        val calzada = UbicacionNeo4j("Calzada")
        val varela = UbicacionNeo4j("Varela")
        varela.conectar(calzada, "TIERRA")
        quilmes.conectar(varela, "TIERRA")
        bernal.conectar(quilmes, "TIERRA")
        ubicacionNeo4jDAO.save(bernal)

        val ubicacionesIntermedias = ubicacionNeo4jDAO
            .caminoMasCorto(bernal.nombre, calzada.nombre, TipoDeVector.Persona.rutasTransitables)
        Assertions.assertEquals(4, ubicacionesIntermedias.size)
    }

    @Test
    fun test_hayCaminoEntreDosUbicacionesConectadasIndirectamenteVariosCaminos() {
        val calzada = UbicacionNeo4j("Calzada")
        val varela = UbicacionNeo4j("Varela")
        val berazategui = UbicacionNeo4j("Berazategui")
        val wilde = UbicacionNeo4j("Wilde")
        varela.conectar(calzada, "TIERRA")
        berazategui.conectar(varela, "TIERRA")
        quilmes.conectar(berazategui, "TIERRA")
        quilmes.conectar(varela, "TIERRA")
        bernal.conectar(quilmes, "TIERRA")
        bernal.conectar(wilde, "TIERRA")
        ubicacionNeo4jDAO.save(bernal)

        val ubicacionesIntermedias = ubicacionNeo4jDAO
            .caminoMasCorto(bernal.nombre, calzada.nombre, TipoDeVector.Persona.rutasTransitables)
        Assertions.assertEquals(4, ubicacionesIntermedias.size)
    }

    @Test
    fun test_hayCaminoEntreDosUbicacionesConectadasIndirectamenteConVariosTiposDeCaminos() {
        val calzada = UbicacionNeo4j("Calzada")
        val varela = UbicacionNeo4j("Varela")
        val berazategui = UbicacionNeo4j("Berazategui")
        val wilde = UbicacionNeo4j("Wilde")
        varela.conectar(calzada, "MAR")
        berazategui.conectar(varela, "TIERRA")
        quilmes.conectar(berazategui, "AIRE")
        quilmes.conectar(varela, "MAR")
        bernal.conectar(quilmes, "TIERRA")
        bernal.conectar(wilde, "AIRE")
        ubicacionNeo4jDAO.save(bernal)

        val ubicacionesIntermedias = ubicacionNeo4jDAO
            .caminoMasCorto(bernal.nombre, calzada.nombre, TipoDeVector.Persona.rutasTransitables)
        Assertions.assertEquals(4, ubicacionesIntermedias.size)
    }

    // ================ esUbicacionMuyLejana ================
    @Test
    fun test_seIndicaSiUnaUbicacionEstaMuyLejos() {
        ubicacionNeo4jDAO.save(bernal)
        ubicacionNeo4jDAO.save(quilmes)

        Assertions.assertTrue(ubicacionNeo4jDAO.esUbicacionMuyLejana(bernal.nombre, quilmes.nombre))
    }

    @Test
    fun test_seIndicaSiUnaUbicacionNoEstaMuyLejos() {
        bernal.conectar(quilmes, "TIERRA")
        ubicacionNeo4jDAO.save(bernal)
        ubicacionNeo4jDAO.save(quilmes)

        Assertions.assertFalse(ubicacionNeo4jDAO.esUbicacionMuyLejana(bernal.nombre, quilmes.nombre))
    }

    @Test
    fun test_siUnaUbicacionNoEsLindanteAOtraSeIndicaQueEsUbicacionLejana() {
        val varela = UbicacionNeo4j("Varela")
        quilmes.conectar(bernal, "TIERRA")
        bernal.conectar(varela, "TIERRA")
        ubicacionNeo4jDAO.save(bernal)

        Assertions.assertTrue(ubicacionNeo4jDAO.esUbicacionMuyLejana(quilmes.nombre, varela.nombre))
    }

    @Test
    fun test_siEsUnaUbicacionLindanteAOtraSeIndicaQueNoEsUbicacionLejana() {
        bernal.conectar(quilmes, "TIERRA")
        ubicacionNeo4jDAO.save(bernal)

        Assertions.assertFalse(ubicacionNeo4jDAO.esUbicacionMuyLejana(bernal.nombre, quilmes.nombre))
    }

    // ================ conectados ================
    @Test
    fun test_seDenotanLasUbicacionesConectadas() {
        ubicacionNeo4jDAO.save(bernal)

        val ubicacionesConectadas = ubicacionNeo4jDAO.conectados(bernal.nombre)
        Assertions.assertNotNull(ubicacionesConectadas)
        Assertions.assertEquals(0, ubicacionesConectadas.size)
    }

    @Test
    fun test_seDenotanLasUbicacionesConectadasDireccionalmente() {
        val laPlata = UbicacionNeo4j("La Plata")
        val varela = UbicacionNeo4j("Varela")
        val berazategui = UbicacionNeo4j("Berazategui")
        berazategui.conectar(laPlata, "TIERRA")
        berazategui.conectar(quilmes, "TIERRA")
        varela.conectar(berazategui, "TIERRA")
        ubicacionNeo4jDAO.save(varela)

        val ubicacionesConectadas = ubicacionNeo4jDAO.conectados(berazategui.nombre)
        Assertions.assertEquals(2, ubicacionesConectadas.size)
        Assertions.assertFalse(ubicacionesConectadas.any{ u -> u == varela.nombre })
    }

    @Test
    fun test_seDenotanLasUbicacionesConectadasLindantes() {
        val laPlata = UbicacionNeo4j("La Plata")
        val varela = UbicacionNeo4j("Varela")
        val berazategui = UbicacionNeo4j("Berazategui")
        val calzada = UbicacionNeo4j("Calzada")
        calzada.conectar(varela, "TIERRA")
        varela.conectar(calzada, "TIERRA")
        berazategui.conectar(laPlata, "TIERRA")
        berazategui.conectar(quilmes, "TIERRA")
        varela.conectar(berazategui, "TIERRA")
        varela.conectar(quilmes, "TIERRA")
        quilmes.conectar(bernal, "TIERRA")
        quilmes.conectar(berazategui, "TIERRA")
        ubicacionNeo4jDAO.save(varela)

        val ubicacionesConectadas = ubicacionNeo4jDAO.conectados(berazategui.nombre)
        Assertions.assertEquals(2, ubicacionesConectadas.size)
    }

    @Test
    fun test_seDenotanLasUbicacionesConectadasDeTodoTipoDeCaminoLindantes() {
        val laPlata = UbicacionNeo4j("La Plata")
        val varela = UbicacionNeo4j("Varela")
        val berazategui = UbicacionNeo4j("Berazategui")
        val calzada = UbicacionNeo4j("Calzada")
        calzada.conectar(varela, "MAR")
        varela.conectar(calzada, "TIERRA")
        berazategui.conectar(laPlata, "TIERRA")
        berazategui.conectar(quilmes, "AIRE")
        varela.conectar(berazategui, "MAR")
        varela.conectar(quilmes, "TIERRA")
        quilmes.conectar(bernal, "AIRE")
        quilmes.conectar(berazategui, "TIERRA")
        ubicacionNeo4jDAO.save(varela)

        val ubicacionesConectadas = ubicacionNeo4jDAO.conectados(berazategui.nombre)
        Assertions.assertEquals(2, ubicacionesConectadas.size)
    }

    @AfterEach
    fun clearUp() {
        cleaner.cleanDB()
    }
}