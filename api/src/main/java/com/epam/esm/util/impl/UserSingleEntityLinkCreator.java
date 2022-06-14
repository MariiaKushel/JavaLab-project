package com.epam.esm.util.impl;

import com.epam.esm.controller.UserController;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.util.SingleEntityLinkCreator;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserSingleEntityLinkCreator extends SingleEntityLinkCreator {

    @Override
    public List<Link> createLinks(UserDto user) throws CustomException {
        Link selfLink = linkTo(methodOn(UserController.class).findUser(null)).withSelfRel();
        Link ordersLink = linkTo(methodOn(UserController.class).findOrdersByCurrentUser(null, 1, 10))
                .withRel(ORDERS);
        Link createOrderLink = linkTo(methodOn(UserController.class).createOrder(null, null))
                .withRel(CREATE_ORDER);
        return List.of(selfLink, ordersLink, createOrderLink);
    }

    @Override
    public List<Link> createLinks(OrderDto order, Long userId) throws CustomException {
        Link selfLink = linkTo(methodOn(UserController.class).findOrderByCurrentUser(null, order.getId())).withSelfRel();
        Link ordersLink = linkTo(methodOn(UserController.class).findOrdersByCurrentUser(null, 1, 10))
                .withRel(ORDERS);
        Link createOrderLink = linkTo(methodOn(UserController.class).createOrder(null, null))
                .withRel(CREATE_ORDER);
        Link userLink = linkTo(methodOn(UserController.class).findUser(null)).withRel(USER);
        return List.of(selfLink, ordersLink, createOrderLink, userLink);
    }
}
