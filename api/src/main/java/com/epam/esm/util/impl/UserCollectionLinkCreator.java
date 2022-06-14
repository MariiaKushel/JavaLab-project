package com.epam.esm.util.impl;

import com.epam.esm.controller.UserController;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.util.CollectionLinkCreator;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserCollectionLinkCreator extends CollectionLinkCreator {

    @Override
    public List<Link> createLinks(List<OrderDto> orders, long userId, int page, int size, int lastPage)
            throws CustomException {
        for (OrderDto order : orders) {
            order.add(linkTo(methodOn(UserController.class)
                    .findOrderByCurrentUser(null, order.getId())).withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        Link firstPageLink = linkTo(methodOn(UserController.class)
                .findOrdersByCurrentUser(null, 1, size))
                .withRel(FIRST_PAGE);
        links.add(firstPageLink);
        if (page > 1 && page <= lastPage) {
            Link previousPageLink = linkTo(methodOn(UserController.class)
                    .findOrdersByCurrentUser(null, page - 1, size))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(UserController.class)
                .findOrdersByCurrentUser(null, page, size))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (page < lastPage) {
            Link nexPageLink = linkTo(methodOn(UserController.class)
                    .findOrdersByCurrentUser(null, page + 1, size))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link lastPageLink = linkTo(methodOn(UserController.class)
                .findOrdersByCurrentUser(null, lastPage, size))
                .withRel(LAST_PAGE);
        links.add(lastPageLink);
        Link createOrderLink = linkTo(methodOn(UserController.class).createOrder(null, null))
                .withRel(CREATE_ORDER);
        links.add(createOrderLink);
        Link userLink = linkTo(methodOn(UserController.class).findUser(null)).withRel(USER);
        links.add(userLink);
        return links;
    }
}
