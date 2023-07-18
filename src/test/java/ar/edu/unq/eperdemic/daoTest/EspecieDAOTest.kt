package ar.edu.unq.eperdemic.daoTest

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.UbicacionJPADAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.VectorDAO
import ar.edu.unq.eperdemic.services.impl.Cleaner
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EspecieDAOTest {
    private lateinit var utils : EntentyUtils

    @Autowired private lateinit var especieDao : EspecieDAO
    @Autowired private lateinit var vectorDao : VectorDAO
    @Autowired private lateinit var patogenoDao: PatogenoDAO
    @Autowired private lateinit var ubicacionDao: UbicacionJPADAO
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var patogeno : Patogeno
    private lateinit var especie : Especie
    private lateinit var especie2 : Especie
    private lateinit var vector : Vector
    private lateinit var vector2 : Vector
    private lateinit var vector3 : Vector
    private lateinit var vector4 : Vector
    private lateinit var ubicacion : Ubicacion
    private lateinit var ubicacion2 : Ubicacion

    @BeforeEach
    fun crearModelo(){
        utils = EntentyUtils()
        //Modelo
        patogeno = Patogeno("Covid")
        especie = Especie(patogeno,"HongoA", "Argentina")
        especie2 = Especie(patogeno,"HongoB", "Brasil")
        ubicacion = Ubicacion("China")
        ubicacion2 = Ubicacion("Australia")
        vector = Vector(TipoDeVector.Persona,ubicacion)
        vector2 = Vector(TipoDeVector.Persona,ubicacion)
        vector3 = Vector(TipoDeVector.Persona,ubicacion)
        vector4 = Vector(TipoDeVector.Animal,ubicacion)

        patogenoDao.save(patogeno)
        especieDao.save(especie)
        especieDao.save(especie2)
        ubicacionDao.save(ubicacion)
    }

    @Test
    fun test_sePersisteUnaEspecieYSeLeSeteaUnId(){
        Assertions.assertNotNull(especie.id)
    }

    @Test
    fun test_sePersisteUnaEspecieYSeRecupera(){
        val especieRecuperada = utils.findByIdOrThrow(especieDao,especie.id!!)

        Assertions.assertEquals(especieRecuperada.id, especie.id)
        Assertions.assertEquals(especieRecuperada.nombre, especie.nombre)
    }

    @Test
    fun test_especieDaoRecuperaATodasLasEspecies(){
        val especies =  especieDao.findAll()

        Assertions.assertEquals(2, especies.count())
    }

    @Test
    fun test_seConoceLaCantidadDeInfectadosDeUnaEspecie(){
        especie.infectaA(vector)
        vectorDao.save(vector)

        val cantInfectados =  especieDao.cantidadDeInfectados(especie.id!!)

        Assertions.assertEquals(1, cantInfectados)
    }

    @Test
    fun test_seRecuperaLaEspecieLider(){
        especie.infectaA(vector)
        vectorDao.save(vector)
        especie.infectaA(vector2)
        vectorDao.save(vector2)

        val especieLider =  especieDao.especieLider()

        Assertions.assertEquals(especie.id, especieLider!!.id)
    }

    @Test
    fun test_seRecuperanLasEspeciesLideres(){
        especie.infectaA(vector)
        vectorDao.save(vector)
        especie.infectaA(vector2)
        vectorDao.save(vector2)
        especie.infectaA(vector3)
        vectorDao.save(vector3)
        especie2.infectaA(vector4)
        vectorDao.save(vector4)

        val especiesLideres =  especieDao.especiesLideres()
        val especieConMaxContagios = especiesLideres[0]

        Assertions.assertEquals(especie.id, especieConMaxContagios.id)
    }

    @Test
    fun test_seRecuperaEspecieLiderEnLaUbicacionDada(){
        especie.infectaA(vector)
        vectorDao.save(vector)
        especie.infectaA(vector2)
        vectorDao.save(vector2)
        especie.infectaA(vector3)
        vectorDao.save(vector3)
        especie2.infectaA(vector4)
        vectorDao.save(vector4)

        val especieLiderEnUbicacion =  especieDao.especieLiderEn(ubicacion.nombre)
        Assertions.assertNotNull(especieLiderEnUbicacion)
    }

    @Test
    fun test_noHayEspecieLiderEnLaUbicacionDada(){
        val especieLiderEnUbicacion = especieDao.especieLiderEn(ubicacion2.nombre)
        Assertions.assertNull(especieLiderEnUbicacion)
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}