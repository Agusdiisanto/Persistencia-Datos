package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exceptions.EmptyCountryException
import ar.edu.unq.eperdemic.modelo.exceptions.EmptyNameException
import ar.edu.unq.eperdemic.modelo.exceptions.InfectionRejectedException
import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.utils.RandomEspecie.RandomContagio
import ar.edu.unq.eperdemic.modelo.utils.RandomMutacion.RandomMutacion
import javax.persistence.*

@Entity
class Especie(@ManyToOne var patogeno: Patogeno, var nombre : String , var paisDeOrigen: String){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    private var contagioPorPersona = 0
    private var contagioPorAnimal = 0
    private var contagioPorInsecto = 0
    private var capacidadDeBiomecanizacion = 0
    var defensaMicroorganismos = 0

    @ManyToMany(mappedBy = "especies", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)

    var vectores: MutableSet<Vector> = mutableSetOf()

    @OneToMany(mappedBy = "especie", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var mutaciones: MutableSet<Mutacion> = mutableSetOf()

    init{
        validarEspecie()
        this.contagioPorPersona = RandomContagio.randomContagioPersona()
        this.contagioPorAnimal = RandomContagio.randomContagioAnimal()
        this.contagioPorInsecto = RandomContagio.randomContagioInsecto()
        this.capacidadDeBiomecanizacion = RandomMutacion.randomCapacidadBiomecanizacion()
        this.defensaMicroorganismos = RandomMutacion.randomDefenseMicroorganismo()
    }

    // ================== PRIVATE METHODS ==================
    private fun validarEspecie() {
        if (this.nombre.isBlank()){
            throw EmptyNameException("La especie debe tener un nombre")
        }
        if(this.paisDeOrigen.isBlank()){
            throw EmptyCountryException()
        }
    }

    private fun capacidadDeContagioSegun(tipo: TipoDeVector): Int {
        val capacidadDeContagioMap = mapOf(
            TipoDeVector.Persona to contagioPorPersona,
            TipoDeVector.Animal  to contagioPorAnimal,
            TipoDeVector.Insecto to contagioPorInsecto
        )
        return capacidadDeContagioMap[tipo] ?: error("No se encontró la estrategia para $tipo")
    }

    private fun infectarSegunDefensa(vectorContagiado: Vector, vectorInfector: Vector) {
        if(!vectorContagiado.contieneMutacionSupresion(this)) {
            infectaA(vectorContagiado)
            analizarMutacion(vectorInfector)
        }
    }

    private fun analizarMutacion(vectorOriginario : Vector){
        if(tieneMutaciones()){
            val mutacionAlAzar = mutaciones.random()
            vectorOriginario.mutarSegunProbabilidad(mutacionAlAzar, capacidadDeBiomecanizacion)
        }
    }

    private fun tieneMutaciones() : Boolean {
        return mutaciones.isNotEmpty()
    }

    // ================== PUBLIC METHODS ==================
    fun infectaA(vector : Vector) {
        vectores.add(vector)
        vector.infectadoPor(this)
    }

    fun infectarSiCorresponde(vector: Vector) {
        if(!vector.contieneMutacionSupresion(this)) {
            infectaA(vector)
        }else{
            throw InfectionRejectedException("No se pudo infectar al vector")
        }
    }

    fun infectarSegunProbabilidad(vectorContagiado : Vector, vectorInfector : Vector) {
        val porcentajeDeContagioExitoso = RandomContagio.random() + capacidadDeContagioSegun(vectorContagiado.tipo)
        if (porcentajeDeContagioExitoso > RandomContagio.randomPosibilidad()){
            infectarSegunDefensa(vectorContagiado, vectorInfector)
        }
    }

    fun agregarMutacion(mutacion: Mutacion) {
        this.mutaciones.add(mutacion)
        mutacion.especie = this
    }
}