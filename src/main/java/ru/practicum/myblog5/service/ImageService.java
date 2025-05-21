package ru.practicum.myblog5.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String save(MultipartFile file);

    void delete(String imageName);

    Resource get(String imageName);
}
