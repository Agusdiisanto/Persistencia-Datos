package ar.edu.unq.eperdemic.servicesTest.Relacional

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoTestContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import ar.edu.unq.eperdemic.services.impl.Cleaner
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
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
class VectorServiceTest {
    //Service
    @Autowired private lateinit var service: VectorServiceImpl
    @Autowired private lateinit var serviceUbicacion: UbicacionService
    @Autowired private lateinit var servicePatogeno: PatogenoService
    @Autowired private lateinit var serviceEspecie: EspecieService
    @Autowired private lateinit var serviceDistrito : DistritoService
    @Autowired private lateinit var cleaner: Cleaner

    //Modelo
    private lateinit var vectorPersonaInfectada: Vector
    private lateinit var vectorInsectoinfectado: Vector
    private lateinit var vectorAnimalInfectado: Vector
    private lateinit var vectorPersona: Vector
    private lateinit var vectorInsecto: Vector
    private lateinit var vectorAnimal: Vector
    private lateinit var ubicacion: Ubicacion
    private lateinit var ubicacion2: Ubicacion
    private lateinit var patogeno: Patogeno
    private lateinit var patogenoImp: Patogeno
    private lateinit var especieCovid: Especie
    private lateinit var especieHantavirus: Especie
    private lateinit var especieSars: Especie
    private lateinit var especieEbola: Especie
    private lateinit var distrito : Distrito
    private lateinit var coordenada : Coordenada

    // Test
    private val testeo : RandomContagio = RandomContagio

    @BeforeEach
    fun crearModelo(){

        distrito = Distrito("Amba", GeoJsonPolygon(listOf(GeoJsonPoint(24.0, 12.2), GeoJsonPoint(10.0,8.0), GeoJsonPoint(4.0,6.0))))
        serviceDistrito.crear(distrito)

        //Modelo
        patogeno = Patogeno("Virus")
        coordenada = Coordenada(12.2, 24.0)


        //Persistencia
        ubicacion = serviceUbicacion.crearUbicacion("Buenos Aires",coordenada)
        ubicacion2 = serviceUbicacion.crearUbicacion("Santa Fe",coordenada)
        patogenoImp = servicePatogeno.crearPatogeno(patogeno)

        // Vectores
        vectorPersonaInfectada = service.crearVector(TipoDeVector.Persona, ubicacion.id!!)
        vectorInsectoinfectado = service.crearVector(TipoDeVector.Insecto, ubicacion.id!!)
        vectorAnimalInfectado = service.crearVector(TipoDeVector.Animal, ubicacion.id!!)
        vectorPersona = service.crearVector(TipoDeVector.Persona, ubicacion2.id!!)
        vectorInsecto = service.crearVector(TipoDeVector.Insecto, ubicacion2.id!!)
        vectorAnimal = service.crearVector(TipoDeVector.Animal, ubicacion2.id!!)

        // Especies
        testeo.setModo(ModoTestContagio(99,99,99,10,10))
        especieCovid = servicePatogeno.agregarEspecie(patogeno.id!!, "Covid", ubicacion.id!!)
        especieHantavirus = servicePatogeno.agregarEspecie(patogeno.id!!, "Hantavirus", ubicacion.id!!)

        testeo.setModo(ModoTestContagio(1,1,1,2,99))
        especieSars = servicePatogeno.agregarEspecie(patogeno.id!!, "Sars", ubicacion.id!!)
        especieEbola = servicePatogeno.agregarEspecie(patogeno.id!!, "Ebola", ubicacion.id!!)
    }

    fun vectoresRecuperados(vectores : MutableSet<Vector>): MutableSet<Vector> {
        return (vectores.map { v -> service.recuperarVector(v.id!!) }).toMutableSet()
    }

    @Test
    fun test_alAgregarUnVectorSePersiste(){
        Assertions.assertDoesNotThrow { service.recuperarVector(vectorPersona.id!!) }
    }

    @Test
    fun test_alPersistirUnVectorSeGeneraUnId(){
        Assertions.assertNotNull(vectorPersona.id)
    }

    @Test
    fun test_seRecuperaUnVectorPersistido(){
        val vectorRecuperado = service.recuperarVector(vectorPersona.id!!)
        val nombreDeUbicacion = vectorRecuperado.ubicacion.nombre

        Assertions.assertEquals(ubicacion2.nombre, nombreDeUbicacion)
        Assertions.assertEquals(TipoDeVector.Persona, vectorRecuperado.tipo)
    }

    @Test
    fun test_seRecuperanTodosLasEspeciesPersistidas(){
        val vectores = service.recuperarTodos()
        Assertions.assertEquals(6, vectores.size)
    }

    @Test
    fun test_seIntentaRecuperarUnVectorNoPersistido(){
        service.borrarVector(vectorPersona.id!!)

        val exception = Assertions.assertThrows(NotFoundException::class.java, { service.recuperarVector(vectorPersona.id!!) }, "No hay ningun Vector con ese ID")
        Assertions.assertEquals("No se pudo encontrar la entidad con ID ${vectorPersona.id!!}", exception.message)
    }

    @Test
    fun test_seBorraUnVectorPersistido(){
        service.borrarVector(vectorPersona.id!!)
        Assertions.assertThrows(NotFoundException::class.java, { service.recuperarVector(vectorPersona.id!!) }, "No hay ningun Vector con ese ID")
    }

    @Test
    fun test_seIntentaBorrarUnVectorQueNoEstaPersistido(){
        service.borrarVector(vectorPersona.id!!)
        Assertions.assertThrows(NotFoundException::class.java, { service.borrarVector(vectorPersona.id!!) }, "No se encontró el vector con ID 2")
    }

    @Test
    fun test_seIntentaRecuperarTodosLosVectoresPeroNoHayNingunoPersistido(){
        service.borrarVector(vectorPersonaInfectada.id!!)
        service.borrarVector(vectorInsectoinfectado.id!!)
        service.borrarVector(vectorAnimalInfectado.id!!)
        service.borrarVector(vectorPersona.id!!)
        service.borrarVector(vectorInsecto.id!!)
        service.borrarVector(vectorAnimal.id!!)
        val vectores = service.recuperarTodos()

        Assertions.assertTrue(vectores.isEmpty())
    }

    @Test
    fun test_seInfectaUnVectorConUnaEspecie(){
        service.infectar(vectorPersona.id!!, especieCovid.id!!)
        val cantEspecies = service.recuperarVector(vectorPersona.id!!).especies.size
        val covidRecuperado = serviceEspecie.recuperarEspecie(especieCovid.id!!)
        Assertions.assertTrue(covidRecuperado.vectores.any {v -> v.id == vectorPersona.id})
        Assertions.assertEquals(1, cantEspecies)
    }

    @Test
    fun test_enfermedadesDeUnVector(){
        service.infectar(vectorPersona.id!!, especieCovid.id!!)
        service.infectar(vectorPersona.id!!, especieHantavirus.id!!)

        val especies = service.enfermedades(vectorPersona.id!!)

        Assertions.assertEquals(2, especies.size)
    }

    @Test
    fun test_enfermedadesDeUnVectorSinRepeticion(){
        service.infectar(vectorPersona.id!!, especieCovid.id!!)
        service.infectar(vectorPersona.id!!, especieHantavirus.id!!)
        service.infectar(vectorPersona.id!!, especieHantavirus.id!!)

        val especies = service.enfermedades(vectorPersona.id!!)

        Assertions.assertEquals(2, especies.size)
    }

    @Test
    fun test_seContagiaUnaListaDeVectoresVacia(){
        val vectores = mutableSetOf<Vector>()

        service.infectar(vectorPersona.id!!, especieCovid.id!!)
        vectorPersona = service.recuperarVector(vectorPersona.id!!)
        service.contagiar(vectorPersona,vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
    }

    // TESTS DE PERSONA CONTAGIANDO OTROS VECTORES
    @Test
    fun test_unaPersonaNoContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        service.infectar(vectorPersonaInfectada.id!!, especieEbola.id!!)
        vectorPersonaInfectada = service.recuperarVector(vectorPersonaInfectada.id!!)
        service.contagiar(vectorPersonaInfectada, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{ v -> v.contieneEspecie(especieEbola.id!!) })
    }

    @Test
    fun test_unaPersonaNoContagiaUnaListaConUnaSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        service.infectar(vectorPersonaInfectada.id!!, especieEbola.id!!)
        vectorPersonaInfectada = service.recuperarVector(vectorPersonaInfectada.id!!)
        service.contagiar(vectorPersonaInfectada, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{ v -> v.contieneEspecie(especieEbola.id!!) })
    }

    @Test
    fun test_unaPersonaNuncaContagiaUnaListaConUnSoloAnimal(){
        val vectores = mutableSetOf(vectorAnimal)

        service.infectar(vectorPersonaInfectada.id!!, especieCovid.id!!)
        vectorPersonaInfectada = service.recuperarVector(vectorPersonaInfectada.id!!)
        service.contagiar(vectorPersonaInfectada, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
    }

    @Test
    fun test_unaPersonaContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        service.infectar(vectorPersonaInfectada.id!!, especieCovid.id!!)
        vectorPersonaInfectada = service.recuperarVector(vectorPersonaInfectada.id!!)
        service.contagiar(vectorPersonaInfectada,vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertTrue(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
        Assertions.assertEquals(1, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
    }

    @Test
    fun test_unaPersonaContagiaUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        service.infectar(vectorPersonaInfectada.id!!, especieCovid.id!!)
        vectorPersonaInfectada = service.recuperarVector(vectorPersonaInfectada.id!!)
        service.contagiar(vectorPersonaInfectada, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertTrue(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
        Assertions.assertEquals(1, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
    }

    // TESTS DE INSECTO CONTAGIANDO OTROS VECTORES
    @Test
    fun test_unInsectoNoContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        service.infectar(vectorInsectoinfectado.id!!, especieEbola.id!!)
        vectorInsectoinfectado = service.recuperarVector(vectorInsectoinfectado.id!!)
        service.contagiar(vectorInsectoinfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{v -> v.contieneEspecie(especieEbola.id!!)})
    }

    @Test
    fun test_unInsectoNoContagiaUnaListaConUnSoloAnimal(){
        val vectores = mutableSetOf(vectorAnimal)

        service.infectar(vectorInsectoinfectado.id!!, especieEbola.id!!)
        vectorInsectoinfectado = service.recuperarVector(vectorInsectoinfectado.id!!)
        service.contagiar(vectorInsectoinfectado,vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{ v -> v.contieneEspecie(especieEbola.id!!) })
    }

    @Test
    fun test_unInsectoNuncaContagiaUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        service.infectar(vectorInsectoinfectado.id!!, especieCovid.id!!)
        vectorInsectoinfectado = service.recuperarVector(vectorInsectoinfectado.id!!)
        service.contagiar(vectorInsectoinfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
    }

    @Test
    fun test_unInsectoContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        service.infectar(vectorInsectoinfectado.id!!, especieCovid.id!!)
        vectorInsectoinfectado = service.recuperarVector(vectorInsectoinfectado.id!!)
        service.contagiar(vectorInsectoinfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertTrue(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
        Assertions.assertEquals(1, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
    }

    @Test
    fun test_unInsectoContagiaUnaListaConUnSoloAnimal(){
        val vectores = mutableSetOf(vectorAnimal)

        service.infectar(vectorInsectoinfectado.id!!, especieCovid.id!!)
        vectorInsectoinfectado = service.recuperarVector(vectorInsectoinfectado.id!!)
        service.contagiar(vectorInsectoinfectado,vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertTrue(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
        Assertions.assertEquals(1, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
    }

    // TESTS DE ANIMAL CONTAGIANDO OTROS VECTORES
    @Test
    fun test_unAnimalNoContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        service.infectar(vectorAnimalInfectado.id!!, especieEbola.id!!)
        vectorAnimalInfectado = service.recuperarVector(vectorAnimalInfectado.id!!)
        service.contagiar(vectorAnimalInfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{ v -> v.contieneEspecie(especieEbola.id!!) })
    }

    @Test
    fun test_unAnimalNoContagiaUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        service.infectar(vectorAnimalInfectado.id!!, especieEbola.id!!)
        vectorAnimalInfectado = service.recuperarVector(vectorAnimalInfectado.id!!)
        service.contagiar(vectorAnimalInfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{ v -> v.contieneEspecie(especieEbola.id!!) })
    }

    @Test
    fun test_unAnimalNuncaContagiaUnaListaConUnSoloAnimal(){
        val vectores = mutableSetOf(vectorAnimal)

        service.infectar(vectorAnimalInfectado.id!!, especieCovid.id!!)
        vectorAnimalInfectado = service.recuperarVector(vectorAnimalInfectado.id!!)
        service.contagiar(vectorAnimalInfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertFalse(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
    }

    @Test
    fun test_unAnimalContagiaUnaListaConUnaSolaPersona(){
        val vectores = mutableSetOf(vectorPersona)

        service.infectar(vectorAnimalInfectado.id!!, especieCovid.id!!)
        vectorAnimalInfectado = service.recuperarVector(vectorAnimalInfectado.id!!)
        service.contagiar(vectorAnimalInfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertTrue(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
        Assertions.assertEquals(1, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
    }

    @Test
    fun test_unAnimalContagiaUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        service.infectar(vectorAnimalInfectado.id!!, especieCovid.id!!)
        vectorAnimalInfectado = service.recuperarVector(vectorAnimalInfectado.id!!)
        service.contagiar(vectorAnimalInfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertTrue(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
        Assertions.assertEquals(1, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
    }

    // Con varias especies
    @Test
    fun test_unAnimalContagiaConDosEspeciesAUnaListaConUnSoloInsecto(){
        val vectores = mutableSetOf(vectorInsecto)

        service.infectar(vectorAnimalInfectado.id!!, especieCovid.id!!)
        service.infectar(vectorAnimalInfectado.id!!, especieHantavirus.id!!)
        vectorAnimalInfectado = service.recuperarVector(vectorAnimalInfectado.id!!)
        service.contagiar(vectorAnimalInfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertTrue(vectoresRecuperados.any{ v -> v.contieneEspecie(especieCovid.id!!) })
        Assertions.assertTrue(vectoresRecuperados.any{ v -> v.contieneEspecie(especieHantavirus.id!!) })
        Assertions.assertEquals(1, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
    }

    // Infectar a varios a la vez
    @Test
    fun test_unAnimalContagiaUnaListaDeVectoresDeDistintoTipoConDosEspeciesSiCorresponde(){
        val vectores = mutableSetOf(vectorPersona, vectorInsecto, vectorAnimal)

        service.infectar(vectorAnimalInfectado.id!!, especieCovid.id!!)
        service.infectar(vectorAnimalInfectado.id!!, especieHantavirus.id!!)
        vectorAnimalInfectado = service.recuperarVector(vectorAnimalInfectado.id!!)
        service.contagiar(vectorAnimalInfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertEquals(2, vectoresRecuperados.filter{ v -> v.estaInfectado() }.size)
        Assertions.assertFalse(vectorAnimal.estaInfectado())
    }

    @Test
    fun test_unAnimalContagiaUnaListaDePersonasSiCorresponde(){
        val vectorPersona2 = service.crearVector(TipoDeVector.Persona, ubicacion2.id!!)
        val vectores = mutableSetOf(vectorPersona, vectorPersona2)

        service.infectar(vectorAnimalInfectado.id!!, especieCovid.id!!)
        service.infectar(vectorAnimalInfectado.id!!, especieEbola.id!!)
        vectorAnimalInfectado = service.recuperarVector(vectorAnimalInfectado.id!!)
        service.contagiar(vectorAnimalInfectado,vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertEquals(2, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
        Assertions.assertEquals(0, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieEbola.id!!) }.size)
    }

    @Test
    fun test_unInsectoContagiaUnaListaDeAnimalesSiCorresponde(){
        val vector3 = service.crearVector(TipoDeVector.Animal, ubicacion2.id!!)
        val vectores = mutableSetOf(vectorAnimal, vector3)

        service.infectar(vectorInsectoinfectado.id!!, especieCovid.id!!)
        service.infectar(vectorInsectoinfectado.id!!, especieEbola.id!!)
        vectorInsectoinfectado = service.recuperarVector(vectorInsectoinfectado.id!!)
        service.contagiar(vectorInsectoinfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertEquals(2, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
        Assertions.assertEquals(0, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieEbola.id!!) }.size)
    }

    @Test
    fun test_unInsectoContagiaAlgunosVectoresDeUnaLista(){
        val vector3 = service.crearVector(TipoDeVector.Animal, ubicacion2.id!!)
        val vectores = mutableSetOf(vectorAnimal, vector3)

        service.infectar(vectorInsectoinfectado.id!!, especieCovid.id!!)
        service.infectar(vectorInsectoinfectado.id!!, especieEbola.id!!)
        vectorInsectoinfectado = service.recuperarVector(vectorInsectoinfectado.id!!)
        service.contagiar(vectorInsectoinfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertEquals(2, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieCovid.id!!) }.size)
        Assertions.assertEquals(0, vectoresRecuperados.filter{ v -> v.contieneEspecie(especieEbola.id!!) }.size)
    }

    @Test
    fun test_unInsectoContagiaUnaListaDeVectoresDeDistintoTipoConDosEspeciesSiCorresponde(){
        val vectores = mutableSetOf(vectorPersona, vectorInsecto, vectorAnimal)

        service.infectar(vectorInsectoinfectado.id!!, especieCovid.id!!)
        service.infectar(vectorInsectoinfectado.id!!, especieHantavirus.id!!)
        vectorInsectoinfectado = service.recuperarVector(vectorInsectoinfectado.id!!)
        service.contagiar(vectorInsectoinfectado, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertEquals(2, vectoresRecuperados.filter{ v -> v.estaInfectado() }.size)
        Assertions.assertFalse(vectorInsecto.estaInfectado())
    }

    @Test
    fun test_unaPersonaContagiaUnaListaDeVectoresDeDistintoTipoConDosEspeciesSiCorresponde(){
        val vectores = mutableSetOf(vectorPersona, vectorInsecto, vectorAnimal)

        service.infectar(vectorPersonaInfectada.id!!, especieCovid.id!!)
        service.infectar(vectorPersonaInfectada.id!!, especieHantavirus.id!!)
        vectorPersonaInfectada = service.recuperarVector(vectorPersonaInfectada.id!!)
        service.contagiar(vectorPersonaInfectada, vectores)

        val vectoresRecuperados = vectoresRecuperados(vectores)

        Assertions.assertEquals(2, vectoresRecuperados.filter{ v -> v.estaInfectado() }.size)
        Assertions.assertFalse(vectorAnimal.estaInfectado())
    }

    @AfterEach
    fun clearUp(){
        cleaner.cleanDB()
    }
}