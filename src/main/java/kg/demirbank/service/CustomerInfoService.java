package kg.demirbank.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public interface CustomerInfoService {

    void savePersonPhoto(MultipartFile file, String personalNumber, String universityCode);

    void savePersonPhoto(MultipartFile[] photos, String universityCode);

    void pathFolder(Path folder, String fileName, BufferedImage src) throws IOException;

    Optional<Resource> servePersonPhoto(String universityCode, String personalNumber, Boolean defPhoto);

    Optional<Resource> serveUniversityLogo(String universityCode);

    void saveUniversityLogo(MultipartFile logo, String universityCode);

    Resource serveNoPhotoAvailable() throws IOException;
}
