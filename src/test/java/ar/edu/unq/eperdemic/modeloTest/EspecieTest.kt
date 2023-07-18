package ar.edu.unq.eperdemic.modeloTest

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.TipoDeVector.Persona
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exceptions.EmptyCountryException
import ar.edu.unq.eperdemic.modelo.exceptions.EmptyNameException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.*

class EspecieTest {
    private lateinit var patogeno: Patogeno
    private lateinit var especie: Especie
    private lateinit var vector: Vector
    private lateinit var ubicacion: Ubicacion

    @BeforeEach
    fun modelo(){
        patogeno = Patogeno("Covid")
        especie = Especie(patogeno,"19","China")
        ubicacion = Ubicacion("Palermo")
        vector = Vector(Persona,ubicacion)
    }
    @Test
    fun test01_unaEspecieTieneNombre(){
        Assertions.assertEquals("19",especie.nombre)
    }

    @Test
    fun test02_unaEspecieTieneUnPatogeno(){
        Assertions.assertEquals(patogeno,especie.patogeno)
    }

    @Test
    fun test03_unaEspecieTienePaisDeOrigen(){
        Assertions.assertEquals("China",especie.paisDeOrigen)
    }

    @Test
    fun test04_UnaEspecieInfectaAUnVector(){
        especie.infectaA(vector)
        Assertions.assertTrue(especie.vectores.contains(vector))
    }

    @Test
    fun test05_UnaEspecieNoSePuedeInstanciarSinNombre(){
        Assertions.assertThrows(
            EmptyNameException::class.java,
            { Especie(patogeno,"","China") },
            "La especie debe tener un nombre"
        )
    }

    @Test
    fun test05_UnaEspecieNoSePuedeInstanciarSinPaisDeOrigen(){
        Assertions.assertThrows(
            EmptyCountryException::class.java,
            { Especie(patogeno,"Covid","") },
            "La especie debe tener un pais de origen"
        )
    }
}