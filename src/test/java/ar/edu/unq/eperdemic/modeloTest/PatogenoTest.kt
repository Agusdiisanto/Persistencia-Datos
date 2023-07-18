package ar.edu.unq.eperdemic.modeloTest

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exceptions.EmptyNameException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions

class PatogenoTest {


    private lateinit var patogeno : Patogeno
    private lateinit var especie : Especie

    @BeforeEach
    fun modelo(){
        patogeno = Patogeno("Gripe")
        especie = Especie(patogeno,"A","Argentina")
    }

    @Test
    fun test_unPatogenoTieneUNTipo(){
        Assertions.assertEquals("Gripe", patogeno.tipo)
    }

    @Test
    fun test_unPatogenoNoTieneEspecies(){
        Assertions.assertTrue(patogeno.especies.isEmpty())
    }

    @Test
    fun test_unPatogenoCreaUnaEspecie(){
        patogeno.crearEspecie("B","Argentina")

        Assertions.assertFalse(patogeno.especies.isEmpty())
        Assertions.assertEquals(1, patogeno.especies.size)
    }

    @Test
    fun test_elTipoDeUnPatogenoNoPuedeSerVacio(){
        Assertions.assertThrows(EmptyNameException::class.java) { Patogeno("") }
    }


}