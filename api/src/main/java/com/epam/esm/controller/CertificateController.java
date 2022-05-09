package com.epam.esm.controller;

import com.epam.esm.exception.CustomException;
import com.epam.esm.pagination.Pagination;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Rest controller represent CRUD operation on the GiftCertificate
 */
@RestController
@RequestMapping(value = "/certificates")
public class CertificateController {

    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String PREVIOUS_PAGE = "previousPage";
    private static final String NEXT_PAGE = "nextPage";
    private CertificateService service;

    @Autowired
    public CertificateController(CertificateService service) {
        this.service = service;
    }

    /**
     * Method to get GiftCertificate with tags as CertificateDto by id.
     *
     * @param id GiftCertificate id
     * @return GiftCertificateDto
     * @throws CustomException - if GiftCertificate was not found or id has not valid value;
     */
    @GetMapping(value = "/{id}")
    public CertificateDto findCertificate(@PathVariable("id") long id) throws CustomException {
        CertificateDto certificate = service.findById(id);
        addSelfLink(certificate);
        return certificate;
    }

    /**
     * Method to get pagination GiftCertificate list with tags as CertificateDto list
     *
     * @param page - page
     * @param size - page size
     * @return CollectionModel consist of list of GiftCertificateDto or empty list if was not found anyone GiftCertificate
     * and links to previous and nex pages.
     * @throws CustomException - if page or size has not valid value;
     */
    @GetMapping(params = {"page", "size"})
    public CollectionModel<CertificateDto> findAllCertificates(@RequestParam("page") Integer page,
                                                               @RequestParam("size") Integer size)
            throws CustomException {
        long quantity = service.count();
        Pagination.check(page, size, quantity);
        List<CertificateDto> certificates = service.findAll(page, size);
        for (CertificateDto certificate : certificates) {
            addSelfLink(certificate);
        }
        Link listSelfLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(page, size))
                .withSelfRel();
        int previousPage = Pagination.previousPage(page);
        Link previous = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(previousPage, size))
                .withRel(PREVIOUS_PAGE);
        int nextPage = Pagination.nextPage(page, size, quantity);
        Link next = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(nextPage, size))
                .withRel(NEXT_PAGE);
        return CollectionModel.of(certificates, listSelfLink, previous, next);
    }

    /**
     * Method to delete GiftCertificate by id
     *
     * @param id GiftCertificate id
     * @throws CustomException - if id has not valid value or GiftCertificate by id not found;
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificate(@PathVariable("id") long id) throws CustomException {
        service.delete(id);
    }

    /**
     * Method to create new GiftCertificate with tags.
     * If such tag have not existed yet, it will be created.
     *
     * @param dto - new GiftCertificate as CertificateDto
     * @return CertificateDto contains new GiftCertificate.
     * @throws CustomException if CertificateDto has not valid value of fields
     *                         or if GiftCertificate with such value of fields already exist;
     */
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateDto createCertificate(@RequestBody CertificateDto dto)
            throws CustomException {
        CertificateDto certificate = service.create(dto);
        addSelfLink(certificate);
        return certificate;
    }

    /**
     * Method to update GiftCertificate with tags.
     * If such tag have not existed yet, it will be created.
     *
     * @param id  GiftCertificate id
     * @param dto CertificateDto consists field which must be updated.
     * @return CertificateDto contains updated GiftCertificate.
     * @throws CustomException if GiftCertificateDto has not valid value of fields or GiftCertificate by id not found;
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CertificateDto updateCertificate(@PathVariable("id") long id,
                                            @RequestBody CertificateDto dto)
            throws CustomException {
        CertificateDto certificate = service.update(id, dto);
        addSelfLink(certificate);
        return certificate;
    }

    /**
     * Method to get pagination GiftCertificate list with tags as GiftCertificateDto list by parameters.
     *
     * @param page  - page
     * @param size  - page size
     * @param param - parameters map
     *              Available parameters : tag (tag name), name (part of certificate name),
     *              description (part of certificate description), sort_by (sorting type).
     * @return CollectionModel consist of GiftCertificateDto list or empty list if was not found anyone GiftCertificate
     * and links to previous and nex pages.
     * @throws CustomException if parameters map has not valid value;
     */
    @GetMapping(value = "/search")
    public CollectionModel<CertificateDto> findAllCertificatesByParameters(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam Map<String, String> param)
            throws CustomException {
        Map<String, String> parameters = new HashMap<>(param);
        parameters.remove(PAGE);
        parameters.remove(SIZE);
        long quantity = service.countByParameters(parameters);
        Pagination.check(page, size, quantity);
        List<CertificateDto> certificates = service.findAllByParameters(parameters, page, size);
        for (CertificateDto certificate : certificates) {
            addSelfLink(certificate);
        }
        Link listSelfLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(page, size, parameters))
                .withSelfRel();
        int previousPage = Pagination.previousPage(page);
        Link previous = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(previousPage, size, parameters))
                .withRel(PREVIOUS_PAGE);
        int nextPage = Pagination.nextPage(page, size, quantity);
        Link next = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(nextPage, size, parameters))
                .withRel(NEXT_PAGE);
        return CollectionModel.of(certificates, listSelfLink, previous, next);
    }

    /**
     * Method to get pagination GiftCertificate list with tags as GiftCertificateDto list by tags.
     *
     * @param page - page
     * @param size - page size
     * @return CollectionModel consist of list of GiftCertificateDto or empty list if was not found anyone GiftCertificate
     * and links to previous and nex pages.
     * @throws CustomException if the tag array has not valid value or page or size has not valid data;
     */
    @GetMapping(params = {"tags", "page", "size"})
    public CollectionModel<CertificateDto> findAllCertificatesByTags(@RequestParam("page") Integer page,
                                                                     @RequestParam("size") Integer size,
                                                                     @RequestParam("tags") String[] tags)
            throws CustomException {
        long quantity = service.countByTags(tags);
        Pagination.check(page, size, quantity);
        List<CertificateDto> certificates = service.findByTags(tags, page, size);
        for (CertificateDto certificate : certificates) {
            addSelfLink(certificate);
        }
        Link listSelfLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(page, size, tags))
                .withSelfRel();
        int previousPage = Pagination.previousPage(page);
        Link previous = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(previousPage, size, tags))
                .withRel(PREVIOUS_PAGE);
        int nextPage = Pagination.nextPage(page, size, quantity);
        Link next = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(nextPage, size, tags))
                .withRel(NEXT_PAGE);
        return CollectionModel.of(certificates, listSelfLink, previous, next);
    }

    private void addSelfLink(CertificateDto certificate) throws CustomException {
        Link selfLink = linkTo(methodOn(CertificateController.class)
                .findCertificate(certificate.getId())).withSelfRel();
        certificate.add(selfLink);
        Set<TagDto> tags = certificate.getTags();
        if (tags == null) {
            return;
        }
        for (TagDto tag : tags) {
            Link tagSelfLink = linkTo(methodOn(TagController.class)
                    .findTag(tag.getId())).withSelfRel();
            String[] tagName = new String[]{tag.getName()};
            Link findByTagLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificatesByTags(1, 10, tagName)).withRel("certificatesByTag");
            tag.add(tagSelfLink, findByTagLink);
        }
    }
}
