package kg.demirbank.service.impl;

import kg.demirbank.exception.BusinessException;
import kg.demirbank.service.CustomerInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerInfoServiceImpl implements CustomerInfoService {

    @Value("${photo.folder}")
    private String photoFolder;

    @Value("${photo.folder.logo}")
    private String logoFolder;

    @Value("${photo.no.photo.avail}")
    private String noPhotoAvailable;

    @Override
    public void savePersonPhoto(MultipartFile file, String personalNumber, String universityCode) {
        try {
            BufferedImage src = ImageIO.read(file.getInputStream());
            String fileName = personalNumber.toLowerCase() + ".jpg";
            Path folder = Files.createDirectories(Paths.get(photoFolder, universityCode));
            pathFolder(folder, fileName, src);

        } catch (IOException ex) {
            throw new BusinessException("Cannot save person photo", ex);
        }
    }

    @Override
    public void savePersonPhoto(MultipartFile[] photos, String universityCode) {
        try {
            if (Objects.nonNull(photos) && photos.length > 0) {
                Path folder = Files.createDirectories(Paths.get(photoFolder, universityCode));
                for (MultipartFile multipartFile : photos) {

                    try {
                        String fileName = multipartFile.getOriginalFilename();
                        if (fileName == null) continue;

                        BufferedImage src = ImageIO.read(new ByteArrayInputStream(multipartFile.getBytes()));
                        pathFolder(folder, fileName, src);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void pathFolder(Path folder, String fileName, BufferedImage src) throws IOException {
        Path fullPath = folder.resolve(fileName);
        File destination;
        if (Files.exists(fullPath)) {
            destination = fullPath.toFile();
        } else {
            destination = Files.createFile(folder.resolve(fileName)).toFile();
        }
        ImageIO.write(src, "jpg", destination);
    }

    public Optional<Resource> serveUniversityLogo(String universityCode) {
        Path path = Paths.get(logoFolder,universityCode + ".jpg");
        if(!Files.exists(path))
            return Optional.empty();

        return Optional.of(new FileSystemResource(path));
    }

    public Optional<Resource> servePersonPhoto(String universityCode, String personalNumber, Boolean defPhoto) {
        // If photo exist, then hand it over
        Path path = Paths.get(photoFolder, universityCode, personalNumber.toLowerCase() + ".jpg");
        if(Files.exists(path))
            return Optional.of(new FileSystemResource(path));

        // If photo isn't found and default(replacement) photo is not required, then stop
        if(!defPhoto)
            return Optional.empty();

        // If default(replacement) photo is required then, first, try to return logo of the university
        // If logo isn't there then return 'no photo available' photo
        path = Paths.get(logoFolder, universityCode + ".jpg");
        if (Files.exists(path))
            return Optional.of(new FileSystemResource(path));

        return Optional.of(new FileSystemResource(Paths.get(noPhotoAvailable)));
    }

    @Override
    public void saveUniversityLogo(MultipartFile logo, String universityCode) {
        try {
            BufferedImage src = ImageIO.read(logo.getInputStream());
            String fileName = universityCode + ".jpg";
            Path fullPath = Files.createDirectories(Paths.get(logoFolder)).resolve(fileName);

            pathFolder(fullPath, fileName, src);

        } catch (IOException ex) {
            throw new BusinessException("Cannot save person photo", ex);
        }
    }

    @Override
    public Resource serveNoPhotoAvailable() throws IOException {
        Path path = Paths.get(noPhotoAvailable);
        return new ByteArrayResource(Files.readAllBytes(path));
    }

}
