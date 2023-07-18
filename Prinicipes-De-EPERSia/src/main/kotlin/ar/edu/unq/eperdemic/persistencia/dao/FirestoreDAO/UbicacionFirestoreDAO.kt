package ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO

import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionFire

interface UbicacionFirestoreDAO{
    fun save(entity: UbicacionFire): UbicacionFire

    fun update(id: String, entity: UbicacionFire): UbicacionFire

    fun actualizarVectores(ubicacionOrigenId: String, ubicacionDestinoId: String)

    fun incrementarVectoresEn(nombre: String)

    fun saveOrUpdate(ubicacion: UbicacionFire): UbicacionFire

    fun findByName(nombre: String): UbicacionFire

    fun findById(id: String): UbicacionFire?

    fun deleteById(id: String)

    fun nombresDeUbicacionesConAlerta(alerta : String): List<String>

    fun findAll(): List<UbicacionFire>

    fun deleteAll()


}