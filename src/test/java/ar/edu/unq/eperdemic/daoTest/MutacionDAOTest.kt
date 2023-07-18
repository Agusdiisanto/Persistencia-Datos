package ar.edu.unq.eperdemic.daoTest

import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.MutacionDAO
import ar.edu.unq.eperdemic.modelo.mutacion.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.mutacion.SupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
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
class MutacionDAOTest {
    private lateinit var utils : EntentyUtils

    @Autowired private lateinit var mutacionDAO : MutacionDAO
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var mutacion : Mutacion
    private lateinit var mutacion2 : Mutacion

    @BeforeEach
    fun crearModelo(){
        utils = EntentyUtils()
        mutacion = SupresionBiomecanica()
        mutacion2 = BioalteracionGenetica()
    }

    @Test
    fun test_sePersisteUnaMutacionYSeLeSeteaUnID(){
        mutacionDAO.save(mutacion)

        Assertions.assertNotNull(mutacion.id)
    }

    @Test
    fun test_sePersisteUnaMutacionYSeRecupera(){
        mutacionDAO.save(mutacion)
        val mutacionSupresion = utils.findByIdOrThrow(mutacionDAO, mutacion.id!!)

        Assertions.assertEquals(mutacionSupresion.id, mutacion.id)
    }

    @Test
    fun test_noSeRecuperaUnaMutacionNoPersistido() {
        mutacionDAO.save(mutacion)
        cleaner.cleanDB()

        Assertions.assertThrows(
            NotFoundException::class.java
        ) { utils.findByIdOrThrow(mutacionDAO,mutacion.id!!) }
    }

    @Test
    fun test_sePersistenVariasMutacionesYSeLasRecupera(){
        mutacionDAO.save(mutacion)
        mutacionDAO.save(mutacion2)

        val mutaciones = mutacionDAO.findAll()
        Assertions.assertEquals(2, mutaciones.count())
    }

    @Test
    fun test_seRecuperanTodasLasMutacionesSinMutacionesPersistidas(){
        val mutaciones =  mutacionDAO.findAll()
        Assertions.assertEquals(0, mutaciones.count())
    }

    @AfterEach
    fun clearUp() {
        cleaner.cleanDB()
    }
}