package com.epam.esm.controller;

import com.epam.esm.enumeration.SearchParameterName;
import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.util.impl.AdminCollectionLinkCreator;
import com.epam.esm.util.impl.AdminSingleEntityLinkCreator;
import com.epam.esm.util.impl.CommonCollectionLinkCreator;
import com.epam.esm.util.impl.CommonSingleEntityLinkCreator;
import com.nimbusds.jose.shaded.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

/**
 * Rest controller represent CRUD operation on the GiftCertificate
 */
@RestController
@RequestMapping(value = "/certificates")
public class CertificateController {

    private static final String ROLE_CLAIM_KEY = "authorities";

    private CertificateService service;
    private AdminSingleEntityLinkCreator adminSingleEntityLinkCreator;
    private CommonSingleEntityLinkCreator commonSingleEntityLinkCreator;
    private AdminCollectionLinkCreator adminCollectionLinkCreator;
    private CommonCollectionLinkCreator commonCollectionLinkCreator;

    @Autowired
    public CertificateController(CertificateService service,
                                 AdminSingleEntityLinkCreator adminSingleEntityLinkCreator,
                                 CommonSingleEntityLinkCreator commonSingleEntityLinkCreator,
                                 AdminCollectionLinkCreator adminCollectionLinkCreator,
                                 CommonCollectionLinkCreator commonCollectionLinkCreator) {
        this.service = service;
        this.adminSingleEntityLinkCreator = adminSingleEntityLinkCreator;
        this.commonSingleEntityLinkCreator = commonSingleEntityLinkCreator;
        this.adminCollectionLinkCreator = adminCollectionLinkCreator;
        this.commonCollectionLinkCreator = commonCollectionLinkCreator;
    }

    /**
     * Method to get GiftCertificate with tags as CertificateDto by id.
     *
     * @param jwt access token
     * @param id GiftCertificate id
     * @return GiftCertificateDto
     * @throws CustomException - if GiftCertificate was not found or id has not valid value;
     */
    @GetMapping(value = "/{id}")
    public CertificateDto findCertificate(@AuthenticationPrincipal Jwt jwt,
                                          @PathVariable("id") long id) throws CustomException {
        CertificateDto certificate = service.findById(id);
        List<Link> links = (jwt != null && ((JSONArray)jwt.getClaim(ROLE_CLAIM_KEY)).get(0).equals(UserRole.ROLE_ADMIN.name()))
                ? adminSingleEntityLinkCreator.createLinks(certificate)
                : commonSingleEntityLinkCreator.createLinks(certificate);
        return certificate.add(links);
    }

    /**
     * Method to get pagination GiftCertificate list with tags as CertificateDto list
     *
     * @param jwt access token
     * @param page - page
     * @param size - page size
     * @return CollectionModel consist of list of GiftCertificateDto or empty list if was not found anyone GiftCertificate
     * and links to previous and nex pages.
     * @throws CustomException - if page or size has not valid value;
     */
    @GetMapping
    public CollectionModel<CertificateDto> findAllCertificates(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size)
            throws CustomException {
        List<CertificateDto> certificates = service.findAll(page, size);
        int lastPage = service.findAllLastPage(size);
        List<Link> links = (jwt != null && ((JSONArray)jwt.getClaim(ROLE_CLAIM_KEY)).get(0).equals(UserRole.ROLE_ADMIN.name()))
                ? adminCollectionLinkCreator.createLinksCertificates(certificates, page, size, lastPage)
                : commonCollectionLinkCreator.createLinksCertificates(certificates, page, size, lastPage);
        return CollectionModel.of(certificates, links);
    }

    /**
     * Method to delete GiftCertificate by id
     *
     * @param id GiftCertificate id
     * @return no content ResponseEntity
     * @throws CustomException - if id has not valid value or GiftCertificate by id not found;
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteCertificate(@PathVariable("id") long id) throws CustomException {
        service.delete(id);
        return ResponseEntity.noContent().build();
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateDto createCertificate(@RequestBody CertificateDto dto)
            throws CustomException {
        CertificateDto certificate = service.create(dto);
        List<Link> links = adminSingleEntityLinkCreator.createLinks(certificate);
        return certificate.add(links);
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CertificateDto updateCertificate(@PathVariable("id") long id,
                                            @RequestBody CertificateDto dto)
            throws CustomException {
        CertificateDto certificate = service.update(id, dto);
        List<Link> links = adminSingleEntityLinkCreator.createLinks(certificate);
        return certificate.add(links);
    }

    /**
     * Method to get pagination GiftCertificate list with tags as GiftCertificateDto list by parameters.
     *
     * @param page   - page
     * @param size   - page size
     * @param tag    tag name
     * @param name   part of certificate name
     * @param description part of certificate description
     * @param sortBy sorting type.
     * @return CollectionModel consist of GiftCertificateDto list or empty list if was not found anyone GiftCertificate
     * and links to previous and nex pages.
     * @throws CustomException if parameters map has not valid value;
     */
    @GetMapping(value = "/search")
    public CollectionModel<CertificateDto> findAllCertificatesByParameters(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "sort_by", defaultValue = "date.asc", required = false) String sortBy
    ) throws CustomException {
        Map<SearchParameterName, String> parameters = collectParamToMap(tag, name, description, sortBy);
        List<CertificateDto> certificates = service.findAllByParameters(parameters, page, size);
        int lastPage = service.findAllByParametersLastPage(parameters, size);
        List<Link> links = (jwt != null && ((JSONArray)jwt.getClaim(ROLE_CLAIM_KEY)).get(0).equals(UserRole.ROLE_ADMIN.name()))
                ? adminCollectionLinkCreator.createLinksCertificates(certificates, tag, name, description,
                sortBy, page, size, lastPage)
                : commonCollectionLinkCreator.createLinksCertificates(certificates, tag, name, description,
                sortBy, page, size, lastPage);
        return CollectionModel.of(certificates, links);
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
    @GetMapping(params = "tags")
    public CollectionModel<CertificateDto> findAllCertificatesByTags(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "tags") String[] tags)
            throws CustomException {
        List<CertificateDto> certificates = service.findAllByTags(tags, page, size);
        int lastPage = service.findAllByTagsLastPage(tags, size);
        List<Link> links = (jwt != null && ((JSONArray)jwt.getClaim(ROLE_CLAIM_KEY)).get(0).equals(UserRole.ROLE_ADMIN.name()))
                ? adminCollectionLinkCreator.createLinksCertificates(certificates, tags, page, size, lastPage)
                : commonCollectionLinkCreator.createLinksCertificates(certificates, tags, page, size, lastPage);
        return CollectionModel.of(certificates, links);
    }

    private Map<SearchParameterName, String> collectParamToMap(String tag, String name, String description, String sortBy) {
        Map<SearchParameterName, String> param = new HashMap<>();
        if (tag != null) param.put(SearchParameterName.TAG, tag);
        if (name != null) param.put(SearchParameterName.NAME, name);
        if (description != null) param.put(SearchParameterName.DESCRIPTION, description);
        param.put(SearchParameterName.ACTIVE, "true");
        param.put(SearchParameterName.SORT_BY, sortBy);
        return param;
    }
}
