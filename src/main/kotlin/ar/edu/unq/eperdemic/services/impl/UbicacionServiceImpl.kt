package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.ubicacion.*
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO.UbicacionFirestoreDAO
import ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO.UbicacionFirestoreDAOImpl
import ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO.UbicacionMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.Neo4JDAO.UbicacionNeo4jDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.UbicacionJPADAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.VectorDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import ar.edu.unq.eperdemic.services.exceptions.UbicacionMuyLejana
import ar.edu.unq.eperdemic.services.exceptions.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.services.exceptions.UniqueException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UbicacionServiceImpl: UbicacionService, EntentyUtils(){
    @Autowired private lateinit var ubicacionJPADAO: UbicacionJPADAO
    @Autowired private lateinit var ubicacionNeo4jDAO: UbicacionNeo4jDAO
    @Autowired private lateinit var ubicacionFireRepository: UbicacionFirestoreDAO
    @Autowired private lateinit var vectorDAO: VectorDAO
    @Autowired private lateinit var ubicacionMongoDAO: UbicacionMongoDAO
    @Autowired private lateinit var distritoDAO: DistritoDAO


    override fun mover(vectorId: Long, ubicacionNombre: String) {
        val vector = this.findByIdOrThrow(vectorDAO, vectorId)
        val nombreUbicacionActual = vector.ubicacion.nombre
        val ubicacionActual = ubicacionMongoDAO.findByNombre(nombreUbicacionActual)
        val ubicacionDestino = ubicacionJPADAO.findByNombre(ubicacionNombre)
        val nombreUbicacionDestino = ubicacionDestino.nombre
        val km100ARadianes = 100.0/6371
        if (!ubicacionMongoDAO.estaAMenorDistanciaDe(ubicacionActual.coordenada.x, ubicacionActual.coordenada.y,km100ARadianes, nombreUbicacionDestino) ||
             ubicacionNeo4jDAO.esUbicacionMuyLejana(nombreUbicacionActual,nombreUbicacionDestino)
        ){
            throw UbicacionMuyLejana(
                "La ubicacion '$nombreUbicacionDestino' es muy lejana a '$nombreUbicacionActual'."
            )
        }
        if (!ubicacionNeo4jDAO.esConexionValida(nombreUbicacionActual,nombreUbicacionDestino,vector.tipo.rutasTransitables)){
            throw UbicacionNoAlcanzable("No hay caminos disponibles para el tipo de vector.")
        }
        vector.mover(ubicacionDestino)
        vector.expandir()
        actualizarUbicacionesFirebase(nombreUbicacionActual, nombreUbicacionDestino)
        vectorDAO.save(vector)
    }

    override fun moverMasCorto(vectorId: Long, nombreDeUbicacion: String) {
        val vector = this.findByIdOrThrow(vectorDAO,vectorId)
        val ubicacionRecuperada = ubicacionJPADAO.findByNombre(nombreDeUbicacion)
        val nombreUbicacionActual = vector.ubicacion.nombre
        val nombreUbicacionDestino = ubicacionRecuperada.nombre

        val rutaMasCorta = ubicacionNeo4jDAO.caminoMasCorto(nombreUbicacionActual, nombreUbicacionDestino, vector.tipo.rutasTransitables)

        if(rutaMasCorta.isEmpty()) {
            throw UbicacionNoAlcanzable("No hay caminos disponibles para el tipo de vector.")
        }
        val ubicacionesIntermedias = ubicacionJPADAO.findAllByNombreIn(rutaMasCorta.filter{s -> s != nombreUbicacionActual && s != nombreUbicacionDestino})

        ubicacionesIntermedias.forEach { u-> vector.mover(u); vector.expandir()}
        vector.mover(ubicacionRecuperada)
        vector.expandir()

        actualizarUbicacionesFirebase(nombreUbicacionActual, nombreUbicacionDestino)

        vectorDAO.save(vector)
    }

    private fun actualizarUbicacionesFirebase(nombreUbicacionActual: String, nombreUbicacionDestino: String) {
        val ubicacionInicial = ubicacionFireRepository.findByName(nombreUbicacionActual)
        val ubicacionFinal = ubicacionFireRepository.findByName(nombreUbicacionDestino)
        ubicacionFireRepository.actualizarVectores(ubicacionInicial.id!!, ubicacionFinal.id!!)
    }

    override fun expandir(ubicacionNombre: String) {
        val ubicacion = ubicacionJPADAO.findByNombre(ubicacionNombre)
        val infectadosEnUbicacion = ubicacion.vectoresInfectados()
        if(infectadosEnUbicacion.isNotEmpty()){
            val infectadoAlAzar = infectadosEnUbicacion.random()
            infectadoAlAzar.expandir()
        }
        ubicacionJPADAO.save(ubicacion)
    }

    override fun conectar(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: String) {
        val ubicacion1 = ubicacionNeo4jDAO.findByNombre(nombreDeUbicacion1)
        val ubicacion2 = ubicacionNeo4jDAO.findByNombre(nombreDeUbicacion2)
        ubicacion1.conectar(ubicacion2, tipoCamino)
        ubicacionNeo4jDAO.save(ubicacion1)
    }

    override fun conectados(nombreDeUbicacion: String): List<Ubicacion> {
        val conectados = ubicacionNeo4jDAO.conectados(nombreDeUbicacion)
        return ubicacionJPADAO.findAllByNombreIn(conectados)
    }

    override fun crearUbicacion(nombreUbicacion: String, coordenada : Coordenada): Ubicacion {
        if (ubicacionJPADAO.existsByNombreUbicacion(nombreUbicacion)) {
            throw UniqueException("No se puede guardar el objeto debido a que ya existe otro objeto con el mismo nombre")
        }
        val ubicacion = Ubicacion(nombreUbicacion)
        val ubicacionNeo4j = UbicacionNeo4j(nombreUbicacion)
        val ubicacionMongoDB = UbicacionMongoDB(nombreUbicacion,coordenada)
        val ubicacionFire = UbicacionFire(nombreUbicacion, latitud=coordenada.latitud!!, longitud=coordenada.longitud!!)
        val distrito = distritoDAO.findDistritoIdByAreaWithin(ubicacionMongoDB.coordenada)
            ?: throw NotFoundException("No Hay distrito para la coordenada")
        ubicacionMongoDB.actualizarDistrito(distrito)
        ubicacionJPADAO.save(ubicacion)
        ubicacionNeo4jDAO.save(ubicacionNeo4j)
        ubicacionMongoDAO.save(ubicacionMongoDB)
        ubicacionFireRepository.saveOrUpdate(ubicacionFire)
        return ubicacion
    }

    override fun recuperarTodos(): List<Ubicacion> {
        return ubicacionJPADAO.findAll().toMutableList()
    }

    override fun recuperarUbicacion(ubicacionNombre: String): Ubicacion {
        return ubicacionJPADAO.findByNombre(ubicacionNombre)
    }

    override fun clear() {
        ubicacionJPADAO.deleteAll()
        vectorDAO.deleteAll()
    }

    override fun recuperarCoordenadas(ubicacionNombre: String): Coordenada {
        val point = ubicacionMongoDAO.findByNombre(ubicacionNombre).coordenada
        return Coordenada(point.y, point.x)
    }

    override fun recuperarAlerta(nombre: String): String {
        val ubicacionFire = ubicacionFireRepository.findByName(nombre)
        return ubicacionFire.alerta
    }

    override fun ubicacionesConAlerta(alerta: Alerta): List<Ubicacion> {
        val nombresDeUbicaciones = ubicacionFireRepository.nombresDeUbicacionesConAlerta(alerta.name)
        val ubicaciones = ubicacionJPADAO.findAllByNombreIn(nombresDeUbicaciones)
        return ubicaciones
    }


}