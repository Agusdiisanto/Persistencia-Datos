package ar.edu.unq.eperdemic.utils

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.ubicacion.Coordenada
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.ModoTestContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
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
class DataGenerator {
    private lateinit var serviceTesting: RandomContagio

    @Autowired private lateinit var service: UbicacionService
    @Autowired private lateinit var distritoService: DistritoService
    @Autowired private lateinit var cleaner: Cleaner

    private lateinit var berazategui: Ubicacion
    private lateinit var bernal: Ubicacion
    private lateinit var ezpeleta: Ubicacion
    private lateinit var quilmes: Ubicacion
    private lateinit var varela: Ubicacion
    private lateinit var wilde: Ubicacion
    private lateinit var constitucion: Ubicacion
    private lateinit var retiro: Ubicacion
    private lateinit var avellaneda: Ubicacion
    private lateinit var palermo: Ubicacion
    private lateinit var lanus: Ubicacion
    private lateinit var sourigues: Ubicacion
    private lateinit var sarandi: Ubicacion
    private lateinit var villaDominico: Ubicacion
    private lateinit var calzada: Ubicacion
    private lateinit var platanos: Ubicacion
    private lateinit var ranelagh: Ubicacion
    private lateinit var solano: Ubicacion
    private lateinit var laPlata: Ubicacion
    private lateinit var adrogue: Ubicacion
    private lateinit var ezeiza: Ubicacion
    private lateinit var mostacciano: Ubicacion
    private lateinit var tuffelo: Ubicacion
    private lateinit var ottavia: Ubicacion

    private lateinit var zonaSur: Distrito
    private lateinit var capital: Distrito
    private lateinit var roma: Distrito

    @Autowired private lateinit var patogenoService: PatogenoService
    @Autowired private lateinit var vectorService: VectorService

    private lateinit var mosca: Vector
    private lateinit var mosquito: Vector
    private lateinit var vectorHumano: Vector
    private lateinit var vectorAnimal: Vector
    private lateinit var vaca: Vector

    private lateinit var patogeno: Patogeno
    private lateinit var especie: Especie

    @Test
    fun instanciandoVectoresYConectandoUbicaciones(){
        this.serviceTesting = RandomContagio
        serviceTesting.setModo(ModoTestContagio(100, 100, 100, 10, 0))
        zonaSur = Distrito("Zona sur", GeoJsonPolygon(listOf(
            GeoJsonPoint(-58.5206120227501, -34.75986521598616),
            GeoJsonPoint(-58.33853274596787, -34.63620983908328),
            GeoJsonPoint(-57.797128763332225, -34.880383060917865),
            GeoJsonPoint(-58.076805298674174, -35.037383715779036),
            GeoJsonPoint(-58.7924961630727, -35.06365735688912),
            GeoJsonPoint(-58.52537175234073, -34.76245495358772)
        )))
        capital = Distrito("Capital", GeoJsonPolygon(listOf(
            GeoJsonPoint(-58.46132732574662, -34.70529541714586),
            GeoJsonPoint(-58.305410977966005, -34.615707280148115),
            GeoJsonPoint(-58.48150324289938, -34.513266666589),
            GeoJsonPoint(-58.54615093901572, -34.6408038430395),
            GeoJsonPoint(-58.46132732574662, -34.70529541714586)
        )))
        roma = Distrito("Roma", GeoJsonPolygon(listOf(
            GeoJsonPoint(12.409286, 41.967806),
            GeoJsonPoint(12.547989, 41.961680),
            GeoJsonPoint(12.626266, 41.893225),
            GeoJsonPoint(12.633133, 41.813438),
            GeoJsonPoint(12.509537, 41.804226),
            GeoJsonPoint(12.441224, 41.802042),
            GeoJsonPoint(12.417878, 41.887465),
            GeoJsonPoint(12.436418, 41.943160)
        )))
        distritoService.crear(zonaSur)
        distritoService.crear(capital)
        distritoService.crear(roma)

        //Capital
        constitucion = service.crearUbicacion("Constitución",Coordenada(-34.627443399230735, -58.38112773136726))
        retiro = service.crearUbicacion("Retiro",Coordenada(-34.59213229374494, -58.37370425306075))
        palermo = service.crearUbicacion("Palermo",Coordenada(-34.57851407542505, -58.4262982582528))
        //Zona sur
        bernal = service.crearUbicacion("Bernal", Coordenada(-34.70949870061372, -58.28046374189505))
        berazategui = service.crearUbicacion("Berazategui",Coordenada(-34.76411622098264, -58.20839546041249))
        quilmes = service.crearUbicacion("Quilmes",Coordenada(-34.724605945540425, -58.26086742494711))
        varela = service.crearUbicacion("Varela",Coordenada(-34.81083276562996, -58.27406142782516))
        wilde = service.crearUbicacion("Wilde",Coordenada(-34.69754986696026, -58.31138280865221))
        lanus = service.crearUbicacion("Lanús",Coordenada(-34.70745299287985, -58.39052078866918))
        avellaneda = service.crearUbicacion("Avellaneda",Coordenada(-34.66474930214841, -58.36309804239171))
        ezpeleta = service.crearUbicacion("Ezpeleta",Coordenada(-34.75184692428602, -58.23420201378276))
        sourigues = service.crearUbicacion("Villa España",Coordenada(-34.80657035523038, -58.212741110396976))
        villaDominico = service.crearUbicacion("Villa Dominico",Coordenada(-34.69151041424846, -58.325365680216635))
        ezeiza = service.crearUbicacion("Ezeiza",Coordenada(-34.854120314596614, -58.522882210301205))
        sarandi = service.crearUbicacion("Sarandí",Coordenada(-34.67890190535881, -58.34461167649826))
        calzada = service.crearUbicacion("Calzada",Coordenada(-34.79677193931379, -58.359039667150654))
        platanos = service.crearUbicacion("Plátanos",Coordenada(-34.782333259741065, -58.17071314116697))
        ranelagh = service.crearUbicacion("Ranelagh",Coordenada(-34.789479702291324, -58.20314636310164))
        adrogue = service.crearUbicacion("Adrogue",Coordenada(-34.79781252016158, -58.39426010234938))
        solano = service.crearUbicacion("Solano",Coordenada(-34.77794475267825, -58.30772788865505))
        laPlata = service.crearUbicacion("La Plata",Coordenada(-34.921284967003416, -57.954526503250435))
        //Roma
        mostacciano = service.crearUbicacion("Mostacciano", Coordenada(41.802042, 12.441224))
        tuffelo = service.crearUbicacion("Tuffelo", Coordenada(41.961680, 12.547989))
        ottavia = service.crearUbicacion("Ottavia", Coordenada(41.893225, 12.626266))

        mosca = vectorService.crearVector(TipoDeVector.Insecto, berazategui.id!!)

        patogeno = Patogeno("Virus")
        patogeno = patogenoService.crearPatogeno(patogeno)
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", berazategui.id!!)

        vectorHumano = vectorService.crearVector(TipoDeVector.Persona, berazategui.id!!)
        vectorAnimal = vectorService.crearVector(TipoDeVector.Animal, berazategui.id!!)
        mosquito = vectorService.crearVector(TipoDeVector.Insecto, bernal.id!!)
        vaca = vectorService.crearVector(TipoDeVector.Persona, berazategui.id!!)
        vectorService.crearVector(TipoDeVector.Animal, berazategui.id!!)
        vectorService.crearVector(TipoDeVector.Animal, berazategui.id!!)
        vectorService.crearVector(TipoDeVector.Animal, berazategui.id!!)
        vectorService.crearVector(TipoDeVector.Animal, berazategui.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)
        vectorService.crearVector(TipoDeVector.Animal, bernal.id!!)

        vectorService.crearVector(TipoDeVector.Persona, quilmes.id!!)
        vectorService.crearVector(TipoDeVector.Persona, quilmes.id!!)
        vectorService.crearVector(TipoDeVector.Persona, quilmes.id!!)
        vectorService.crearVector(TipoDeVector.Persona, quilmes.id!!)
        vectorService.crearVector(TipoDeVector.Animal, quilmes.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, quilmes.id!!)

        vectorService.crearVector(TipoDeVector.Persona, varela.id!!)
        vectorService.crearVector(TipoDeVector.Persona, varela.id!!)
        vectorService.crearVector(TipoDeVector.Persona, varela.id!!)
        vectorService.crearVector(TipoDeVector.Animal, varela.id!!)
        vectorService.crearVector(TipoDeVector.Animal, varela.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, varela.id!!)

        vectorService.crearVector(TipoDeVector.Persona, wilde.id!!)
        vectorService.crearVector(TipoDeVector.Persona, wilde.id!!)
        vectorService.crearVector(TipoDeVector.Persona, wilde.id!!)
        vectorService.crearVector(TipoDeVector.Persona, wilde.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, wilde.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, wilde.id!!)

        vectorService.crearVector(TipoDeVector.Animal, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Animal, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Persona, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Persona, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Persona, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Persona, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Persona, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Persona, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, lanus.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, lanus.id!!)

        vectorService.crearVector(TipoDeVector.Animal, avellaneda.id!!)
        vectorService.crearVector(TipoDeVector.Animal, avellaneda.id!!)
        vectorService.crearVector(TipoDeVector.Animal, avellaneda.id!!)
        vectorService.crearVector(TipoDeVector.Persona, avellaneda.id!!)
        vectorService.crearVector(TipoDeVector.Persona, avellaneda.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, avellaneda.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, avellaneda.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, avellaneda.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, avellaneda.id!!)

        vectorService.crearVector(TipoDeVector.Animal, ezpeleta.id!!)
        vectorService.crearVector(TipoDeVector.Animal, ezpeleta.id!!)
        vectorService.crearVector(TipoDeVector.Animal, ezpeleta.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ezpeleta.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ezpeleta.id!!)

        vectorService.crearVector(TipoDeVector.Insecto, sourigues.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, sourigues.id!!)
        vectorService.crearVector(TipoDeVector.Persona, sourigues.id!!)
        vectorService.crearVector(TipoDeVector.Persona, sourigues.id!!)

        vectorService.crearVector(TipoDeVector.Insecto, villaDominico.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, villaDominico.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, villaDominico.id!!)
        vectorService.crearVector(TipoDeVector.Animal, villaDominico.id!!)

        vectorService.crearVector(TipoDeVector.Persona, ezeiza.id!!)
        vectorService.crearVector(TipoDeVector.Animal, ezeiza.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, ezeiza.id!!)
        vectorService.crearVector(TipoDeVector.Animal, ezeiza.id!!)

        vectorService.crearVector(TipoDeVector.Insecto, sarandi.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, sarandi.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, sarandi.id!!)
        vectorService.crearVector(TipoDeVector.Animal, sarandi.id!!)
        vectorService.crearVector(TipoDeVector.Persona, sarandi.id!!)
        vectorService.crearVector(TipoDeVector.Animal, sarandi.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, sarandi.id!!)
        vectorService.crearVector(TipoDeVector.Animal, sarandi.id!!)

        vectorService.crearVector(TipoDeVector.Persona, calzada.id!!)
        vectorService.crearVector(TipoDeVector.Animal, calzada.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, calzada.id!!)
        vectorService.crearVector(TipoDeVector.Animal, calzada.id!!)

        vectorService.crearVector(TipoDeVector.Persona, platanos.id!!)
        vectorService.crearVector(TipoDeVector.Persona, platanos.id!!)
        vectorService.crearVector(TipoDeVector.Persona, platanos.id!!)
        vectorService.crearVector(TipoDeVector.Persona, platanos.id!!)
        vectorService.crearVector(TipoDeVector.Animal, platanos.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, platanos.id!!)

        vectorService.crearVector(TipoDeVector.Persona, ranelagh.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ranelagh.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ranelagh.id!!)
        vectorService.crearVector(TipoDeVector.Animal, ranelagh.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, ranelagh.id!!)

        vectorService.crearVector(TipoDeVector.Persona, adrogue.id!!)
        vectorService.crearVector(TipoDeVector.Persona, adrogue.id!!)
        vectorService.crearVector(TipoDeVector.Persona, adrogue.id!!)
        vectorService.crearVector(TipoDeVector.Persona, adrogue.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, adrogue.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, adrogue.id!!)

        vectorService.crearVector(TipoDeVector.Persona, solano.id!!)
        vectorService.crearVector(TipoDeVector.Animal, solano.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, solano.id!!)
        vectorService.crearVector(TipoDeVector.Animal, solano.id!!)

        vectorService.crearVector(TipoDeVector.Persona, laPlata.id!!)
        vectorService.crearVector(TipoDeVector.Persona, laPlata.id!!)
        vectorService.crearVector(TipoDeVector.Persona, laPlata.id!!)
        vectorService.crearVector(TipoDeVector.Persona, laPlata.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, laPlata.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, laPlata.id!!)
        
        // Vectores en Capital 
        vectorService.crearVector(TipoDeVector.Animal, constitucion.id!!)
        vectorService.crearVector(TipoDeVector.Animal, constitucion.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, constitucion.id!!)
        vectorService.crearVector(TipoDeVector.Persona, constitucion.id!!)
        vectorService.crearVector(TipoDeVector.Persona, constitucion.id!!)
        vectorService.crearVector(TipoDeVector.Persona, constitucion.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, palermo.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, palermo.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, palermo.id!!)
        vectorService.crearVector(TipoDeVector.Persona, palermo.id!!)
        vectorService.crearVector(TipoDeVector.Persona, palermo.id!!)
        vectorService.crearVector(TipoDeVector.Persona, palermo.id!!)
        vectorService.crearVector(TipoDeVector.Persona, retiro.id!!)
        vectorService.crearVector(TipoDeVector.Persona, retiro.id!!)
        vectorService.crearVector(TipoDeVector.Persona, retiro.id!!)
        vectorService.crearVector(TipoDeVector.Persona, retiro.id!!)
        
        // Vectores en Roma
        vectorService.crearVector(TipoDeVector.Animal, mostacciano.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, mostacciano.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, mostacciano.id!!)
        vectorService.crearVector(TipoDeVector.Persona, mostacciano.id!!)
        vectorService.crearVector(TipoDeVector.Persona, tuffelo.id!!)
        vectorService.crearVector(TipoDeVector.Persona, tuffelo.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, tuffelo.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, tuffelo.id!!)
        vectorService.crearVector(TipoDeVector.Insecto, ottavia.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ottavia.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ottavia.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ottavia.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ottavia.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ottavia.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ottavia.id!!)
        vectorService.crearVector(TipoDeVector.Persona, ottavia.id!!)
        
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-A", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-B", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-C", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-D", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-E", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-F", berazategui.id!!)
        patogenoService.agregarEspecie(patogeno.id!!, "Gripe-G", berazategui.id!!)

        service.conectar(lanus.nombre, bernal.nombre, "TIERRA")
        service.conectar(berazategui.nombre, quilmes.nombre, "MAR")
        service.conectar(quilmes.nombre, bernal.nombre, "AIRE")
        service.conectar(wilde.nombre, ezpeleta.nombre, "TIERRA")
        service.conectar(varela.nombre, wilde.nombre, "MAR")
        service.conectar(constitucion.nombre, avellaneda.nombre, "AIRE")
        service.conectar(retiro.nombre, ezpeleta.nombre, "TIERRA")
        service.conectar(avellaneda.nombre, ezpeleta.nombre, "MAR")
        service.conectar(palermo.nombre, quilmes.nombre, "TIERRA")
        service.conectar(varela.nombre, palermo.nombre, "AIRE")
        service.conectar(quilmes.nombre, ezpeleta.nombre, "TIERRA")
        service.conectar(bernal.nombre, berazategui.nombre, "MAR")
        service.conectar(palermo.nombre, quilmes.nombre, "TIERRA")
        service.conectar(varela.nombre, retiro.nombre, "AIRE")
        service.conectar(quilmes.nombre, ezpeleta.nombre, "TIERRA")
        service.conectar(lanus.nombre, berazategui.nombre, "MAR")
        service.conectar(palermo.nombre, quilmes.nombre, "TIERRA")
        service.conectar(varela.nombre, retiro.nombre, "AIRE")
        service.conectar(quilmes.nombre, ezpeleta.nombre, "TIERRA")
        service.conectar(berazategui.nombre, sourigues.nombre, "AIRE")
        service.conectar(berazategui.nombre, ranelagh.nombre, "AIRE")
        service.conectar(ranelagh.nombre, platanos.nombre, "TIERRA")
        service.conectar(platanos.nombre, ranelagh.nombre, "MAR")
        service.conectar(quilmes.nombre, sarandi.nombre, "TIERRA")
        service.conectar(sarandi.nombre, calzada.nombre, "TIERRA")
        service.conectar(sarandi.nombre, solano.nombre, "MAR")
        service.conectar(villaDominico.nombre, varela.nombre, "MAR")
        service.conectar(villaDominico.nombre, ezeiza.nombre, "AIRE")
        service.conectar(ezeiza.nombre, adrogue.nombre, "AIRE")
        service.conectar(mostacciano.nombre, tuffelo.nombre, "AIRE")
        service.conectar(tuffelo.nombre, mostacciano.nombre, "MAR")
        service.conectar(mostacciano.nombre, ottavia.nombre, "AIRE")
    }

    @Test
    fun seMueveUnVector(){
        service.mover(45, "Berazategui")
    }

    @Test
     fun cleanAll(){
        cleaner.cleanDB()
    }
}