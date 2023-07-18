package ar.edu.unq.eperdemic.modeloTest

import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exceptions.EmptyNameException
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import org.junit.jupiter.api.*
import org.mockito.Mockito.*

class UbicacionTest {
    private lateinit var ubicacion: Ubicacion
    private lateinit var ubicacion2: Ubicacion

    private val vector1 = mock(Vector::class.java)
    private val vector2 = mock(Vector::class.java)

    @BeforeEach
    fun modelo() {
        ubicacion = Ubicacion("Tokio")
        ubicacion2 = Ubicacion("Londres")
        ubicacion.vectores.add(vector1)
        ubicacion.vectores.add(vector2)
    }

    @Test
    fun test01_unaUbicacionTieneNombreUnivoco(){
        Assertions.assertEquals("Tokio", ubicacion.nombre)
    }

    @Test
    fun test02_unaUbicacionEligeUnVectorAlAzar(){
        val vector = ubicacion.vectorAlAzar()

        Assertions.assertTrue(ubicacion.vectores.contains(vector))
    }

    @Test
    fun test03_unaUbicacionIntentaElegirUnVectorAlAzarPeroNoHayVectores(){
        Assertions.assertThrows(NotFoundException::class.java, {  ubicacion2.vectorAlAzar() }, "No se encontró un vector en la ubicación dada")
    }

    @Test
    fun test04_unaUbicacionContieneUnaEspecieConElIDDado(){
        `when`(vector1.contieneEspecie(1)).thenReturn(true)

        Assertions.assertTrue(ubicacion.contieneEspecie(1))
    }

    @Test
    fun test05_unaUbicacionNoContieneUnaEspecieConElIDDado(){
        Assertions.assertFalse(ubicacion.contieneEspecie(1))
    }

    @Test
    fun test06_unaUbicacionTieneVectoresInfectados(){
        `when`(vector1.estaInfectado()).thenReturn(true)

        val vectInfectados = ubicacion.vectoresInfectados()

        Assertions.assertEquals(1, vectInfectados.size)
    }

    @Test
    fun test07_unaUbicacionNoTieneNingunVectorInfectado(){
        val vectInfectados = ubicacion.vectoresInfectados()

        Assertions.assertEquals(0, vectInfectados.size)
    }

    @Test
    fun test08_unaUbicacionContieneUnVectorQueSeInstanciaEnElla(){
        val vector = Vector(TipoDeVector.Persona, ubicacion)
        Assertions.assertTrue(ubicacion.vectores.contains(vector))
    }

    @Test
    fun test009_elNombreDeUnaUbicacionNoPuedeSerVacio() {
        Assertions.assertThrows(EmptyNameException::class.java) { Ubicacion("") }
    }
}