package ar.edu.unq.eperdemic.daoTest

import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.UbicacionJPADAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.VectorDAO
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.services.impl.Cleaner
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VectorDAOTest {

    private lateinit var utils : EntentyUtils

    @Autowired private lateinit var vectorDAO : VectorDAO
    @Autowired private lateinit var ubicacionDAO: UbicacionJPADAO
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var mosquito: Vector
    private lateinit var murcielago: Vector
    private lateinit var bernal: Ubicacion

    @BeforeEach
    fun crearModelo(){
        utils = EntentyUtils()
        bernal = Ubicacion("Bernal")
        mosquito = Vector(TipoDeVector.Insecto, bernal)
        murcielago = Vector(TipoDeVector.Animal, bernal)

    }
    @Test
    fun test_sePersisteUnVectorYSeLeSeteaID1(){
        ubicacionDAO.save(bernal)
        vectorDAO.save(mosquito)

        Assertions.assertNotNull(mosquito.id)
    }

    @Test
    fun test_sePersistenDosVectoresConIDDiferentes(){
        ubicacionDAO.save(bernal)
        vectorDAO.save(mosquito)
        vectorDAO.save(murcielago)

        Assertions.assertFalse(mosquito.id == murcielago.id)
    }

    @Test
    fun test_sePersisteUnVectorYSeLeRecupera(){
        ubicacionDAO.save(bernal)
        vectorDAO.save(mosquito)

        val vectorRecuperado = utils.findByIdOrThrow(vectorDAO,mosquito.id!!)

        Assertions.assertEquals(vectorRecuperado.id, mosquito.id)
        Assertions.assertEquals(vectorRecuperado.tipo, mosquito.tipo)
    }

    @Test
    fun test_noSeRecuperaUnVectorNoPersistido(){
        ubicacionDAO.save(bernal)
        vectorDAO.save(murcielago)
        cleaner.cleanDB()

        Assertions.assertThrows(
            NotFoundException::class.java
        ) { utils.findByIdOrThrow(vectorDAO,murcielago.id!!) }
    }

    @Test
    fun test_sePersistenVariosVectoresYSeLesRecupera(){
        ubicacionDAO.save(bernal)
        vectorDAO.save(mosquito)
        vectorDAO.save(murcielago)

        val vectores =  vectorDAO.findAll()
        Assertions.assertEquals(2, vectores.count())
    }

    @Test
    fun test_seRecuperanTodosLosVectoresSinVectoresPersistidos(){
        val vectores =  vectorDAO.findAll()
        Assertions.assertEquals(0, vectores.count())
    }

    @AfterEach
    fun clearUp() {
        cleaner.cleanDB()
    }
}