package ar.edu.unq.eperdemic.modelo.ubicacion

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.*

@Document("Ubicacion")
open class UbicacionMongoDB {
    @Id
    var id: String? = null
    lateinit var nombre: String
    lateinit var coordenada: GeoJsonPoint
    private var distritoID : String? = null

    protected constructor()
    constructor(nombre : String, coordenada: Coordenada){
        this.nombre = nombre
        this.coordenada = GeoJsonPoint(coordenada.longitud!!, coordenada.latitud!!)
    }

    fun actualizarDistrito(distrito : Distrito?) {
        this.distritoID = distrito?.id
    }
}