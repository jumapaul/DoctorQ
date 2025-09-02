package com.doctorq.userservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = new ClassPathResource("client_secret.json").getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("doctorq-q.firebasestorage.app")
                    .build();

            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    public Storage firebaseStorage() throws IOException {
        InputStream serviceAccount = new ClassPathResource("client_secret.json").getInputStream();

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount);

        return StorageOptions.newBuilder().setCredentials(googleCredentials).build().getService();
    }
}
