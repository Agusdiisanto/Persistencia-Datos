package ar.edu.unq.eperdemic.modelo.ubicacion

import com.google.cloud.firestore.annotation.DocumentId

data class UbicacionFire(val nombre: String, var cantidadVectores : Int = 0, var latitud: Double, var longitud: Double) {

    var alerta: String = ""

    @DocumentId
    var id: String? = null

    constructor() : this("", 0, 0.0, 0.0)

    init {
        cambioDeAlerta()
    }

    fun cambioDeAlerta() {
        alerta = when {
            cantidadVectores > 15 -> Alerta.Rojo.name
            cantidadVectores in 7..15 -> Alerta.Amarillo.name
            else -> Alerta.Verde.name
        }
    }

    fun incrementarContadorVectores() {
      cantidadVectores++
      cambioDeAlerta()
    }

    fun decrementarContadorVectores() {
        cantidadVectores--
        cambioDeAlerta()

    }

    companion object {
        fun alertaPara(cantidad: Long): String {
            return when {
                cantidad > 15 -> Alerta.Rojo.name
                cantidad in 7..15 -> Alerta.Amarillo.name
                else -> Alerta.Verde.name
            }
        }
    }


}
