package ar.edu.unq.eperdemic.modeloTest

import ar.edu.unq.eperdemic.modelo.exceptions.InvalidPathTypeException
import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionNeo4j
import org.junit.jupiter.api.*

class UbicacionNeo4JTest {
  private lateinit var ubicacion: UbicacionNeo4j
  private lateinit var ubicacion2: UbicacionNeo4j

  @BeforeEach
  fun modelo() {
    ubicacion = UbicacionNeo4j("Tokio")
    ubicacion2 = UbicacionNeo4j("Londres")
  }

  @Test
  fun test_UbicacionesSeConectanPorRutaMartitima(){
    ubicacion.conectar(ubicacion2,"MAR")
    Assertions.assertTrue(ubicacion.conexionesDeRutasMartimas.contains(ubicacion2))
    Assertions.assertFalse(ubicacion2.conexionesDeRutasMartimas.contains(ubicacion))
  }

  @Test
  fun test_UbicacionesSeConectanPorRutaAerea(){
    ubicacion.conectar(ubicacion2,"AIRE")
    Assertions.assertTrue(ubicacion.conexionesDeRutasAereas.contains(ubicacion2))
    Assertions.assertFalse(ubicacion2.conexionesDeRutasAereas.contains(ubicacion))
  }

  @Test
  fun test_UbicacionesSeConectanPorRutaTerrestre(){
    ubicacion.conectar(ubicacion2,"TIERRA")
    Assertions.assertTrue(ubicacion.conexionesDeRutasTerrestres.contains(ubicacion2))
    Assertions.assertFalse(ubicacion2.conexionesDeRutasTerrestres.contains(ubicacion))
  }

  @Test
  fun test_UbicacionesNoSeConectanPorRutaIncorrecta(){
    Assertions.assertThrows(
      InvalidPathTypeException::class.java,
      { ubicacion.conectar(ubicacion2,"Cemento") },
      "El camino Cemento es invalido. Los tipos validos son 'AIRE', 'MAR' y 'TIERRA'."
    )
  }
}