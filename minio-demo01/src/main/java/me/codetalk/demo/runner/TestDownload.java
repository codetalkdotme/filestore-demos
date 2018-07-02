package me.codetalk.demo.runner;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * Created by guobiao.xu on 2018/7/2.
 */
@Component
public class TestDownload implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUpload.class);

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.upload.bucket}")
    private String minioUploadBucket;

    @Value("${minio.upload.subdir}")
    private String subDir;

    @Override
    public void run(String... strings) throws Exception {
        // 列出test-upload/audio下的文件
        Iterable<Result<Item>> iterable = minioClient.listObjects(minioUploadBucket, subDir);
        for (Result<Item> rs : iterable) {
            Item item = rs.get();

            Long ts1 = System.currentTimeMillis();
            String obj = item.objectName(); // objectName = audio/007.wav

            // check parent folder
            File outFile = new File("out/" + obj), outParent = outFile.getParentFile();
            if(!outParent.exists()) outParent.mkdirs();

            try(InputStream in = minioClient.getObject(minioUploadBucket, obj);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {
                IOUtils.copy(in, out);
            }

            Long ts2 = System.currentTimeMillis();

            LOGGER.info("Download {}, cost = {}ms", obj, (ts2 - ts1));
        }
    }

//    me.codetalk.demo.runner.TestUpload       : Download 007.wav, cost = 8ms
//    me.codetalk.demo.runner.TestUpload       : Download 008.wav, cost = 7ms
//    me.codetalk.demo.runner.TestUpload       : Download 01.wav, cost = 7ms
//    me.codetalk.demo.runner.TestUpload       : Download test.wav, cost = 7ms

}
