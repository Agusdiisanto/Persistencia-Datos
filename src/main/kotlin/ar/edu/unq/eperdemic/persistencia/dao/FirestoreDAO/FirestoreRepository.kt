package ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO
import com.google.cloud.firestore.*


abstract class FirestoreRepository<T : Any>{
    abstract fun getCollectionName(): String

    abstract fun getEntityType(): Class<T>
    open fun getFirestore(): Firestore = FirestoreManager.getFirestore()

    fun getCollectionReference(): CollectionReference =
        getFirestore().collection(getCollectionName())

    fun save(entity: T): T {
        val documentReference = getCollectionReference().document()
        val entityId = documentReference.id

        val entityClass = entity.javaClass
        val idField = entityClass.getDeclaredField("id")
        idField.isAccessible = true

        idField.set(entity, entityId)

        documentReference.set(entity).get()
        return entity

    }

    fun update(id: String, entity: T): T {
        val documentReference = getCollectionReference().document(id)
        documentReference.set(entity, SetOptions.merge()).get()
        return entity
    }

    fun findAll(): List<T> {
        val querySnapshot: QuerySnapshot = getCollectionReference().get().get()
        return querySnapshot.documents.mapNotNull { it.toObject(getEntityType()) }
    }

    fun findById(id: String): T? {
        val documentSnapshot = getCollectionReference().document(id).get().get()
        return documentSnapshot.toObject(getEntityType())
    }

    fun deleteById(id: String) {
        getCollectionReference().document(id).delete()
    }

    fun deleteAll() {
        val batch = getFirestore().batch()
        val querySnapshot = getCollectionReference().get().get()
        for (document in querySnapshot) {
            batch.delete(document.reference)
        }
        batch.commit().get()
    }
}
