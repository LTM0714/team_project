package com.myarea.myarea.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AWS_S3Service {
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public List<String> uploadFile(Long postId, List<MultipartFile> multipartFiles){
        List<String> fileNameList = new ArrayList<>();

        for (MultipartFile f : multipartFiles) {
            String safeName = (f.getOriginalFilename() == null ? "file" : f.getOriginalFilename()).replaceAll("\\s+","_");
            String key = "posts/%d/%s_%s".formatted(postId, java.util.UUID.randomUUID(), safeName);

            var meta = new com.amazonaws.services.s3.model.ObjectMetadata();
            meta.setContentLength(f.getSize());
            meta.setContentType(f.getContentType());

            try (var in = f.getInputStream()) {
                var put = new com.amazonaws.services.s3.model.PutObjectRequest(bucket, key, in, meta);
                // 공개 버킷로 운영한다면: put.withCannedAcl(CannedAccessControlList.PublicRead);
                amazonS3.putObject(put);
            } catch (Exception e) {
                throw new RuntimeException("S3 upload failed: " + safeName, e);
            }
            fileNameList.add(key);
        }
        return fileNameList;
    }

    // 파일명을 난수화하기 위해 UUID 를 활용하여 난수를 돌린다.
    public String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    //  "."의 존재 유무만 판단
    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
        }
    }


    public void deleteFiles(List<String> keys){
        for(String key : keys){
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
        }
    }
}
