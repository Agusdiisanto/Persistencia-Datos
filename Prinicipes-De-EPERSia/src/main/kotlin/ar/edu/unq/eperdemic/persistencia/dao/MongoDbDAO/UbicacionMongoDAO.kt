package ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO

import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionMongoDB
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UbicacionMongoDAO: MongoRepository<UbicacionMongoDB, String> {
    @Aggregation(pipeline = [
        "{\$match: {nombre: {\$in: ?0}}}",
        "{\$group: {_id: '\$distritoID', total: {\$sum: 1}}}",
        "{\$sort: {total: -1}}",
        "{\$limit: 1}",
        "{\$project: { _id: 1 }}"
    ])
    fun distritoMasEnfermo(ubicacionesInfectadas: List<String>): String?

    fun findByNombre(nombre: String): UbicacionMongoDB

    @Query("{'coordenada': {'\$geoWithin': {'\$centerSphere': [[?0,?1], ?2]}},'nombre': ?3}", exists = true)
    fun estaAMenorDistanciaDe(longitud: Double, latitud: Double, distanciaEnRadianes : Double, nombreDeUbicacion:String):Boolean

    @Query("{'coordenada': {'\$geoWithin':{'\$geometry': ?0}}}")
    fun findByCoordenadaWithin(area:GeoJsonPolygon):List<UbicacionMongoDB>
}
