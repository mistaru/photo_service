package kg.demirbank.controller;

import kg.demirbank.service.CustomerInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campus/photo")
public class CampusPhotoController {

    private final CustomerInfoService customerInfoService;

    //*******************//
    // UPLOAD OPERATIONS //
    //*******************//

    @PostMapping("/upload/single")
    public ResponseEntity<Void> uploadSinglePhoto(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("personalNumber") String personalNumber,
                                                  @RequestParam("universityCode") String universityCode)
    {
        customerInfoService.savePersonPhoto(file, personalNumber, universityCode);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/upload/multiple", method = RequestMethod.POST)
    public ResponseEntity<Void> uploadMultiplePhotos(@RequestParam("photos") MultipartFile[] photos,
                                                     @RequestParam(value = "universityCode") String universityCode)
    {
        customerInfoService.savePersonPhoto(photos, universityCode);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/university/logo", method = RequestMethod.POST)
    public ResponseEntity<Void> uploadUniversityLogo(@RequestParam("logo") MultipartFile logo,
                                                     @RequestParam(value = "universityCode") String universityCode)
    {

        customerInfoService.saveUniversityLogo(logo, universityCode);
        return ResponseEntity.noContent().build();
    }

    //*********************//
    // DOWNLOAD OPERATIONS //
    //*********************//

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Resource> getPersonPhoto(
            @RequestParam(value = "personalNumber") String personalNumber,
            @RequestParam(value = "universityCode") String universityCode,
            @RequestParam(value = "defPhoto", required = false, defaultValue = "0") Boolean defaultPhoto)
            throws IOException
    {
        Optional<Resource> optResource = customerInfoService.servePersonPhoto(universityCode, personalNumber, defaultPhoto);
        if(!optResource.isPresent())
            return ResponseEntity.notFound().build(); // 404

        Resource resource = optResource.get();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @RequestMapping(value = "/university/logo",method = RequestMethod.GET)
    public ResponseEntity<Resource> getUniversityLogo(@RequestParam(value = "universityCode") String universityCode)
            throws IOException
    {
        Optional<Resource> optResource = customerInfoService.serveUniversityLogo(universityCode);
        if(!optResource.isPresent())
            return ResponseEntity.notFound().build(); // 404

        Resource resource = optResource.get();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @RequestMapping(value = "/no_photo_available", method = RequestMethod.GET)
    public ResponseEntity<Resource> getNoPhotoAvailable() throws IOException {

        Resource resource = customerInfoService.serveNoPhotoAvailable();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
