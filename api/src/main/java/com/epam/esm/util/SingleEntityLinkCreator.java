package com.epam.esm.util;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.TagController;
import com.epam.esm.enumeration.SortingType;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.dto.UserDto;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Abstract class represent common methods for single entity link creation.
 */
public abstract class SingleEntityLinkCreator implements LinkCreator {
    /**
     * Method create links by single TagDto.
     *
     * @param tag TagDto
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public List<Link> createLinks(TagDto tag) throws CustomException {
        Link selfLink = linkTo(methodOn(TagController.class).findTag(null, tag.getId())).withSelfRel();
        Link findAllLink = linkTo(methodOn(TagController.class).findAllTags(null, 1, 10)).withRel(FIND_ALL);
        Link findTheMostWidelyTagLink = linkTo(methodOn(TagController.class).findTheMostWidelyTag(null))
                .withRel(FIND_THE_MOST_WIDELY_TAG);
        return List.of(selfLink, findAllLink, findTheMostWidelyTagLink);
    }

    /**
     * Method create links by single UserDto.
     *
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public abstract List<Link> createLinks(UserDto user) throws CustomException;

    /**
     * Method create links by single OrderDto.
     *
     * @param order OrderDto
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public abstract List<Link> createLinks(OrderDto order, Long userId) throws CustomException;

    /**
     * Method create links by single CertificateDto.
     *
     * @param certificate CertificateDto
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public List<Link> createLinks(CertificateDto certificate) throws CustomException {
        for (TagDto tag : certificate.getTags()) {
            tag.add(linkTo(methodOn(TagController.class).findTag(null, tag.getId())).withSelfRel());
        }
        Link selfLink = linkTo(methodOn(CertificateController.class).findCertificate(null, certificate.getId()))
                .withSelfRel();
        Link findAllLink = linkTo(methodOn(CertificateController.class).findAllCertificates(null, 1, 10))
                .withRel(FIND_ALL);
        String tagName = certificate.getTags().stream()
                .findFirst()
                .map(TagDto::getName)
                .orElse(null);
        Link searchLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(null, 1, 10, tagName, certificate.getName(),
                        certificate.getDescription(), SortingType.NAME_ASC.getType()))
                .withRel(SEARCH);
        String[] tags = new String[certificate.getTags().size()];
        List<String> tagNames = certificate.getTags().stream().map(TagDto::getName).toList();
        tags = tagNames.toArray(tags);
        Link findByTagsLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(null, 1, 10, tags))
                .withRel(FIND_BY_TAGS);
        return List.of(selfLink, findAllLink, searchLink, findByTagsLink);
    }

}
