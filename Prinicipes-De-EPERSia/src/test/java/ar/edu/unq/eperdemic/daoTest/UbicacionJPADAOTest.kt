package ar.edu.unq.eperdemic.daoTest

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.UbicacionJPADAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.VectorDAO
import ar.edu.unq.eperdemic.services.impl.Cleaner
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionJPADAOTest {
    private lateinit var utils : EntentyUtils

    @Autowired private lateinit var ubicacionDAO: UbicacionJPADAO
    @Autowired private lateinit var vectorDAO: VectorDAO
    @Autowired private lateinit var especieDAO: EspecieDAO
    @Autowired private lateinit var patogenoDAO: PatogenoDAO
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var bernal: Ubicacion
    private lateinit var quilmes: Ubicacion
    private lateinit var mosquito: Vector
    private lateinit var virus: Patogeno
    private lateinit var dengue: Especie

    @BeforeEach
    fun crearModelo() {
        utils = EntentyUtils()
        bernal = Ubicacion("Bernal")
        quilmes = Ubicacion("Quilmes")
        mosquito = Vector(TipoDeVector.Insecto, bernal)
        virus = Patogeno("Virus")
        dengue = Especie(virus, "Dengue", "Paraguay")
    }

    @Test
    fun test_sePersisteUnaUbicacionYSeLeAsignaUnID() {
        runTrx { ubicacionDAO.save(bernal) }
        Assertions.assertNotNull(bernal.id)
    }

    @Test
    fun test_lasUbicacionesSePersistenDiferentesID() {
        runTrx {
            ubicacionDAO.save(bernal)
            ubicacionDAO.save(quilmes)
        }
        Assertions.assertFalse(quilmes.id == bernal.id)
    }

    @Test
    fun test_noEsPosibleGuardarDosUbicacionesConIgualNombre() {
        quilmes.nombre = "Bernal"
        ubicacionDAO.save(quilmes)
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            ubicacionDAO.save(bernal)
        }
    }

    @Test
    fun test_sePersisteUnaUbicacionYSeLeRecupera() {
        ubicacionDAO.save(bernal)
        val bernalRecuperada = utils.findByIdOrThrow(ubicacionDAO,bernal.id!!)
        Assertions.assertEquals(bernalRecuperada.id, bernal.id)
        Assertions.assertEquals(bernalRecuperada.nombre, bernal.nombre)
    }

    @Test
    fun test_noSeRecuperaUnaUbicacionNoPersistido() {
        ubicacionDAO.save(bernal)
        cleaner.cleanDB()

        Assertions.assertThrows(NotFoundException::class.java) {
            utils.findByIdOrThrow(ubicacionDAO, bernal.id!!)
        }
    }

    @Test
    fun test_sePersistenVariasUbicacionesYSeLesRecupera() {
        ubicacionDAO.save(bernal)
        ubicacionDAO.save(quilmes)

        val ubicaciones = ubicacionDAO.findAll()
        Assertions.assertEquals(2, ubicaciones.count())
    }

    @Test
    fun test_seRecuperanTodasLasUbicacionesSinUbicacionesPersistidas() {
        val ubicaciones = ubicacionDAO.findAll()
        Assertions.assertEquals(0, ubicaciones.count())
    }

    @Test
    fun test_seObtieneLaCantidadDeVectoresDeUnaUbicacion() {
        ubicacionDAO.save(bernal)
        vectorDAO.save(mosquito)

        val cantVectores = ubicacionDAO.cantidadDeVectoresEn(bernal.nombre)
        Assertions.assertEquals(1, cantVectores)
    }

    @Test
    fun test_seObtieneLaCantidadDeVectoresInfectadosDeUnaUbicacion() {
        ubicacionDAO.save(bernal)
        patogenoDAO.save(virus)
        vectorDAO.save(mosquito)
        especieDAO.save(dengue)
        dengue.infectaA(mosquito)
        ubicacionDAO.save(bernal)

        val cantVectores =  ubicacionDAO.cantidadDeInfectados(bernal.nombre)
        Assertions.assertEquals(1, cantVectores)
    }

    @Test
    fun test_seObtieneLaCantidadDeEspeciesDeUnaUbicacion() {
        ubicacionDAO.save(bernal)
        patogenoDAO.save(virus)
        vectorDAO.save(mosquito)
        especieDAO.save(dengue)
        mosquito.infectadoPor(dengue)
        ubicacionDAO.save(bernal)

        val cantUbicacionesInfectadas =  ubicacionDAO.cantidadDeUbicacionesConEspecie(dengue.id!!)
        Assertions.assertEquals(1, cantUbicacionesInfectadas)
    }

    @Test
    fun test_seObtieneLaCantidadDeUbicaciones() {
        ubicacionDAO.save(bernal)
        ubicacionDAO.save(quilmes)

        val cantUbicaciones = ubicacionDAO.cantidadDeUbicaciones()
        Assertions.assertEquals(2, cantUbicaciones)
    }

    @Test
    fun test_seRecuperanUbicacionesSegunNombres() {
        ubicacionDAO.save(bernal)
        ubicacionDAO.save(quilmes)

        val ubicaciones = listOf(bernal.nombre, quilmes.nombre)
        val ubicacionesRecuperadas = ubicacionDAO.findAllByNombreIn(ubicaciones)
        Assertions.assertEquals(2, ubicacionesRecuperadas.size)
    }

    // ======================== hayInfectados ========================
    @Test
    fun test_seSabeSiHayInfectados() {
        ubicacionDAO.save(bernal)
        patogenoDAO.save(virus)
        vectorDAO.save(mosquito)
        especieDAO.save(dengue)
        dengue.infectaA(mosquito)
        ubicacionDAO.save(bernal)
        Assertions.assertTrue(ubicacionDAO.hayInfectados(bernal.nombre))
    }

    @AfterEach
    fun clearUp() {
        cleaner.cleanDB()
    }
}
