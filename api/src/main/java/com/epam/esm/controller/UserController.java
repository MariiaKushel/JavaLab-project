package com.epam.esm.controller;

import com.epam.esm.enumeration.UserRole;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.util.impl.AdminCollectionLinkCreator;
import com.epam.esm.util.impl.AdminSingleEntityLinkCreator;
import com.epam.esm.util.impl.UserCollectionLinkCreator;
import com.epam.esm.util.impl.UserSingleEntityLinkCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
@RequestMapping(value = "/users")
public class UserController {

    private static final String USER_ID_CLAIM_KEY = "user_id";
    private static final String ROLE_CLAIM_KEY = "authorities";

    private UserService userService;
    private OrderService orderService;
    private UserSingleEntityLinkCreator userSingleEntityLinkCreator;
    private UserCollectionLinkCreator userCollectionLinkCreator;
    private AdminSingleEntityLinkCreator adminSingleEntityLinkCreator;
    private AdminCollectionLinkCreator adminCollectionLinkCreator;

    @Autowired
    public UserController(UserService userService, OrderService orderService,
                          UserSingleEntityLinkCreator userSingleEntityLinkCreator,
                          UserCollectionLinkCreator userCollectionLinkCreator,
                          AdminSingleEntityLinkCreator adminSingleEntityLinkCreator,
                          AdminCollectionLinkCreator adminCollectionLinkCreator) {
        this.userService = userService;
        this.orderService = orderService;
        this.userSingleEntityLinkCreator = userSingleEntityLinkCreator;
        this.userCollectionLinkCreator = userCollectionLinkCreator;
        this.adminSingleEntityLinkCreator = adminSingleEntityLinkCreator;
        this.adminCollectionLinkCreator = adminCollectionLinkCreator;
    }

    /**
     * Method to get current User as UserDto by access token
     *
     * @param jwt access token
     * @return User as UserDto
     * @throws CustomException - if User was not found or id has not valid value;
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/me")
    public UserDto findUser(@AuthenticationPrincipal Jwt jwt) throws CustomException {
        Long userId = jwt.getClaim(USER_ID_CLAIM_KEY);
        UserDto user = userService.findById(userId);
        List<Link> links = user.getRole()==UserRole.ROLE_USER
                ? userSingleEntityLinkCreator.createLinks(user)
                : adminSingleEntityLinkCreator.createLinks(user);
        return user.add(links);
    }

    /**
     * Method to get pagination Orders list by current User as OrderDto list
     *
     * @param jwt  access token
     * @param page page
     * @param size page size
     * @return CollectionModel consist of list of OrderDto or empty list if was not found anyone Order
     * and links to previous and nex pages.
     * @throws CustomException - if id or page or size has not valid value;
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/me/orders")
    public CollectionModel<OrderDto> findOrdersByCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size)
            throws CustomException {
        Long userId = jwt.getClaim(USER_ID_CLAIM_KEY);
        List<OrderDto> orders = orderService.findAllByUser(userId, page, size);
        int lastPage = orderService.findAllByUserLastPage(userId, size);
        List<Link> links = userCollectionLinkCreator.createLinks(orders, userId, page, size, lastPage);
        return CollectionModel.of(orders, links);
    }

    /**
     * Method to get pagination Orders list by User id as OrderDto list
     *
     * @param userId user id
     * @param page   page
     * @param size   page size
     * @return CollectionModel consist of list of OrderDto or empty list if was not found anyone Order
     * and links to previous and nex pages.
     * @throws CustomException - if id or page or size has not valid value;
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/{userId}/orders")
    public CollectionModel<OrderDto> findOrdersByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size)
            throws CustomException {
        List<OrderDto> orders = orderService.findAllByUser(userId, page, size);
        int lastPage = orderService.findAllByUserLastPage(userId, size);
        List<Link> links = adminCollectionLinkCreator.createLinks(orders, userId, page, size, lastPage);
        return CollectionModel.of(orders, links);
    }

    /**
     * Method to get Order as OrderDto by current User
     *
     * @param jwt     access token
     * @param orderId Order id
     * @return Order as OrderDto
     * @throws CustomException - if Order was not found or id or orderId has not valid value;
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/me/orders/{orderId}")
    public OrderDto findOrderByCurrentUser(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable("orderId") long orderId) throws CustomException {
        Long userId = jwt.getClaim(USER_ID_CLAIM_KEY);
        OrderDto order = orderService.findByIdAndByUser(orderId, userId);
        List<Link> links = userSingleEntityLinkCreator.createLinks(order, userId);
        return order.add(links);
    }

    /**
     * Method to get Order as OrderDto by User id
     *
     * @param userId  user id
     * @param orderId Order id
     * @return Order as OrderDto
     * @throws CustomException - if Order was not found or id or orderId has not valid value;
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/{userId}/orders/{orderId}")
    public OrderDto findOrderByUser(@PathVariable("userId") Long userId,
                                    @PathVariable("orderId") long orderId) throws CustomException {
        OrderDto order = orderService.findByIdAndByUser(orderId, userId);
        List<Link> links = adminSingleEntityLinkCreator.createLinks(order, userId);
        return order.add(links);
    }

    /**
     * Method to create new Order by current User
     *
     * @param jwt          access token
     * @param certificates certificate list
     * @return new Order as OrderDto
     * @throws CustomException - if certificate list or id has not valid value;
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(value = "/me/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@AuthenticationPrincipal Jwt jwt,
                                @RequestBody List<CertificateDto> certificates) throws CustomException {
        Long userId = jwt.getClaim(USER_ID_CLAIM_KEY);
        OrderDto order = orderService.create(userId, certificates);
        List<Link> links = userSingleEntityLinkCreator.createLinks(order, userId);
        return order.add(links);
    }
}
