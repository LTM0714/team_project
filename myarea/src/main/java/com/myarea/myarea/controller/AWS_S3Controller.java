package com.myarea.myarea.controller;

import com.myarea.myarea.service.AWS_S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class AWS_S3Controller {
    private final AWS_S3Service aws_s3Service;
}
