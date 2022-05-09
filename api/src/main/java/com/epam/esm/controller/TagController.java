package com.epam.esm.controller;

import com.epam.esm.exception.CustomException;
import com.epam.esm.pagination.Pagination;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Rest controller represent CRD operation on the CustomTag
 */
@RestController
@RequestMapping(value = "/tags")
public class TagController {

    private TagService service;

    @Autowired
    public TagController(TagService service) {
        this.service = service;
    }

    /**
     * Method to get CustomTag as TagDto by id
     *
     * @param id CustomTag id
     * @return CustomTag as TgDto
     * @throws CustomException - if CustomTag was not found or id has not valid value;
     */
    @GetMapping(value = "/{id}")
    public TagDto findTag(@PathVariable("id") long id) throws CustomException {
        TagDto tag = service.findById(id);
        addSelfLink(tag);
        return tag;
    }

    /**
     * Method to get pagination CustomTag list as TagDto list
     *
     * @param page - page
     * @param size - page size
     * @return CollectionModel consist of list of CustomTag or empty list if was not found anyone CustomTag
     * and links to previous and nex pages.
     * @throws CustomException - if page or size has not valid value;
     */
    @GetMapping
    public CollectionModel<TagDto> findAllTags(@RequestParam("page") Integer page,
                                               @RequestParam("size") Integer size)
            throws CustomException {

        long quantity = service.count();
        Pagination.check(page, size, quantity);
        List<TagDto> tags = service.findAll(page, size);
        for (TagDto tag : tags) {
            addSelfLink(tag);
        }
        int previousPage = Pagination.previousPage(page);
        Link previous = linkTo(methodOn(TagController.class).findAllTags(previousPage, size)).withRel("previousPage");
        int nextPage = Pagination.nextPage(page, size, quantity);
        Link next = linkTo(methodOn(TagController.class).findAllTags(nextPage, size)).withRel("nextPage");
        Link listSelfLink = linkTo(methodOn(TagController.class).findAllTags(page, size)).withSelfRel();
        return CollectionModel.of(tags, listSelfLink, previous, next);
    }


    /**
     * Method to delete CustomTag by id
     *
     * @param id CustomTag id
     * @throws CustomException if id has not valid value;
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable("id") long id) throws CustomException {
        service.delete(id);
    }

    /**
     * Method to create new CustomTag
     *
     * @param tag TagDto consist name of new CustomTag
     * @return TagDto as new CustomTag
     * @throws CustomException - if CustomTag with that name already exist or id has not valid value;
     */
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto createCustomTag(@RequestBody TagDto tag) throws CustomException {
        TagDto newTag = service.create(tag);
        addSelfLink(newTag);
        return newTag;
    }

    /**
     * Method to get the most widely used tag of a user with the highest cost of all orders.
     *
     * @return TagDto as the most widely used tag
     */
    @GetMapping(value = "/the-most-widely")
    public TagDto findTheMostWidelyTag() throws CustomException {
        TagDto tag = service.findTheMostWidelyTag();
        addSelfLink(tag);
        return tag;
    }

    private void addSelfLink(TagDto tag) throws CustomException {
        Link selfLink = linkTo(methodOn(TagController.class).findTag(tag.getId())).withSelfRel();
        tag.add(selfLink);
    }
}
