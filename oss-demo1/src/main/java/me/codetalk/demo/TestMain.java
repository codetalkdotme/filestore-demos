package me.codetalk.demo;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guobiao.xu on 2018/8/3.
 */
@SpringBootApplication
public class TestMain implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMain.class);

    @Value("${oss.endpoint}")
    private String ossEndpoint;

    @Value("${oss.bucket}")
    private String ossBucket;

    @Value("${oss.accessKeyId}")
    private String ossAccessKeyId;

    @Value("${oss.accessKeySecret}")
    private String ossAccessKeySecret;

    @Autowired
    private OSSClient ossClient;

    public static void main(String[] args) {
        SpringApplication.run(TestMain.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
//        showBucketInfo();
        putFile(new File("sample/bluerose.jpeg"));
//        preSignedUrl();
    }

    private void showBucketInfo() {
        long start = System.currentTimeMillis();

        BucketInfo info = ossClient.getBucketInfo(ossBucket);
        LOGGER.info("Bucket " + ossBucket + "的信息如下：");
        LOGGER.info("\t数据中心：" + info.getBucket().getLocation());
        LOGGER.info("\t创建时间：" + info.getBucket().getCreationDate());
        LOGGER.info("\t用户标志：" + info.getBucket().getOwner());

        LOGGER.info("getBucketInfo, cost = {}ms", System.currentTimeMillis() - start);
    }

    private void putFile(File file) throws Exception {
        long start = System.currentTimeMillis();

//        Map<String, String> fileMeta = new HashMap<>();
//        fileMeta.put("file_name", "bluerose.jpeg");
//        fileMeta.put("file_length", "132908");
//        ObjectMetadata objMeta = new ObjectMetadata();
//        objMeta.setUserMetadata(fileMeta);

        URL presignedUrl = ossClient.generatePresignedUrl(ossBucket, "bluerose.jpeg",
                new Date(System.currentTimeMillis() + 3600 * 1000));
        LOGGER.info("url original = {}", presignedUrl.toString());

        Thread.sleep(30 * 1000);

        String fileKey = file.getName();
        ossClient.putObject(ossBucket, fileKey, file);

        LOGGER.info("putObject, cost = {}ms", System.currentTimeMillis() - start);
        LOGGER.info("object access url = {}.{}/{}", ossBucket, ossEndpoint, fileKey);
    }

    private void preSignedUrl() {
        URL presignedUrl = ossClient.generatePresignedUrl(ossBucket, "1403.jpg",
                new Date(System.currentTimeMillis() + 3600 * 1000));
        URL presignedThumb = ossClient.generatePresignedUrl(ossBucket, "1403.jpg-m_lfit_h_90_w_160",
                new Date(8000, 11, 31));
        LOGGER.info("url original = {}", presignedUrl.toString());
        LOGGER.info("url with style = {}", presignedThumb.toString());
    }

    @Bean
    public OSSClient createOssClient() {
        OSSClient client = new OSSClient(ossEndpoint, ossAccessKeyId, ossAccessKeySecret);

        return client;
    }

}
