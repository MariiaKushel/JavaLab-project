package com.epam.esm.util;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.TagController;
import com.epam.esm.enumeration.SortingType;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.TagDto;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Abstract class represent common methods for collection link creation.
 */
public abstract class CollectionLinkCreator implements LinkCreator {

    /**
     * Method create links with pagination by list TagDto. Also, it adds selfLink to each tag of list.
     *
     * @param tags list TagDto
     * @param page page
     * @param size page size
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public List<Link> createLinks(List<TagDto> tags,
                                  int page,
                                  int size,
                                  int lastPage) throws CustomException {
        for (TagDto tag : tags) {
            tag.add(linkTo(methodOn(TagController.class).findTag(null, tag.getId())).withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        Link firstPageLink = linkTo(methodOn(TagController.class).findAllTags(null, 1, size))
                .withRel(FIRST_PAGE);
        links.add(firstPageLink);
        if (page > 1 && page <= lastPage) {
            Link previousPageLink = linkTo(methodOn(TagController.class).findAllTags(null, page - 1, size))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(TagController.class).findAllTags(null, page, size))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (page < lastPage) {
            Link nexPageLink = linkTo(methodOn(TagController.class).findAllTags(null, page + 1, size))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link lastPageLink = linkTo(methodOn(TagController.class).findAllTags(null, lastPage, size))
                .withRel(LAST_PAGE);
        links.add(lastPageLink);
        Link findTheMostWidelyTagLink = linkTo(methodOn(TagController.class).findTheMostWidelyTag(null))
                .withRel(FIND_THE_MOST_WIDELY_TAG);
        links.add(findTheMostWidelyTagLink);
        return links;
    }

    /**
     * Method create links with pagination by list OrderDto. Also, it adds selfLink to each order of list.
     *
     * @param orders list OrderDto
     * @param userId user id
     * @param page   page
     * @param size   page size
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public abstract List<Link> createLinks(List<OrderDto> orders,
                                           long userId,
                                           int page,
                                           int size,
                                           int lastPage) throws CustomException;

    /**
     * Method create links with pagination by list CertificateDto. Also, it adds selfLink to each certificate of list.
     *
     * @param certificates list CertificateDto
     * @param page         page
     * @param size         page size
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public List<Link> createLinksCertificates(List<CertificateDto> certificates,
                                              int page,
                                              int size,
                                              int lastPage) throws CustomException {
        for (CertificateDto certificate : certificates) {
            certificate.add(linkTo(methodOn(CertificateController.class).findCertificate(null, certificate.getId()))
                    .withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        Link firstPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(null, 1, size))
                .withRel(FIRST_PAGE);
        links.add(firstPageLink);
        if (page > 1 && page <= lastPage) {
            Link previousPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificates(null, page - 1, size))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(null, page, size))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (page < lastPage) {
            Link nexPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificates(null, page + 1, size))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link lastPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(null, lastPage, size))
                .withRel(LAST_PAGE);
        links.add(lastPageLink);
        Link searchLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(null, 1, 10, "tag_name", "name_part",
                        "description_part", SortingType.DATE_ASC.getType()))
                .withRel(SEARCH);
        links.add(searchLink);
        String[] tags = new String[]{"tag_name_1", "tag_name_2"};
        Link findByTagsLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(null, 1, 10, tags))
                .withRel(FIND_BY_TAGS);
        links.add(findByTagsLink);
        return links;
    }

    /**
     * Method create links with pagination by list CertificateDto. Also, it adds selfLink to each certificate of list.
     *
     * @param certificates list CertificateDto
     * @param tags         tags name array
     * @param page         page
     * @param size         page size
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public List<Link> createLinksCertificates(List<CertificateDto> certificates,
                                              String[] tags,
                                              int page,
                                              int size,
                                              int lastPage) throws CustomException {
        for (CertificateDto certificate : certificates) {
            certificate.add(linkTo(methodOn(CertificateController.class).findCertificate(null, certificate.getId()))
                    .withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        Link firstPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(null, 1, size, tags))
                .withRel(FIRST_PAGE);
        links.add(firstPageLink);
        if (page > 1 && page <= lastPage) {
            Link previousPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificatesByTags(null, 1, size, tags))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(null, 1, size, tags))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (page < lastPage) {
            Link nexPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificatesByTags(null, 1, size, tags))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link lastPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(null, 1, size, tags))
                .withRel(LAST_PAGE);
        links.add(lastPageLink);
        Link searchLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(null, 1, 10, "tag_name", "name_part",
                        "description_part", SortingType.DATE_ASC.getType()))
                .withRel(SEARCH);
        links.add(searchLink);
        Link findAllLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(null, 1, 10))
                .withRel(FIND_ALL);
        links.add(findAllLink);
        return links;
    }

    /**
     * Method create links with pagination by list CertificateDto. Also, it adds selfLink to each certificate of list.
     *
     * @param certificates list CertificateDto
     * @param tag          tag name
     * @param name         part of certificate name
     * @param description  part of certificate description
     * @param sortBy       sorting type
     * @param page         page
     * @param size         page size
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public List<Link> createLinksCertificates(List<CertificateDto> certificates,
                                              String tag,
                                              String name,
                                              String description,
                                              String sortBy,
                                              int page,
                                              int size,
                                              int lastPage) throws CustomException {
        for (CertificateDto certificate : certificates) {
            certificate.add(linkTo(methodOn(CertificateController.class).findCertificate(null, certificate.getId()))
                    .withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        Link firstPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(null, 1, size, tag, name, description, sortBy))
                .withRel(FIRST_PAGE);
        links.add(firstPageLink);
        if (page > 1 && page <= lastPage) {
            Link previousPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificatesByParameters(null, page - 1, size, tag, name, description, sortBy))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(null, page, size, tag, name, description, sortBy))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (page < lastPage) {
            Link nexPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificatesByParameters(null, page + 1, size, tag, name, description, sortBy))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link lastPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(null, lastPage, size, tag, name, description, sortBy))
                .withRel(LAST_PAGE);
        links.add(lastPageLink);
        Link findAllLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(null, 1, 10))
                .withRel(FIND_ALL);
        links.add(findAllLink);
        String[] tags = new String[]{"tag_name_1", "tag_name_2"};
        Link findByTagsLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(null, 1, 10, tags))
                .withRel(FIND_BY_TAGS);
        links.add(findByTagsLink);
        return links;
    }
}
