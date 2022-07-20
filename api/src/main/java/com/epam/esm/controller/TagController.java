package com.epam.esm.controller;

import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.TagDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller represent CRD operation on the CustomTag
 */
@RestController
@RequestMapping(value = "/tags")
public class TagController {

    private static final String ROLE_CLAIM_KEY = "authorities";

    private TagService service;
    private AdminSingleEntityLinkCreator adminSingleEntityLinkCreator;
    private CommonSingleEntityLinkCreator commonSingleEntityLinkCreator;
    private AdminCollectionLinkCreator adminCollectionLinkCreator;
    private CommonCollectionLinkCreator commonCollectionLinkCreator;

    @Autowired
    public TagController(TagService service,
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
     * Method to get CustomTag as TagDto by id
     *
     * @param jwt access token
     * @param id CustomTag id
     * @return CustomTag as TgDto
     * @throws CustomException - if CustomTag was not found or id has not valid value;
     */
    @GetMapping(value = "/{id}")
    public TagDto findTag(@AuthenticationPrincipal Jwt jwt,
                          @PathVariable("id") long id) throws CustomException {
        TagDto tag = service.findById(id);
        List<Link> links = getSingleEntityLinksByRole(jwt, tag);
        return tag.add(links);
    }

    /**
     * Method to get pagination CustomTag list as TagDto list
     *
     * @param jwt access token
     * @param page - page
     * @param size - page size
     * @return CollectionModel consist of list of CustomTag or empty list if was not found anyone CustomTag
     * and links to previous and nex pages.
     * @throws CustomException - if page or size has not valid value;
     */
    @GetMapping
    public CollectionModel<TagDto> findAllTags(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size)
            throws CustomException {
        List<TagDto> tags = service.findAll(page, size);
        int lastPage = service.findAllLastPage(size);
        List<Link> links = (jwt != null && ((JSONArray)jwt.getClaim(ROLE_CLAIM_KEY)).get(0).equals(UserRole.ROLE_ADMIN.name()))
                ? adminCollectionLinkCreator.createLinks(tags, page, size, lastPage)
                : commonCollectionLinkCreator.createLinks(tags, page, size, lastPage);
        return CollectionModel.of(tags, links);
    }


    /**
     * Method to delete CustomTag by id
     *
     * @param id CustomTag id
     * @return no content ResponseEntity
     * @throws CustomException if id has not valid value;
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteTag(@PathVariable("id") long id) throws CustomException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Method to create new CustomTag
     *
     * @param tag TagDto consist name of new CustomTag
     * @return TagDto as new CustomTag
     * @throws CustomException - if CustomTag with that name already exist or id has not valid value;
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto createTag(@RequestBody TagDto tag) throws CustomException {
        TagDto newTag = service.create(tag);
        List<Link> links = adminSingleEntityLinkCreator.createLinks(newTag);
        return newTag.add(links);
    }

    /**
     * Method to get the most widely used tag of a user with the highest cost of all orders.
     *
     * @param jwt access token
     * @return TagDto as the most widely used tag
     */
    @GetMapping(value = "/the-most-widely")
    public TagDto findTheMostWidelyTag(@AuthenticationPrincipal Jwt jwt) throws CustomException {
        TagDto tag = service.findTheMostWidelyTag();
        List<Link> links = getSingleEntityLinksByRole(jwt, tag);
        return tag.add(links);
    }

    private List<Link> getSingleEntityLinksByRole(Jwt jwt, TagDto tag) throws CustomException {
        return (jwt != null && ((JSONArray)jwt.getClaim(ROLE_CLAIM_KEY)).get(0).equals(UserRole.ROLE_ADMIN.name()))
                ? adminSingleEntityLinkCreator.createLinks(tag)
                : commonSingleEntityLinkCreator.createLinks(tag);
    }
}
