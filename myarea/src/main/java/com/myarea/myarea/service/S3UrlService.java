package com.myarea.myarea.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3UrlService {
    @Value("${cloud.aws.s3.bucket}") private String bucket;
    @Value("${cloud.aws.region.static}") private String region;

    private final com.amazonaws.services.s3.AmazonS3 s3;

    /** key -> 외부에서 바로 접근 가능한 URL */
    public String toViewUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    public String cloudfront(String key) {
        return "https://d340lgnzbiyzns.cloudfront.net/" + key;
    }
}
