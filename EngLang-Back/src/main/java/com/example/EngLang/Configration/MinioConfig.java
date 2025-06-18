package com.example.EngLang.Configration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String minioUrl;


    @Value("${minio.access-key}")
    private String accesskey;

    @Value("${minio.secret-key}")
    private String  secretkey;

    @Value("${minio.bucket-name}")
    private String  bucketname;

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accesskey,secretkey)
                .build();
    }
}
