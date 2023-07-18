package ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO

import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DistritoDAO : MongoRepository<Distrito, String>{
    @Query("{'area': { '\$geoIntersects': { '\$geometry': ?0 } } }")
    fun findDistritoIdByAreaWithin(coordenada: GeoJsonPoint): Distrito?

    @Query("{'area': {\$geoIntersects: {\$geometry: ?0}}}", exists = true)
    fun existeDistritoEnArea(polygon: GeoJsonPolygon): Boolean

    fun findByNombre(nombre: String): Distrito
}
