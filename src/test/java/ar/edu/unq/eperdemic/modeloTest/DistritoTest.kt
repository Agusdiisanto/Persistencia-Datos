package ar.edu.unq.eperdemic.modeloTest

import ar.edu.unq.eperdemic.modelo.exceptions.EmptyNameException
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon

class DistritoTest {
  private lateinit var quilmes: Distrito
  private lateinit var amba: Distrito

  @BeforeEach
  fun crearModelo() {
    amba = Distrito("AMBA", GeoJsonPolygon(listOf(
      GeoJsonPoint(
        -57.801031650712986,
        -34.967122018431695),
      GeoJsonPoint(
        -58.716728040198376,
        -33.934761836564604),
      GeoJsonPoint(
        -59.48239507773465,
        -34.83559140382412),
      GeoJsonPoint(
        -57.801031650712986,
        -34.967122018431695)
    )))
  }

  @Test
  fun test_unDistritoRequiereNombre() {
    val areaParaDistrito = GeoJsonPolygon(listOf(
      GeoJsonPoint(5.0, 6.0),
      GeoJsonPoint(10.0, 12.0),
      GeoJsonPoint(12.0, 2.0)
    ))
    Assertions.assertThrows(EmptyNameException::class.java) {
      Distrito("", areaParaDistrito)
    }
  }
  @Test
  fun test_unDistritoTieneUnNombre(){
    Assertions.assertEquals(amba.nombre, "AMBA")
  }

  @Test
  fun test_unDistritoTieneUnAreaValida(){
    val pointsArea = amba.area.points

    val primerPunto = pointsArea.first()
    val ultimoPunto = pointsArea.last()

    Assertions.assertEquals(primerPunto, ultimoPunto)
  }

  @Test
  fun test_unDistritoSeLeAgregaUnPuntoAlAreaParaSerValida(){
    val area = GeoJsonPolygon(listOf(
      GeoJsonPoint(5.0, 6.0),
      GeoJsonPoint(10.0, 12.0),
      GeoJsonPoint(20.0, 2.0)
    ))
    val pointsAreaNoValidado = area.points
    val primerPunto = pointsAreaNoValidado.first()
    val ultimoPunto = pointsAreaNoValidado.last()
    Assertions.assertNotEquals(primerPunto, ultimoPunto)

    quilmes = Distrito("Quilmes", area)
    val pointsAreaValidado = quilmes.area.points
    val primerPuntoValido = pointsAreaValidado.first()
    val ultimoPuntoValido = pointsAreaValidado.last()
    Assertions.assertEquals(primerPuntoValido, ultimoPuntoValido)


  }

}