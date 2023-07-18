package ar.edu.unq.eperdemic.modeloTest

import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionFire
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.*


class UbicacionFirebaseTest {
    private lateinit var quilmes: UbicacionFire
    private lateinit var wilde: UbicacionFire
    private lateinit var berazategui: UbicacionFire
    private lateinit var ezpeleta: UbicacionFire

    @BeforeEach
    fun modelo() {
        quilmes = UbicacionFire("Quilmes", 0, 0.0, 0.0)
        wilde = UbicacionFire("Wilde", 2, 0.0, 0.0)
        berazategui = UbicacionFire("Berazategui", 15, 0.0, 0.0)
        ezpeleta = UbicacionFire("Ezpeleta", 6, 0.0, 0.0)
    }

    @Test
    fun test_seIncrementaLosVectoresEnUnaUbicacion(){
        quilmes.incrementarContadorVectores()
        Assertions.assertEquals(1, quilmes.cantidadVectores)
    }

    @Test
    fun test_seDecrementaLosVectoresEnUnaUbicacion(){
        wilde.decrementarContadorVectores()
        Assertions.assertEquals(1, wilde.cantidadVectores)

    }

    @Test
    fun test_hayAlertaVerdeSiLaCantidadDeVectoresEnUnaUbicacionEsMenorA7(){
        Assertions.assertEquals("Verde", quilmes.alerta)
    }

    @Test
    fun test_hayAlertaAmarillaSiHayMasDe7VectoresEnUnaUbicacion(){
        Assertions.assertEquals("Verde", ezpeleta.alerta)
        ezpeleta.incrementarContadorVectores()
        Assertions.assertEquals("Amarillo", ezpeleta.alerta)

    }

    @Test
    fun test_hayAlertaRojaSiHayMasDe15VectoresEnUnaUbicacion(){
        Assertions.assertEquals("Amarillo", berazategui.alerta)
        berazategui.incrementarContadorVectores()
        Assertions.assertEquals("Rojo", berazategui.alerta)

    }

}