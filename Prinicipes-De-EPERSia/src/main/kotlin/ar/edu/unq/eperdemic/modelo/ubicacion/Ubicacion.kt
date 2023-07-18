package ar.edu.unq.eperdemic.modelo.ubicacion

import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exceptions.EmptyNameException
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import javax.persistence.*

@Entity
class Ubicacion(@Column(unique = true) var nombre: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(mappedBy = "ubicacion", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var vectores: MutableSet<Vector> = mutableSetOf()

    fun vectorAlAzar(): Vector {
        if(vectores.isEmpty()){
            throw NotFoundException("No se encontró un vector en la ubicación dada")
        }
        else{
            return vectores.random()
        }
    }

    fun contieneEspecie(especieId: Long): Boolean {
        return vectores.any{v -> v.contieneEspecie(especieId)}
    }

    fun vectoresInfectados(): List<Vector> {
        return vectores.filter{ v-> v.estaInfectado() }
    }

    init {
        validarUbicacion()
    }

    private fun validarUbicacion() {
        if (this.nombre.isBlank()) {
            throw EmptyNameException("La ubicacion le falta un nombre")
        }
    }
}