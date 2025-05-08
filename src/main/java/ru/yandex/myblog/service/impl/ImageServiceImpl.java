package ru.yandex.myblog.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.myblog.service.ImageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    @Value("${app.image.dir}")
    private Path pathDir;

    @Override
    public String save(MultipartFile file) {
        try {
            if (!Files.exists(this.pathDir)) {
                Files.createDirectory(this.pathDir);
            }

            String suffix = file.getOriginalFilename() != null
                    ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))
                    : ".jpg";

            String fileName = UUID.randomUUID() + suffix;
            Path path = pathDir.resolve(fileName);

            file.transferTo(path);

             return fileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String imageName) {
        try {
            Files.delete(pathDir.resolve(imageName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource get(String imageName) {
        try {
            return new UrlResource(pathDir.resolve(imageName).toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
