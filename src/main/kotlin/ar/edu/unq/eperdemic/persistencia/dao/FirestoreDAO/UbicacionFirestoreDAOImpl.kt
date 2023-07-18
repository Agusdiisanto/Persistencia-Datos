package ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO

import ar.edu.unq.eperdemic.modelo.ubicacion.UbicacionFire
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import org.springframework.stereotype.Repository

@Repository
class UbicacionFirestoreDAOImpl  : FirestoreRepository<UbicacionFire>(), UbicacionFirestoreDAO {

    override fun getFirestore(): Firestore = FirestoreManager.getFirestore()

    override fun getCollectionName(): String {
        return "ubicacion"
    }
    override fun getEntityType(): Class<UbicacionFire> = UbicacionFire::class.java

    override fun actualizarVectores(ubicacionOrigenId: String, ubicacionDestinoId: String) {
        val transaction = getFirestore().runTransaction { transaction ->
            val fromRef = getCollectionReference().document(ubicacionOrigenId)
            val toRef= getCollectionReference().document(ubicacionDestinoId)

            val fromSnapshot = transaction.get(fromRef).get().data
            val toSnapshot = transaction.get(toRef).get().data

            val vectoresOrigen = fromSnapshot?.get("cantidadVectores") as? Long ?: 0
            val vectoresDestino = toSnapshot?.get("cantidadVectores") as? Long ?: 0

            if (vectoresOrigen < 1) {
                throw RuntimeException("El vector ya no se encuentra en la ubicacion de origen")
            }

            val updatedFromVectors = vectoresOrigen - 1
            val updatedToVectors = vectoresDestino + 1

            transaction.update(fromRef, "cantidadVectores", updatedFromVectors)
            transaction.update(fromRef, "alerta", UbicacionFire.alertaPara(updatedFromVectors))
            transaction.update(toRef, "cantidadVectores", updatedToVectors)
            transaction.update(toRef, "alerta", UbicacionFire.alertaPara(updatedToVectors))

            null
        }

        try {
            transaction.get()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun incrementarVectoresEn(nombre: String) {
        val querySnapshot = getCollectionReference().whereEqualTo("nombre", nombre).limit(1).get().get()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents[0]
            val locationRef = documentSnapshot.reference

            locationRef.update("cantidadVectores", FieldValue.increment(1))
        }else {
            throw RuntimeException("No existe la ubicacion")
        }
    }

    override fun saveOrUpdate(ubicacion: UbicacionFire): UbicacionFire {
        return if (ubicacion.id.isNullOrEmpty()) {
            save(ubicacion)
        } else {
            update(ubicacion.id!!, ubicacion)
        }
    }

    override fun findByName(nombre: String): UbicacionFire {
        val querySnapshot = getFirestore().collection(getCollectionName())
            .whereEqualTo("nombre", nombre)
            .limit(1)
            .get()
            .get()
        if (querySnapshot.isEmpty) {
            throw NotFoundException("No se encontró la ubicación con nombre: $nombre")
        }
        return querySnapshot.documents[0].toObject(UbicacionFire::class.java)
    }

    override fun nombresDeUbicacionesConAlerta(alerta : String): List<String> {
        val querySnapshot = getFirestore().collection(getCollectionName())
            .whereEqualTo("alerta", alerta)
            .select("nombre")
            .get()
            .get()
        if (querySnapshot.isEmpty) {
            return emptyList()
        }
        return querySnapshot.documents.mapNotNull { document ->
            document.getString("nombre")
        }
    }
}
