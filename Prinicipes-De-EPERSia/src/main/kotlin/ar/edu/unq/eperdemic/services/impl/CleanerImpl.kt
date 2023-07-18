package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO.UbicacionFirestoreDAOImpl
import ar.edu.unq.eperdemic.persistencia.dao.Neo4JDAO.UbicacionNeo4jDAO
import ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO.UbicacionMongoDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Service
@Transactional
class CleanerImpl : Cleaner {
    @Autowired lateinit var dataDAO: DataDAO
    @Autowired lateinit var neo4jDAO: UbicacionNeo4jDAO
    @Autowired lateinit var mongoDAO: UbicacionMongoDAO
    @Autowired lateinit var distritoDAO: DistritoDAO
    @Autowired lateinit var ubicacionFireRepo : UbicacionFirestoreDAOImpl

    override fun cleanDB() {
        dataDAO.clear()
        neo4jDAO.clearAll()
        mongoDAO.deleteAll()
        distritoDAO.deleteAll()
        ubicacionFireRepo.deleteAll()
    }
}

interface Cleaner {
    fun cleanDB()
}

@Component
class DataDAO {
    @PersistenceContext
    lateinit var entityManager: EntityManager

    fun clear() {
        val nombreDeTablas = entityManager.createNativeQuery("show tables").resultList
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate()
        nombreDeTablas.forEach { result ->
            var tabla = ""
            when(result){
                is String -> tabla = result
                is Array<*> -> tabla= result[0].toString()
            }
            entityManager.createNativeQuery("truncate table $tabla").executeUpdate()
        }
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate()
    }
}