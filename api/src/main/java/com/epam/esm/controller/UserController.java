package com.epam.esm.controller;

import com.epam.esm.exception.CustomException;
import com.epam.esm.properties.JwtProperty;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.util.JwtDecoder;
import com.epam.esm.util.impl.UserCollectionLinkCreator;
import com.epam.esm.util.impl.UserSingleEntityLinkCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
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
 * Rest controller represent operation on User and their Orders
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private UserService userService;
    private OrderService orderService;
    private UserSingleEntityLinkCreator singleEntityLinkCreator;
    private UserCollectionLinkCreator collectionLinkCreator;
    private JwtProperty jwtProperty;

    @Autowired
    public UserController(UserService userService, OrderService orderService,
                          UserSingleEntityLinkCreator singleEntityLinkCreator,
                          UserCollectionLinkCreator collectionLinkCreator, JwtProperty jwtProperty) {
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
    public UserDto findUser(@CookieValue(name = "JWT") String jwt) throws CustomException {
        Long userId = JwtDecoder.decodeUserId(jwt, jwtProperty);
        UserDto user = userService.findById(userId);
        List<Link> links = singleEntityLinkCreator.createLinks(user);
        return user.add(links);
    }

    /**
     * Method to get pagination Orders list by User as OrderDto list
     *
     * @param jwt  JWT value
     * @param page page
     * @param size page size
     * @return CollectionModel consist of list of OrderDto or empty list if was not found anyone Order
     * and links to previous and nex pages.
     * @throws CustomException - if id or page or size has not valid value;
     */
    @GetMapping(value = "/orders")
    public CollectionModel<OrderDto> findOrders(@CookieValue(name = "JWT") String jwt,
                                                @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                @RequestParam(name = "size", defaultValue = "10", required = false) int size)
            throws CustomException {
        Long userId = JwtDecoder.decodeUserId(jwt, jwtProperty);
        List<OrderDto> orders = orderService.findAllByUser(userId, page, size);
        int lastPage = orderService.findAllByUserLastPage(userId, size);
        List<Link> links = collectionLinkCreator.createLinks(orders, userId, page, size, lastPage);
        return CollectionModel.of(orders, links);
    }

    /**
     * Method to get Order as OrderDto by User
     *
     * @param orderId Order id
     * @return Order as OrderDto
     * @throws CustomException - if Order was not found or id or orderId has not valid value;
     */
    @GetMapping(value = "/orders/{orderId}")
    public OrderDto findOrder(@CookieValue(name = "JWT") String jwt,
                              @PathVariable("orderId") long orderId) throws CustomException {
        Long userId = JwtDecoder.decodeUserId(jwt, jwtProperty);
        OrderDto order = orderService.findByIdAndByUser(orderId, userId);
        List<Link> links = singleEntityLinkCreator.createLinks(order, userId);
        return order.add(links);
    }

    /**
     * Method to create new Order by User
     *
     * @param certificates certificate list
     * @return new Order as OrderDto
     * @throws CustomException - if certificate list or id has not valid value;
     */
    @PostMapping(value = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@CookieValue(name = "JWT") String jwt,
                                @RequestBody List<CertificateDto> certificates) throws CustomException {
        Long userId = JwtDecoder.decodeUserId(jwt, jwtProperty);
        OrderDto order = orderService.create(userId, certificates);
        List<Link> links = singleEntityLinkCreator.createLinks(order, userId);
        return order.add(links);
    }
}
