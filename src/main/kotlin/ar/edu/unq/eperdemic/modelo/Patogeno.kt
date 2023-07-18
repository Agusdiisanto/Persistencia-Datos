package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exceptions.EmptyNameException
import java.io.Serializable
import javax.persistence.*

@Entity
class Patogeno(var tipo : String) : Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    @OneToMany(mappedBy = "patogeno", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var especies: MutableSet<Especie> = mutableSetOf()

    init {
        validarPatogeno()
    }

    private fun validarPatogeno() {
        if (this.tipo.isBlank()){
            throw EmptyNameException("El patogono debe tener un tipo")
        }
    }

    fun setId(id : Long) {
        this.id = id
    }

    override fun toString(): String {
        return tipo
    }

    fun crearEspecie(nombreEspecie: String, paisDeOrigen: String) : Especie{
        val especie = Especie(this, nombreEspecie, paisDeOrigen)
        especies.add(especie)
        return especie
    }
}
