package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito


interface DistritoService {
    fun distritoMasEnfermo(): Distrito

    // Metodos CRUD :

    fun crear(distrito:Distrito): Distrito

    fun recuperar(nombre : String) : Distrito

    fun recuperarTodos() : List<Distrito>
}