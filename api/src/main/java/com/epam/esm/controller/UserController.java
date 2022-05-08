package com.epam.esm.controller;

import com.epam.esm.exception.CustomException;
import com.epam.esm.pagination.Pagination;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
 * Rest controller represent operation on User and their Orders
 */
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;
    private OrderService orderService;

    @Autowired
    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    /**
     * Method to get User as UserDto by id
     *
     * @param id User id
     * @return User as UserDto
     * @throws CustomException - if User was not found or id has not valid value;
     */
    @GetMapping(value = "/{id}")
    public UserDto findUser(@PathVariable("id") long id) throws CustomException {
        UserDto user = userService.findById(id);
        addSelfLink(user);
        return user;
    }

    /**
     * Method to get pagination Orders list by User as OrderDto list
     *
     * @param id   User id
     * @param page page
     * @param size page size
     * @return CollectionModel consist of list of OrderDto or empty list if was not found anyone Order
     * and links to previous and nex pages.
     * @throws CustomException - if id or page or size has not valid value;
     */
    @GetMapping(value = "/{id}/orders")
    public CollectionModel<OrderDto> findOrders(@PathVariable("id") long id,
                                                @RequestParam("page") Integer page,
                                                @RequestParam("size") Integer size) throws CustomException {
        long quantity = orderService.countByUser(id);
        Pagination.check(page, size, quantity);
        List<OrderDto> orders = orderService.findAllByUser(id, page, size);
        for (OrderDto order : orders) {
            addOrderLink(id, order);
        }
        int previousPage = Pagination.previousPage(page);
        Link previous = linkTo(methodOn(UserController.class).findOrders(id, previousPage, size)).withRel("previousPage");
        int nextPage = Pagination.nextPage(page, size, quantity);
        Link next = linkTo(methodOn(UserController.class).findOrders(id, nextPage, size)).withRel("nextPage");
        Link listSelfLink = linkTo(methodOn(UserController.class).findOrders(id, page, size)).withSelfRel();
        return CollectionModel.of(orders, listSelfLink, previous, next);
    }

    /**
     * Method to get Order as OrderDto by User
     *
     * @param id      User id
     * @param orderId Order id
     * @return Order as OrderDto
     * @throws CustomException - if Order was not found or id or orderId has not valid value;
     */
    @GetMapping(value = "/{id}/orders/{orderId}")
    public OrderDto findOrder(@PathVariable("id") long id, @PathVariable("orderId") long orderId) throws CustomException {
        OrderDto order = orderService.findByIdAndByUser(orderId, id);
        addOrderLink(id, order);
        return order;
    }

    /**
     * Method to create new Order by User
     *
     * @param id           User id
     * @param certificates certificate list
     * @return new Order as OrderDto
     * @throws CustomException - if certificate list or id has not valid value;
     */
    @PostMapping(value = "/{id}/orders/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@PathVariable("id") long id,
                                @RequestBody List<CertificateDto> certificates) throws CustomException {
        OrderDto order = orderService.create(id, certificates);
        addOrderLink(id, order);
        return order;
    }

    private void addSelfLink(UserDto user) throws CustomException {
        Link selfLink = linkTo(methodOn(UserController.class).findUser(user.getId())).withSelfRel();
        Link ordersLink = linkTo(methodOn(UserController.class).findOrders(user.getId(), 1, 10))
                .withRel("orders");
        user.add(selfLink, ordersLink);
    }

    private void addOrderLink(long userId, OrderDto order) throws CustomException {
        Link orderLink = linkTo(methodOn(UserController.class).findOrder(userId, order.getId())).withRel("order");
        order.add(orderLink);
    }
}
