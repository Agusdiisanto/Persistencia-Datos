package ar.edu.unq.eperdemic.servicesTest.Neo4J

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exceptions.InvalidPathTypeException
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoTestContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exceptions.UbicacionMuyLejana
import ar.edu.unq.eperdemic.services.exceptions.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.services.impl.Cleaner
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionServiceNeo4jTest {
    private lateinit var serviceTesting: RandomContagio
    @Autowired private lateinit var service: UbicacionService
    @Autowired private lateinit var patogenoService: PatogenoService
    @Autowired private lateinit var vectorService: VectorService
    @Autowired private lateinit var distritoService: DistritoService
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var berazategui: Ubicacion
    private lateinit var bernal: Ubicacion
    private lateinit var quilmes: Ubicacion
    private lateinit var varela: Ubicacion
    private lateinit var wilde: Ubicacion
    private lateinit var constitucion: Ubicacion
    private lateinit var retiro: Ubicacion
    private lateinit var mosca: Vector
    private lateinit var mosquito: Vector
    private lateinit var vectorHumano: Vector
    private lateinit var vectorAnimal: Vector
    private lateinit var patogeno: Patogeno
    private lateinit var especie: Especie
    private lateinit var coordenada: Coordenada
    private lateinit var distrito : Distrito

    @BeforeEach
    fun crearModelo() {
        this.serviceTesting = RandomContagio
        serviceTesting.setModo(ModoTestContagio(100, 100, 100, 10, 0))
        distrito = Distrito("Amba", GeoJsonPolygon(listOf(GeoJsonPoint(24.0, 12.2), GeoJsonPoint(10.0,8.0), GeoJsonPoint(4.0,6.0))))
        distritoService.crear(distrito)

        //Modelo
        coordenada = Coordenada(12.2, 24.0)

        bernal = service.crearUbicacion("Bernal",coordenada)
        berazategui = service.crearUbicacion("Berazategui",coordenada)
        quilmes = service.crearUbicacion("Quilmes",coordenada)
        varela = service.crearUbicacion("Varela",coordenada)
        wilde = service.crearUbicacion("Wilde",coordenada)
        constitucion = service.crearUbicacion("Constitucion",coordenada)
        retiro = service.crearUbicacion("Retiro",coordenada)

        mosca = vectorService.crearVector(TipoDeVector.Insecto, berazategui.id!!)
        vectorHumano = vectorService.crearVector(TipoDeVector.Persona, berazategui.id!!)
        vectorAnimal = vectorService.crearVector(TipoDeVector.Animal, berazategui.id!!)
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, bernal.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", berazategui.id!!)
        vectorService.crearVector(TipoDeVector.Persona, wilde.id!!)
        vectorService.crearVector(TipoDeVector.Persona, quilmes.id!!)
        vectorService.crearVector(TipoDeVector.Persona, quilmes.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-C", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-D", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-E", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-F", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-G", berazategui.id!!)
    }

    @Test
    fun test_sePersisteUnaRelacion(){
        service.conectar(quilmes.nombre, berazategui.nombre, "TIERRA")

        Assertions.assertTrue(
            service.conectados(quilmes.nombre).all{ u-> u.nombre == berazategui.nombre}
        )
    }

    @Test
    fun test_seConocenLasUbicacionesConectadas(){
        service.conectar(quilmes.nombre, varela.nombre, "TIERRA")
        service.conectar(quilmes.nombre, berazategui.nombre, "mar")
        service.conectar(quilmes.nombre, bernal.nombre, "AIRE")

        val conectados = service.conectados(quilmes.nombre)
        Assertions.assertEquals(3, conectados.size)
        Assertions.assertTrue(conectados.any{ u -> u.nombre == varela.nombre })
    }

    @Test
    fun test_lasUbicacionesConectadasSoloIncluyenLasUbicacionesLindantes(){
        val calzada = service.crearUbicacion("Calzada",coordenada)
        service.conectar(quilmes.nombre, varela.nombre, "TIERRA")
        service.conectar(quilmes.nombre, berazategui.nombre, "mar")
        service.conectar(quilmes.nombre, bernal.nombre, "AIRE")
        service.conectar(varela.nombre, calzada.nombre, "Tierra")

        val conectados = service.conectados(quilmes.nombre)
        Assertions.assertEquals(3, conectados.size)
        Assertions.assertTrue(conectados.any{ u -> u.nombre == varela.nombre })
    }

    @Test
    fun test_lasUbicacionesConectadasContemplanSoloLaDireccionCorrecta(){
        val calzada = service.crearUbicacion("Calzada",coordenada)
        service.conectar(quilmes.nombre, varela.nombre, "TIERRA")
        service.conectar(varela.nombre, calzada.nombre, "Tierra")

        val conectados = service.conectados(varela.nombre)
        Assertions.assertEquals(1, conectados.size)
        Assertions.assertTrue(conectados.any{ u -> u.nombre == calzada.nombre })
    }

    @Test
    fun test_soloSePuedeConectarPorUnTipoDeCaminoValido(){
        val tipoDeCamino = "acuatico"

        val exception = Assertions.assertThrows(InvalidPathTypeException::class.java) {
            service.conectar(quilmes.nombre, varela.nombre, tipoDeCamino)
        }
        Assertions.assertEquals(
            "El camino '${tipoDeCamino}' es invalido. Los tipos validos son 'AIRE', 'MAR' y 'TIERRA'.",
            exception.message
        )
    }

    // ============== tests de mover ==============
    @Test
    fun test_unVectorPuedeMoverseAUbicacionesLindantes(){
        service.conectar(berazategui.nombre, quilmes.nombre, "Tierra")

        service.mover(mosca.id!!, quilmes.nombre)
        mosca = vectorService.recuperarVector(mosca.id!!)

        Assertions.assertEquals(quilmes.nombre, mosca.ubicacion.nombre)
    }

    @Test
    fun test_unVectorNoPuedeMoverseAUbicacionesQueNoSonLindantes(){
        service.conectar(berazategui.nombre, constitucion.nombre, "Tierra")
        service.conectar(constitucion.nombre, retiro.nombre, "Tierra")

        val exception = Assertions.assertThrows(UbicacionMuyLejana::class.java) {
            service.mover(mosca.id!!, retiro.nombre)
        }
        Assertions.assertEquals(
            "La ubicacion '${retiro.nombre}' es muy lejana a '${mosca.ubicacion.nombre}'.",
            exception.message
        )
    }

    @Test
    fun test_unVectorAlMoverseSeExpandeInfectandoALosVectoresEnDichaUbicacion(){
        vectorService.infectar(mosca.id!!,especie.id!!)
        service.conectar(berazategui.nombre, quilmes.nombre, "Tierra")

        val vectoresSinInfectar = vectorService.recuperarTodos().filter{v -> v.ubicacion.nombre == "Quilmes"}
        Assertions.assertFalse(vectoresSinInfectar.any{v -> v.estaInfectado()})

        service.mover(mosca.id!!, quilmes.nombre)
        mosca = vectorService.recuperarVector(mosca.id!!)
        val vectoresQuilmes = vectorService.recuperarTodos().filter{v -> v.ubicacion.nombre == "Quilmes"}

        Assertions.assertEquals(quilmes.nombre, mosca.ubicacion.nombre)
        Assertions.assertTrue(vectoresQuilmes.any{v -> v.estaInfectado()})
    }

    @Test
    fun test_siNoHayRutaPosibleElVectorNoSeMueveDeSuUbicacion(){
        service.conectar(berazategui.nombre, constitucion.nombre, "TIERRA")

        val ubicacionActual = mosca.ubicacion
        val exception = Assertions.assertThrows(UbicacionMuyLejana::class.java) {
            service.mover(mosca.id!!, retiro.nombre)
        }
        Assertions.assertEquals(
            "La ubicacion '${retiro.nombre}' es muy lejana a '${ubicacionActual.nombre}'.",
            exception.message
        )
        Assertions.assertEquals(ubicacionActual, mosca.ubicacion)
    }

    @Test
    fun test_siNoHayCaminoSeObtieneUnaUbicacionMuyLejana(){
        val exception = Assertions.assertThrows(UbicacionMuyLejana::class.java) {
            service.mover(mosca.id!!, constitucion.nombre)
        }
        Assertions.assertEquals(
            "La ubicacion '${constitucion.nombre}' es muy lejana a '${mosca.ubicacion.nombre}'.",
            exception.message
        )
    }

    @Test
    fun test_siNoHayUnaRutaPosibleSeObtieneUnaUbicacionNoAlcanzable(){
        service.conectar(berazategui.nombre, constitucion.nombre, "Mar")

        val exception = Assertions.assertThrows(UbicacionNoAlcanzable::class.java) {
            service.mover(mosca.id!!, constitucion.nombre)
        }
        Assertions.assertEquals(
            "No hay caminos disponibles para el tipo de vector.",
            exception.message
        )
    }

    // ============== tests de vectorHumano ==============
    @Test
    fun test_unVectorHumanoPuedeAtravesarCaminosTerrestres(){
        service.conectar(berazategui.nombre, quilmes.nombre, "Tierra")

        service.mover(vectorHumano.id!!, quilmes.nombre)
        vectorHumano = vectorService.recuperarVector(vectorHumano.id!!)

        Assertions.assertEquals(quilmes.nombre, vectorHumano.ubicacion.nombre)
    }

    @Test
    fun test_unVectorHumanoPuedeAtravesarCaminosMaritimos(){
        service.conectar(berazategui.nombre, quilmes.nombre, "Mar")

        service.mover(vectorHumano.id!!, quilmes.nombre)
        vectorHumano = vectorService.recuperarVector(vectorHumano.id!!)

        Assertions.assertEquals(quilmes.nombre, vectorHumano.ubicacion.nombre)
    }

    @Test
    fun test_unVectorHumanoNoPuedeAtravesarCaminosAereos(){
        service.conectar(berazategui.nombre, bernal.nombre, "aire")

        val exception = Assertions.assertThrows(UbicacionNoAlcanzable::class.java) {
            service.mover(vectorHumano.id!!, bernal.nombre)
        }
        Assertions.assertEquals(
            "No hay caminos disponibles para el tipo de vector.",
            exception.message
        )
        Assertions.assertEquals(berazategui.nombre, vectorHumano.ubicacion.nombre)
    }

    // ============== tests de vectorAnimal ==============
    @Test
    fun test_unVectorAnimalPuedeAtravesarCaminosTerrestres(){
        service.conectar(berazategui.nombre, quilmes.nombre, "Tierra")

        service.mover(vectorAnimal.id!!, quilmes.nombre)
        vectorAnimal = vectorService.recuperarVector(vectorAnimal.id!!)

        Assertions.assertEquals(quilmes.nombre, vectorAnimal.ubicacion.nombre)
    }

    @Test
    fun test_unVectorAnimalPuedeAtravesarCaminosAereos(){
        service.conectar(berazategui.nombre, quilmes.nombre, "Aire")

        service.mover(vectorAnimal.id!!, quilmes.nombre)
        vectorAnimal = vectorService.recuperarVector(vectorAnimal.id!!)

        Assertions.assertEquals(quilmes.nombre, vectorAnimal.ubicacion.nombre)
    }

    @Test
    fun test_unVectorAnimalPuedeAtravesarCaminosMaritimos(){
        service.conectar(berazategui.nombre, quilmes.nombre, "Mar")

        service.mover(vectorAnimal.id!!, quilmes.nombre)
        vectorAnimal = vectorService.recuperarVector(vectorAnimal.id!!)

        Assertions.assertEquals(quilmes.nombre, vectorAnimal.ubicacion.nombre)
    }

    // ============== tests de vectorInsecto ==============
    @Test
    fun test_unVectorInsectoPuedeAtravesarCaminosTerrestres(){
        service.conectar(berazategui.nombre, quilmes.nombre, "Tierra")

        service.mover(mosca.id!!, quilmes.nombre)
        mosca = vectorService.recuperarVector(mosca.id!!)

        Assertions.assertEquals(quilmes.nombre, mosca.ubicacion.nombre)
    }

    @Test
    fun test_unVectorInsectoPuedeAtravesarCaminosAereos(){
        service.conectar(berazategui.nombre, quilmes.nombre, "Aire")

        service.mover(mosca.id!!, quilmes.nombre)
        mosca = vectorService.recuperarVector(mosca.id!!)

        Assertions.assertEquals(quilmes.nombre, mosca.ubicacion.nombre)
    }

    @Test
    fun test_unVectorInsectoNoPuedeAtravesarCaminosMaritimos(){
        service.conectar(berazategui.nombre, bernal.nombre, "Mar")

        val exception = Assertions.assertThrows(UbicacionNoAlcanzable::class.java) {
            service.mover(mosca.id!!, bernal.nombre)
        }
        Assertions.assertEquals(
            "No hay caminos disponibles para el tipo de vector.",
            exception.message
        )
        Assertions.assertEquals(berazategui.nombre, mosca.ubicacion.nombre)
    }

    // ============== tests de moverMasCorto ==============
    @Test
    fun test_sePuedeMoverMasCortoAUnaUbicacionConectada(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, berazategui.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", berazategui.id!!)
        service.conectar(berazategui.nombre, quilmes.nombre, "TIERRA")

        Assertions.assertDoesNotThrow{service.moverMasCorto(mosca.id!!, quilmes.nombre)}
    }

    @Test
    fun test_unVectorSeMuevePorElCaminoMasCorto(){
        service.conectar(berazategui.nombre, constitucion.nombre, "TIERRA")
        service.conectar(berazategui.nombre, quilmes.nombre, "TIERRA")
        service.conectar(quilmes.nombre, constitucion.nombre, "TIERRA")

        service.moverMasCorto(mosca.id!!, "Constitucion")
        val vectoresQuilmes = vectorService.recuperarTodos().filter{ v -> v.ubicacion.nombre == "Quilmes"}
        mosca = vectorService.recuperarVector(mosca.id!!)

        Assertions.assertEquals(constitucion.nombre, mosca.ubicacion.nombre)
        Assertions.assertFalse(vectoresQuilmes.any{v -> v.estaInfectado()})
    }

    @Test
    fun test_elVectorCuandoSeMuevePorElCaminoMasCortoSeExpandeInfectandoALosVectoresQueSeEncuentranEnDichaUbicacion(){
        service.conectar(berazategui.nombre, quilmes.nombre, "TIERRA")
        service.conectar(quilmes.nombre, bernal.nombre, "TIERRA")

        val vectoresSinInfectar = vectorService.recuperarTodos().filter{v -> v.ubicacion.nombre == "Quilmes"}
        Assertions.assertFalse(vectoresSinInfectar.any{v -> v.estaInfectado()})

        service.moverMasCorto(mosca.id!!, bernal.nombre)
        mosca = vectorService.recuperarVector(mosca.id!!)
        val vectoresQuilmes = vectorService.recuperarTodos().filter{v -> v.ubicacion.nombre == "Quilmes"}

        Assertions.assertEquals(bernal.nombre, mosca.ubicacion.nombre)
        Assertions.assertTrue(vectoresQuilmes.any{v -> v.estaInfectado()})
    }

    @Test
    fun test_unVectorNoInfectadoSeMuevePorElCaminoMasCortoYNoIfectaALosVectoresEnDichaUbicacion(){
        val vector = vectorService.crearVector(TipoDeVector.Insecto, varela.id!!)
        service.conectar(varela.nombre, quilmes.nombre, "TIERRA")
        service.conectar(quilmes.nombre, wilde.nombre, "TIERRA")

        Assertions.assertFalse(vector.estaInfectado())

        service.moverMasCorto(vector.id!!, wilde.nombre)
        val vectorRecuperado = vectorService.recuperarVector(vector.id!!)
        val vectoresQuilmes = vectorService.recuperarTodos().filter{v -> v.ubicacion.nombre == "Quilmes"}

        Assertions.assertEquals(wilde.nombre, vectorRecuperado.ubicacion.nombre)
        Assertions.assertFalse(vectoresQuilmes.any{v -> v.estaInfectado()})
    }

    @Test
    fun test_unVectorNoPuedeMoversePorElCaminoMasCortoPorqueNoHayRutaPosible(){
        service.conectar(berazategui.nombre, constitucion.nombre, "TIERRA")
        val ubicacionActual = mosca.ubicacion
        val exception = Assertions.assertThrows(UbicacionNoAlcanzable::class.java) {
            service.moverMasCorto(mosca.id!!, retiro.nombre)
        }
        Assertions.assertEquals(
            "No hay caminos disponibles para el tipo de vector.",
            exception.message
        )
        Assertions.assertEquals(ubicacionActual, mosca.ubicacion)
    }

    @Test
    fun test_seIntentaMoverMasCortoAUnaUbicacionNoConectadaCorrectamente(){
        mosca = vectorService.crearVector(TipoDeVector.Insecto, berazategui.id!!)
        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", berazategui.id!!)
        service.conectar(constitucion.nombre, berazategui.nombre, "TIERRA")

        Assertions.assertThrows(UbicacionNoAlcanzable::class.java){
            service.moverMasCorto(mosca.id!!, constitucion.nombre)
        }
    }

    @Test
    fun test_unVectorNoPuedeMoversePorElCaminoMasCortoPorqueLasRutasPosiblesNoSonCompatiblesConElVector(){
        service.conectar(berazategui.nombre, constitucion.nombre, "TIERRA")
        service.conectar(constitucion.nombre, retiro.nombre, "mar")
        val exception = Assertions.assertThrows(UbicacionNoAlcanzable::class.java) {
            service.moverMasCorto(mosca.id!!, retiro.nombre)
        }
        Assertions.assertEquals("No hay caminos disponibles para el tipo de vector.", exception.message)
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}