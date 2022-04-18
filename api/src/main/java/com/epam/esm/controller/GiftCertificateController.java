package com.epam.esm.controller;

import com.epam.esm.exception.CustomException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.dto.GiftCertificateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epam.esm.exception.CustomErrorCode.RESOURCE_NOT_FOUND;

/**
 * Rest controller represent CRUD operation on the GiftCertificate
 */
@RestController
@RequestMapping(value = "/gift-certificates")
public class GiftCertificateController {

    private GiftCertificateService giftCertificateService;

    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }

    /**
     * Method to get GiftCertificate with CustomTags as GiftCertificateDto by id
     * @param id GiftCertificate id
     * @return GiftCertificateDto
     * @throws CustomException - if GiftCertificate was not found or id has not valid value;
     */
    @GetMapping(value = "/{id}")
    public GiftCertificateDto findGiftCertificate(@PathVariable("id") long id) throws CustomException {
        Optional<GiftCertificateDto> certificate = giftCertificateService.findById(id);
        return certificate
                .orElseThrow(() -> new CustomException("Requested resource not found (id=" + id + ")",
                        RESOURCE_NOT_FOUND));
    }

    /**
     * Method to get all GiftCertificate with CustomTags as GiftCertificateDto
     * @return list of GiftCertificateDto or empty list if was not found anyone GiftCertificate
     */
    @GetMapping
    public List<GiftCertificateDto> findAllGiftCertificates() {
        return giftCertificateService.findAll();
    }

    /**
     * Method to delete GiftCertificate by id
     * @param id GiftCertificate id
     * @throws CustomException - if id has not valid value;
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGiftCertificate(@PathVariable("id") long id) throws CustomException {
        giftCertificateService.delete(id);
    }

    /**
     * Method to create new GiftCertificate with CustomTags.
     * If such CustomTag have not existed yet, it will be created.
     * @param giftCertificateDto - new GiftCertificate with CustomTags as GiftCertificateDto
     * @param ucb default UriComponentsBuilder.
     * @return ResponseEntity contains new GiftCertificateDto, header with new gift certificate location, HttpStatus.
     * @throws CustomException if GiftCertificateDto has not valid value of fields;
     */
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GiftCertificateDto> createGiftCertificate(@RequestBody GiftCertificateDto giftCertificateDto,
                                                                    UriComponentsBuilder ucb) throws CustomException {
        GiftCertificateDto dto = giftCertificateService.create(giftCertificateDto);
        long id = dto.getDtoId();

        HttpHeaders headers = new HttpHeaders();
        URI locationUri = ucb.path("/gift-certificates/")
                .path(String.valueOf(id))
                .build()
                .toUri();
        headers.setLocation(locationUri);

        ResponseEntity<GiftCertificateDto> responseEntity = new ResponseEntity<>(dto, headers, HttpStatus.CREATED);
        return responseEntity;
    }

    /**
     * Method to partly update GiftCertificate with CustomTags.
     * If such CustomTag have not existed yet, it will be created.
     * @param id GiftCertificate
     * @param giftCertificateDto GiftCertificateDto consists field which must be updated.
     * @return ResponseEntity contains GiftCertificateDto of updated GiftCertificate,
     * header with new gift certificate location, HttpStatus.
     * @throws CustomException if GiftCertificateDto has not valid value of fields or GiftCertificate by id not found;
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GiftCertificateDto updateGiftCertificate(@PathVariable("id") long id,
                                                    @RequestBody GiftCertificateDto giftCertificateDto)
            throws CustomException {
        return giftCertificateService.update(id, giftCertificateDto);

    }

    /**
     * Method to get all GiftCertificate with CustomTags as GiftCertificateDto by parameters
     * @param parameters search parameters
     * @return list of GiftCertificateDto or empty list if was not found anyone GiftCertificate
     */
    @GetMapping(value = "/")
    public List<GiftCertificateDto> findAllGiftCertificatesByParameters(@RequestParam Map<String, String> parameters)
            throws CustomException {
        return giftCertificateService.findAllByParameters(parameters);
    }
}
