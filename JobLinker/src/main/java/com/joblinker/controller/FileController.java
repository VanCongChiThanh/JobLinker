package com.joblinker.controller;

import com.joblinker.domain.response.ResUploadFileDTO;
import com.joblinker.service.FileService;
import com.joblinker.util.error.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder
    ) throws IOException, StorageException {
        if(file == null || file.isEmpty()){
            throw new StorageException("File cannot be empty");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item ->
                fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extension. Supported extensions are: " + allowedExtensions);
        }
        String uploadFileUrl = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFileUrl, Instant.now());
        return ResponseEntity.ok().body(res);
    }
}