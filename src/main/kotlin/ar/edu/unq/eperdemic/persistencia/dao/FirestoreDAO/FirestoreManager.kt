package ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions

object FirestoreManager {

  private lateinit var firestore: Firestore

  // Id proyecto 2 : principesdeepersia
  // ID proyecto 1 Main : epersia-532d2

  fun getFirestore(): Firestore {
    if (!::firestore.isInitialized) {
      firestore = FirestoreOptions.newBuilder()
        .setProjectId("principesdeepersia")
        .build()
        .service
    }
    return firestore
  }
}