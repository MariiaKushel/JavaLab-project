package com.epam.esm.util.impl;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.TagController;
import com.epam.esm.controller.UserController;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.util.CollectionLinkCreator;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AdminCollectionLinkCreator extends CollectionLinkCreator {

    @Override
    public List<Link> createLinks(List<TagDto> tags,
                                  int page,
                                  int size,
                                  int lastPage) throws CustomException {
        List<Link> links = new ArrayList<>(super.createLinks(tags, page, size, lastPage));
        Link createLink = linkTo(methodOn(TagController.class).createTag(null)).withRel(CREATE);
        links.add(createLink);
        return links;
    }

    @Override
    public List<Link> createLinks(List<OrderDto> orders,
                                  long userId,
                                  int page,
                                  int size,
                                  int lastPage) throws CustomException {
        for (OrderDto order : orders) {
            order.add(linkTo(methodOn(UserController.class).findOrderByUser(userId, order.getId())).withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        Link firstPageLink = linkTo(methodOn(UserController.class).findOrdersByUser(userId, 1, size))
                .withRel(FIRST_PAGE);
        links.add(firstPageLink);
        if (page > 1 && page <= lastPage) {
            Link previousPageLink = linkTo(methodOn(UserController.class)
                    .findOrdersByUser(userId, page - 1, size))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(UserController.class).findOrdersByUser(userId, page, size))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (page < lastPage) {
            Link nexPageLink = linkTo(methodOn(UserController.class).findOrdersByUser(userId, page + 1, size))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link lastPageLink = linkTo(methodOn(UserController.class).findOrdersByUser(userId, lastPage, size))
                .withRel(LAST_PAGE);
        links.add(lastPageLink);
        Link adminLink = linkTo(methodOn(UserController.class).findUser(null)).withRel(USER);
        links.add(adminLink);
        return links;
    }

    @Override
    public List<Link> createLinksCertificates(List<CertificateDto> certificates,
                                              int page,
                                              int size,
                                              int lastPage) throws CustomException {
        List<Link> links = new ArrayList<>(super.createLinksCertificates(certificates, page, size, lastPage));
        Link createLink = linkTo(methodOn(CertificateController.class).createCertificate(null)).withRel(CREATE);
        links.add(createLink);
        return links;
    }

    @Override
    public List<Link> createLinksCertificates(List<CertificateDto> certificates,
                                              String[] tags,
                                              int page,
                                              int size,
                                              int lastPage) throws CustomException {
        List<Link> links = new ArrayList<>(super.createLinksCertificates(certificates, tags, page, size, lastPage));
        Link createLink = linkTo(methodOn(CertificateController.class).createCertificate(null)).withRel(CREATE);
        links.add(createLink);
        return links;
    }

    @Override
    public List<Link> createLinksCertificates(List<CertificateDto> certificates,
                                              String tag,
                                              String name,
                                              String description,
                                              String sortBy,
                                              int page,
                                              int size,
                                              int lastPage) throws CustomException {
        List<Link> links = new ArrayList<>(super.createLinksCertificates(certificates, tag, name, description, sortBy,
                page, size, lastPage));
        Link createLink = linkTo(methodOn(CertificateController.class).createCertificate(null)).withRel(CREATE);
        links.add(createLink);
        return links;
    }
}
