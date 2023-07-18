package ar.edu.unq.eperdemic.daoTest

import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.PatogenoDAO
import ar.edu.unq.eperdemic.modelo.Patogeno
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
class PatogenoDAOTest {
    private lateinit var utils : EntentyUtils

    @Autowired private lateinit var patogenoDAO : PatogenoDAO
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var virus: Patogeno
    private lateinit var bacteria: Patogeno

    @BeforeEach
    fun crearModelo(){
        utils = EntentyUtils()
        virus = Patogeno("Virus")
        bacteria = Patogeno("Bacteria")
    }

    @Test
    fun test_sePersisteUnPatogenoYSeLeSeteaID1(){
         patogenoDAO.save(virus)
        Assertions.assertNotNull(virus.id)
    }

    @Test
    fun test_sePersistenDosPatogenosConIDDiferentes(){
        patogenoDAO.save(virus)
        patogenoDAO.save(bacteria)

        Assertions.assertFalse(bacteria.id == virus.id)
    }

   @Test
    fun test_sePersisteUnPatogenoYSeLeRecupera(){
        patogenoDAO.save(virus)
        val virusRecuperado = utils.findByIdOrThrow(patogenoDAO,virus.id!!)

        Assertions.assertEquals(virusRecuperado.id, virus.id)
        Assertions.assertEquals(virusRecuperado.tipo, virus.tipo)
    }

    @Test
    fun test_noSeRecuperaUnPatogenoNoPersistido() {
        patogenoDAO.save(virus)
        cleaner.cleanDB()

        Assertions.assertThrows(
            NotFoundException::class.java
        ) { utils.findByIdOrThrow(patogenoDAO,virus.id!!) }
    }

    @Test
    fun test_sePersistenVariosPatogenosYSeLesRecupera(){
        patogenoDAO.save(virus)
        patogenoDAO.save(bacteria)

        val patogenos = patogenoDAO.findAll()
        Assertions.assertEquals(2, patogenos.count())
    }

    @Test
    fun test_seRecuperanTodosLosPatogenosSinPatogenosPersistidos(){
        val patogenos =  patogenoDAO.findAll()
        Assertions.assertEquals(0, patogenos.count())
    }

    @AfterEach
    fun clearUp() {
        cleaner.cleanDB()
    }
}