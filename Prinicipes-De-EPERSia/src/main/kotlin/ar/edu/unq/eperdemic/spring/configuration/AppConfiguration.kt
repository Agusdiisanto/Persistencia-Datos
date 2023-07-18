package ar.edu.unq.eperdemic.spring.configuration

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream


@Configuration
class AppConfiguration {

    @Bean
    fun groupName() : String {
        return "Principes de EPERSia"
    }

}

@Configuration
class FirestoreConfig {

    @Bean
    fun firestore(@Value("\${spring.cloud.gcp.firestore.credentials.location}")credentials : String): Firestore {
        val serviceAccount = FileInputStream(credentials)

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }

        return FirestoreClient.getFirestore()
    }

}
