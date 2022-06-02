package com.epam.esm.controller;

import com.epam.esm.exception.CustomException;
import com.epam.esm.properties.JwtProperty;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.util.JwtDecoder;
import com.epam.esm.util.impl.AdminCollectionLinkCreator;
import com.epam.esm.util.impl.AdminSingleEntityLinkCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller represent operation on User and their Orders
 */
@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    private UserService userService;
    private OrderService orderService;
    private AdminSingleEntityLinkCreator singleEntityLinkCreator;
    private AdminCollectionLinkCreator collectionLinkCreator;
    private JwtProperty jwtProperty;

    @Autowired
    public AdminController(UserService userService, OrderService orderService,
                           AdminSingleEntityLinkCreator singleEntityLinkCreator,
                           AdminCollectionLinkCreator collectionLinkCreator, JwtProperty jwtProperty) {
        this.userService = userService;
        this.orderService = orderService;
        this.singleEntityLinkCreator = singleEntityLinkCreator;
        this.collectionLinkCreator = collectionLinkCreator;
        this.jwtProperty = jwtProperty;
    }

    /**
     * Method to get User as UserDto by id
     *
     * @param jwt JWT value
     * @return User as UserDto
     * @throws CustomException - if User was not found or id has not valid value;
     */
    @GetMapping
    public UserDto findAdmin(@CookieValue(name = "JWT") String jwt) throws CustomException {
        Long userId = JwtDecoder.decodeUserId(jwt, jwtProperty);
        UserDto admin = userService.findById(userId);
        List<Link> links = singleEntityLinkCreator.createLinks(admin);
        return admin.add(links);
    }

    /**
     * Method to get pagination Orders list by User as OrderDto list
     *
     * @param page page
     * @param size page size
     * @return CollectionModel consist of list of OrderDto or empty list if was not found anyone Order
     * and links to previous and nex pages.
     * @throws CustomException - if id or page or size has not valid value;
     */
    @GetMapping(value = "/users/{id}/orders")
    public CollectionModel<OrderDto> findOrdersByUser(@PathVariable("id") Long id,
                                                @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                @RequestParam(name = "size", defaultValue = "10", required = false) int size)
            throws CustomException {
        List<OrderDto> orders = orderService.findAllByUser(id, page, size);
        int lastPage = orderService.findAllByUserLastPage(id, size);
        List<Link> links = collectionLinkCreator.createLinks(orders, id, page, size, lastPage);
        return CollectionModel.of(orders, links);
    }

    /**
     * Method to get Order as OrderDto by User
     *
     * @return Order as OrderDto
     * @throws CustomException - if Order was not found or id or orderId has not valid value;
     */
    @GetMapping(value = "/users/{userId}/orders/{orderId}")
    public OrderDto findOrderByUser(@PathVariable("userId") long userId,
                                    @PathVariable("orderId") long orderId) throws CustomException {
        OrderDto order = orderService.findByIdAndByUser(orderId, userId);
        List<Link> links = singleEntityLinkCreator.createLinks(order, userId);
        return order.add(links);
    }
}
