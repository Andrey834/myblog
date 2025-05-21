package ru.practicum.myblog5.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.myblog5.service.ImageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    @Value("${app.image.dir:images}")
    private Path pathDir;

    @Override
    public String save(MultipartFile file) {
        try {
            if (!Files.exists(this.pathDir)) {
                Files.createDirectory(this.pathDir);
            }

            String originalFilename = file.getOriginalFilename();
            String suffix;

            if(originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                suffix = ".jpg";
            }

            String fileName = UUID.randomUUID() + suffix;
            Path path = pathDir.resolve(fileName);

            file.transferTo(path);

            return fileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("Image not saved", e);
        }
    }

    @Override
    public void delete(String imageName) {
        try {
            Files.deleteIfExists(this.pathDir.resolve(imageName));
        } catch (IOException e) {
            throw new IllegalArgumentException("Image not deleted", e);
        }
    }

    @Override
    public Resource get(String imageName) {
        try {
            return new UrlResource(pathDir.resolve(imageName).toUri());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Image not found", e);
        }
    }
}
