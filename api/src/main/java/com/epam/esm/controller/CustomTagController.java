package com.epam.esm.controller;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CustomTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.exception.CustomErrorCode.RESOURCE_NOT_FOUND;

/**
 * Rest controller represent CRD operation on the CustomTag
 */
@RestController
@RequestMapping(value = "/custom-tags")
public class CustomTagController {

    private CustomTagService service;

    @Autowired
    public CustomTagController(CustomTagService tagService) {
        this.service = tagService;
    }

    /**
     * Method to get CustomTag by id
     * @param id CustomTag id
     * @return customTag
     * @throws CustomException - if CustomTag was not found or id has not valid value;
     */
    @GetMapping(value = "/{id}")
    public CustomTag findCustomTag(@PathVariable("id") long id) throws CustomException {
        Optional<CustomTag> customTag = service.findById(id);
        return customTag
                .orElseThrow(() -> new CustomException("Requested resource not found (id=" + id + ")", RESOURCE_NOT_FOUND));
    }

    /**
     * Method to get all CustomTags
     * @return list of CustomTags or empty list if was not found anyone CustomTag
     */
    @GetMapping
    public List<CustomTag> findAllCustomTag() {
        return service.findAll();
    }

    /**
     * Method to delete CustomTag by id
     * @param id CustomTag id
     * @throws CustomException if id has not valid value;
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomTag(@PathVariable("id") long id) throws CustomException {
        service.delete(id);
    }

    /**
     * Method to create new CustomTag
     * @param tag new CustomTag entity contains new tag name
     * @param ucb default UriComponentsBuilder.
     * @return ResponseEntity contains new CustomTag, header with new tag location, HttpStatus.
     * @throws CustomException - if CustomTag with that name already exist or id has not valid value;
     */
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomTag> createCustomTag(@RequestBody CustomTag tag,
                                                     UriComponentsBuilder ucb) throws CustomException {
        CustomTag newTag = service.create(tag);
        long id = newTag.getId();

        HttpHeaders headers = new HttpHeaders();
        URI locationUri = ucb.path("/custom-tags/")
                .path(String.valueOf(id))
                .build()
                .toUri();
        headers.setLocation(locationUri);

        ResponseEntity<CustomTag> responseEntity = new ResponseEntity<CustomTag>(
                newTag, headers, HttpStatus.CREATED);
        return responseEntity;
    }
}
