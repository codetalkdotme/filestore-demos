package me.codetalk.demo.runner;

import io.minio.MinioClient;
import io.minio.messages.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sun.net.www.content.audio.wav;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guobiao.xu on 2018/7/2.
 */
@Component
public class TestUpload implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUpload.class);

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.upload.bucket}")
    private String minioUploadBucket;

    @Override
    public void run(String... strings) throws Exception {
        File[] files = new File("sample/").listFiles();
        for(File file : files) {
            Long ts1 = System.currentTimeMillis();

            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
                Map<String, String> meta = new HashMap<>();

                minioClient.putObject(minioUploadBucket, file.getName(), in, "audio/wav");

            }

            Long ts2 = System.currentTimeMillis();

            LOGGER.info("Upload {}, cost = {}ms", file.getName(), (ts2 - ts1));
        }
    }

//    me.codetalk.demo.runner.TestUpload       : Upload 007.wav, cost = 16ms
//    me.codetalk.demo.runner.TestUpload       : Upload 008.wav, cost = 12ms
//    me.codetalk.demo.runner.TestUpload       : Upload 01.wav, cost = 18ms
//    me.codetalk.demo.runner.TestUpload       : Upload test.wav, cost = 14ms

}
