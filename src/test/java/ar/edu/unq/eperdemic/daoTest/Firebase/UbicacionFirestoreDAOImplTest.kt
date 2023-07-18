package ar.edu.unq.eperdemic.daoTest.Firebase

import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionFire
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO.UbicacionFirestoreDAO
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import ar.edu.unq.eperdemic.services.impl.Cleaner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.junit.jupiter.api.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionFirestoreDAOImplTest {
    private lateinit var utils: EntentyUtils

    //Modelo
    private lateinit var necochea: UbicacionFire
    private lateinit var wilde: UbicacionFire
    private lateinit var ezpeleta: UbicacionFire

    //Dao
    @Autowired
    private lateinit var FirebaseDAO: UbicacionFirestoreDAO
    @Autowired
    private lateinit var cleaner: Cleaner

    @BeforeEach
    fun crearModelo(){
        utils = EntentyUtils()
        necochea = UbicacionFire("Necochea", 0, 0.0, 0.0)
        wilde = UbicacionFire("Wilde", 0, 0.0, 0.0)
        ezpeleta = UbicacionFire("Ezpeleta", 0, 0.0, 0.0)
    }

    // ============== tests de save ==============
    @Test
    fun test_alPersistirUnaUbicacionSeGeneraUnID(){
        val ubicacionPersistida = FirebaseDAO.save(necochea)
        Assertions.assertNotNull(ubicacionPersistida.id)
        Assertions.assertEquals(necochea, ubicacionPersistida)
    }

    // ============== tests de update ==============
    @Test
    fun test_seActualizaUnaUbicacion(){
        FirebaseDAO.save(necochea)
        Assertions.assertEquals(0, necochea.cantidadVectores)
        necochea.incrementarContadorVectores()
        val ubicacionActualizada = FirebaseDAO.update(necochea.id!!, necochea)
        Assertions.assertEquals(1, ubicacionActualizada.cantidadVectores)

    }

    // ============== tests de saveOrUpdate ==============
    @Test
    fun test_seGuardaUnaUbicacionSiCorresponde(){
        val ubicacionPersistida = FirebaseDAO.saveOrUpdate(necochea)
        Assertions.assertNotNull(ubicacionPersistida.id)
        Assertions.assertEquals(necochea, ubicacionPersistida)

    }

    @Test
    fun test_seActualizaUnaUbicacionSiCorresponde(){
        FirebaseDAO.saveOrUpdate(necochea) //Primer saveOrUpdate guarda la ubicacion Necochea
        Assertions.assertEquals(0, necochea.cantidadVectores)
        necochea.incrementarContadorVectores()
        val ubicacionActualizada = FirebaseDAO.saveOrUpdate(necochea) //Segundo saveOrUpdate actualiza la ubicacion Necochea
        Assertions.assertEquals(1, ubicacionActualizada.cantidadVectores)

    }

    // ============== tests de findByName ==============

    @Test
    fun test_seRecuperaUnaUbicacionConSuNombre(){
        FirebaseDAO.save(wilde)
        val ubicacionRecuperada = FirebaseDAO.findByName(wilde.nombre)
        Assertions.assertEquals(wilde, ubicacionRecuperada)
    }

    @Test
    fun test_noSeRecuperaUnaUbicacionInexistenteConSuNombre(){

        Assertions.assertThrows(NotFoundException::class.java) {
            FirebaseDAO.findByName("Quilmes")
            throw NotFoundException("No se encontró la ubicación con nombre: Quilmes")
        }
    }

    // ============== tests de findAll ==============
    @Test
    fun test_seRecuperanTodasLasUbicacionesPersistidas(){
        FirebaseDAO.save(necochea)
        FirebaseDAO.save(wilde)
        FirebaseDAO.save(ezpeleta)

        val ubicaciones = FirebaseDAO.findAll()
        Assertions.assertEquals(3, ubicaciones.size)
        Assertions.assertTrue(ubicaciones.contains(wilde))

    }

    // ============== tests de findById ==============
    @Test
    fun test_seRecuperaUnaUbicacionConSuID(){
        FirebaseDAO.save(necochea)
        val ubicacionRecuperada = FirebaseDAO.findById(necochea.id!!)
        Assertions.assertEquals(ubicacionRecuperada, necochea)

    }

    // ============== tests de deleteById ==============
    @Test
    fun test_seEliminaUnaUbicacionConSuId(){
        FirebaseDAO.save(ezpeleta)
        FirebaseDAO.deleteById(ezpeleta.id!!)

        Assertions.assertThrows(NotFoundException::class.java) {
            FirebaseDAO.findByName("Ezpeleta")
            throw NotFoundException("No se encontró la ubicación con nombre: Quilmes")
        }
    }

    // ============== tests de deleteAll ==============
    @Test
    fun test_seEliminanTodasLasUbicaciones(){
        FirebaseDAO.save(wilde)
        FirebaseDAO.save(ezpeleta)
        FirebaseDAO.save(necochea)
        val ubicacionesRecuperadas = FirebaseDAO.findAll()

        Assertions.assertEquals(3, ubicacionesRecuperadas.size)
        FirebaseDAO.deleteAll()
        val ubicaciones = FirebaseDAO.findAll()
        Assertions.assertEquals(0, ubicaciones.size)


    }

    // ============== tests de nombresDeUbicacionesConAlerta ==============
    @Test
    fun test_seRecuperanLosNombresDeUbicacionesConAlertaVerde(){
        FirebaseDAO.save(wilde)
        FirebaseDAO.save(ezpeleta)
        FirebaseDAO.save(necochea)
        val ubicacionesRecuperadas = FirebaseDAO.nombresDeUbicacionesConAlerta("Verde")
        Assertions.assertTrue(ubicacionesRecuperadas.contains(wilde.nombre))
    }

    @Test
    fun test_seRecuperanLosNombresDeUbicacionesConAlertaAmarilla(){
        FirebaseDAO.save(wilde)
        FirebaseDAO.save(ezpeleta)
        val bosques = UbicacionFire("bosques", 10, 0.0, 0.0)
        FirebaseDAO.save(bosques)
        val ubicacionesRecuperadas = FirebaseDAO.nombresDeUbicacionesConAlerta("Amarillo")
        Assertions.assertTrue(ubicacionesRecuperadas.contains(bosques.nombre))
        Assertions.assertFalse(ubicacionesRecuperadas.contains(wilde.nombre))


    }

    @Test
    fun test_seRecuperanLosNombresDeUbicacionesConAlertaRoja(){
        FirebaseDAO.save(wilde)
        FirebaseDAO.save(ezpeleta)
        val bosques = UbicacionFire("bosques", 20, 0.0, 0.0)
        FirebaseDAO.save(bosques)
        val ubicacionesRecuperadas = FirebaseDAO.nombresDeUbicacionesConAlerta("Rojo")
        Assertions.assertTrue(ubicacionesRecuperadas.contains(bosques.nombre))
        Assertions.assertFalse(ubicacionesRecuperadas.contains(wilde.nombre))

    }

    @AfterEach
    fun clearUp() {
        cleaner.cleanDB()
    }

}