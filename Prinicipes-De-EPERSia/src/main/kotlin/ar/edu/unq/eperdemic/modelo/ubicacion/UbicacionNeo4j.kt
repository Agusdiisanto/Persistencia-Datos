package ar.edu.unq.eperdemic.modelo.ubicacion

import ar.edu.unq.eperdemic.modelo.exceptions.InvalidPathTypeException
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import javax.persistence.Column

@Node("Ubicacion")
class UbicacionNeo4j(@Column(unique = true) var nombre:String) {
    @Id
    @GeneratedValue
    var id: Long? = null

    @Relationship(type = "RUTA_TERRESTRE")
    var conexionesDeRutasTerrestres: MutableSet<UbicacionNeo4j> = mutableSetOf()

    @Relationship(type = "RUTA_MARITIMA")
    var conexionesDeRutasMartimas: MutableSet<UbicacionNeo4j> = mutableSetOf()

    @Relationship(type = "RUTA_AEREA")
    var conexionesDeRutasAereas: MutableSet<UbicacionNeo4j> = mutableSetOf()

    fun conectar(ubicacion: UbicacionNeo4j, tipoCamino: String) {
        val conexiones = mapOf(
            "TIERRA" to conexionesDeRutasTerrestres,
            "MAR" to conexionesDeRutasMartimas,
            "AIRE" to conexionesDeRutasAereas
        )

        if (conexiones.containsKey(tipoCamino.uppercase())) {
            conexiones[tipoCamino.uppercase()]?.add(ubicacion)
        } else {
            throw InvalidPathTypeException("El camino '${tipoCamino}' es invalido. Los tipos validos son 'AIRE', 'MAR' y 'TIERRA'.")
        }
    }
}