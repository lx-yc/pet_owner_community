package com.example.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    private static final String ENDPOINT = "https://oss-cn-guangzhou.aliyuncs.com";
    private static final String ACCESS_KEY_ID = "";
    private static final String ACCESS_KEY_SECRET = "";
    private static final String BUCKET_NAME = "pet-owner-community";

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    public String getBucketName() {
        return BUCKET_NAME;
    }

    public String getEndpoint() {
        return ENDPOINT;
    }
}
