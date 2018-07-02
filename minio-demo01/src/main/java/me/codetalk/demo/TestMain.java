package me.codetalk.demo;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by guobiao.xu on 2018/7/2.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "me.codetalk.demo.runner",
})
public class TestMain {

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.access.key}")
    private String minioAccessKey;

    @Value("${minio.secret.key}")
    private String minioSecretKey;

    @Value("${minio.connect.timeout}")
    private Integer minioConnTimeout;

    @Value("${minio.read.timeout}")
    private Integer minioReadTimeout;

    @Value("${minio.write.timeout}")
    private Integer minioWriteTimeout;

    public static void main(String[] args) {
        SpringApplication.run(TestMain.class, args);
    }

    @Bean
    public MinioClient createMinioClient() throws Exception {
        MinioClient client = new MinioClient(minioEndpoint, minioAccessKey, minioSecretKey);
        client.setTimeout(minioConnTimeout, minioWriteTimeout, minioReadTimeout);

        return client;
    }

}
