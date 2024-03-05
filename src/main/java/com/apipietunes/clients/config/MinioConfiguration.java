package com.apipietunes.clients.config;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

@Configuration
public class MinioConfiguration {
    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.buckets.tracks}")
    public String TRACKS_BUCKET;

    @Value("${minio.buckets.covers}")
    public String COVERS_BUCKET;

    @Bean
    MinioClient minioClient() throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {
        var client = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();

        boolean isTracksBucketExist = client.bucketExists(BucketExistsArgs.builder().bucket(TRACKS_BUCKET).build());
        boolean isCoversBucketExist = client.bucketExists(BucketExistsArgs.builder().bucket(COVERS_BUCKET).build());

        if (!isTracksBucketExist) {
            client.makeBucket(MakeBucketArgs.builder().bucket(TRACKS_BUCKET).build());
        }

        if (!isCoversBucketExist) {
            client.makeBucket(MakeBucketArgs.builder().bucket(COVERS_BUCKET).build());
        }


        return client;
    }
}
