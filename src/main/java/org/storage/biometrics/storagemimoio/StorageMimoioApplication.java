package org.storage.biometrics.storagemimoio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StorageMimoioApplication {



    public static void main(String[] args) {
        SpringApplication.run(StorageMimoioApplication.class, args);
    }

}
