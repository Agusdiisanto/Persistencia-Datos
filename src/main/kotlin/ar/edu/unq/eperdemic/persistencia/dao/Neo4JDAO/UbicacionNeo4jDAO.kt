package ar.edu.unq.eperdemic.persistencia.dao.Neo4JDAO

import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionNeo4j
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param

interface UbicacionNeo4jDAO: Neo4jRepository<UbicacionNeo4j, Long?> {
    fun findByNombre(nombreDeUbicacion: String): UbicacionNeo4j

    @Query("""
        MATCH (ubicacionActual:Ubicacion {nombre: ${'$'}nombreDeUbicacionOrigen}),
              (ubicacionDestino:Ubicacion {nombre: ${'$'}nombreDeUbicacionDestino})
        MATCH caminos = (ubicacionActual)-[:RUTA_TERRESTRE|RUTA_MARITIMA|RUTA_AEREA]->(ubicacionDestino)
        RETURN COUNT(caminos) = 0 AS tieneConexion
    """)
    fun esUbicacionMuyLejana(
        @Param("nombreDeUbicacionOrigen") nombreDeUbicacionActual: String,
        @Param("nombreDeUbicacionDestino") nombreDeUbicacionDestino: String
    ): Boolean

    @Query("""
        MATCH (ubicacion:Ubicacion {nombre: ${'$'}nombreDeUbicacion})
        MATCH (ubicacion)-[:RUTA_TERRESTRE|RUTA_MARITIMA|RUTA_AEREA*1]->(destino)
        RETURN destino.nombre
    """)
    fun conectados(nombreDeUbicacion: String): List<String>

    @Query("""
    MATCH path = shortestPath((ubicacionOrigen:Ubicacion)-[*]->(ubicacionDestino:Ubicacion))
    WHERE ubicacionOrigen.nombre = ${'$'}ubicacionOrigenNombre AND ubicacionDestino.nombre = ${'$'}ubicacionDestinoNombre
    AND ALL(rel IN relationships(path) WHERE
        TYPE(rel) IN ${'$'}caminosTransitables
    )
    UNWIND [node in nodes(path) | node.nombre] as ubicacionesIntermedias
    RETURN ubicacionesIntermedias
    """)
    fun caminoMasCorto(
        @Param("ubicacionOrigenNombre") ubicacionActualNombre: String,
        @Param("ubicacionDestinoNombre") ubicacionDestinoNombre: String,
        @Param("caminosTransitables") caminosTransitables: List<String>
    ): List<String>


    @Query("""
    MATCH  (ubicacionActual:Ubicacion {nombre: ${'$'}ubicacionOrigenNombre}),
              (ubicacionDestino:Ubicacion {nombre: ${'$'}ubicacionDestinoNombre})
    MATCH  (ubicacionActual)-[r]->(ubicacionDestino)
    WHERE TYPE(r) IN ${'$'}caminosTransitables
    RETURN COUNT(*) > 0 AS tieneConexion
    """)
    fun esConexionValida(
        @Param("ubicacionOrigenNombre") ubicacionOrigenNombre: String,
        @Param("ubicacionDestinoNombre") ubicacionDestinoNombre: String,
        @Param("caminosTransitables") caminosTransitables: List<String>
    ): Boolean

    @Query("MATCH (n) DETACH DELETE n")
    fun clearAll()
}