package com.epam.esm.util.impl;

import com.epam.esm.controller.AdminController;
import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.TagController;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.util.SingleEntityLinkCreator;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AdminSingleEntityLinkCreator extends SingleEntityLinkCreator {

    @Override
    public List<Link> createLinks(TagDto tag) throws CustomException {
        List<Link> links = new ArrayList<>(super.createLinks(tag));
        Link deleteLink = linkTo(methodOn(TagController.class).deleteTag(tag.getId())).withRel(DELETE);
        Link createLink = linkTo(methodOn(TagController.class).createTag(null)).withRel(CREATE);
        links.add(deleteLink);
        links.add(createLink);
        return links;
    }

    @Override
    public List<Link> createLinks(UserDto user) throws CustomException {
        Link selfLink = linkTo(methodOn(AdminController.class).findAdmin("")).withSelfRel();
        Link ordersLink = linkTo(methodOn(AdminController.class).findOrdersByUser(1L, 1, 10))
                .withRel(ORDERS);
        return List.of(selfLink, ordersLink);
    }

    @Override
    public List<Link> createLinks(OrderDto order, Long userId) throws CustomException {
        Link selfLink = linkTo(methodOn(AdminController.class).findOrderByUser(userId, order.getId())).withSelfRel();
        Link ordersLink = linkTo(methodOn(AdminController.class).findOrdersByUser(userId, 1, 10))
                .withRel(ORDERS);
        Link adminLink = linkTo(methodOn(AdminController.class).findAdmin("")).withRel(ADMIN);
        return List.of(selfLink, ordersLink, adminLink);
    }

    @Override
    public List<Link> createLinks(CertificateDto certificate) throws CustomException {
        List<Link> links = new ArrayList<>(super.createLinks(certificate));
        Link updateLink = linkTo(methodOn(CertificateController.class).updateCertificate(certificate.getId(), null))
                .withRel(UPDATE);
        Link deleteLink = linkTo(methodOn(CertificateController.class).deleteCertificate(certificate.getId()))
                .withRel(DELETE);
        Link createLink = linkTo(methodOn(CertificateController.class).createCertificate(null))
                .withRel(CREATE);
        links.add(updateLink);
        links.add(deleteLink);
        links.add(createLink);
        return links;
    }
}
