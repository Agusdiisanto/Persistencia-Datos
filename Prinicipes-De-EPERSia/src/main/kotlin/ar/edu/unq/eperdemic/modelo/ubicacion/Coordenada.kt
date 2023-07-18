package ar.edu.unq.eperdemic.modelo.ubicacion

import ar.edu.unq.eperdemic.modelo.exceptions.OutOfRangeCoordinatesException

class Coordenada(latitud: Double, longitud: Double) {
    var latitud : Double? = latitud
    var longitud : Double? = longitud

    init{
        validarCoordenada()
    }

    private fun validarCoordenada() {
        if(longitud!! !in (-180.0..180.0) || latitud!! !in (-90.0..90.0)){
            throw OutOfRangeCoordinatesException("Los valores ingresados para la coordenada son invalidos.")
        }
    }
}
