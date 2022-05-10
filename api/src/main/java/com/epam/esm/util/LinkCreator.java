package com.epam.esm.util;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.TagController;
import com.epam.esm.controller.UserController;
import com.epam.esm.exception.CustomException;
import com.epam.esm.service.SortingType;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.dto.UserDto;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Util class helps to create links by dto
 */
public class LinkCreator {

    private static final String DELETE = "delete";
    private static final String CREATE = "create";
    private static final String UPDATE = "update";
    private static final String FIND_ALL = "findAll";
    private static final String FIND_THE_MOST_WIDELY_TAG = "findTheMostWidelyTag";
    private static final String ORDERS = "orders";
    private static final String CREATE_ORDER = "createOrder";
    private static final String USER = "user";
    private static final String SEARCH = "search";
    private static final String FIND_BY_TAGS = "findByTags";
    private static final String CURRENT_PAGE = "currentPage";
    private static final String PREVIOUS_PAGE = "previousPage";
    private static final String NEXT_PAGE = "nextPage";

    /**
     * Method create links by single TagDto.
     *
     * @param tag TagDto
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public static List<Link> createSingleEntityLinks(TagDto tag) throws CustomException {
        Link selfLink = linkTo(methodOn(TagController.class).findTag(tag.getId())).withSelfRel();
        Link deleteLink = linkTo(methodOn(TagController.class).deleteTag(tag.getId())).withRel(DELETE);
        Link createLink = linkTo(methodOn(TagController.class).createCustomTag(null)).withRel(CREATE);
        Link findAllLink = linkTo(methodOn(TagController.class).findAllTags(1, 10)).withRel(FIND_ALL);
        Link findTheMostWidelyTagLink = linkTo(methodOn(TagController.class).findTheMostWidelyTag())
                .withRel(FIND_THE_MOST_WIDELY_TAG);
        return List.of(selfLink, deleteLink, createLink, findAllLink, findTheMostWidelyTagLink);
    }

    /**
     * Method create links by single UserDto.
     *
     * @param user UserDto
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public static List<Link> createSingleEntityLinks(UserDto user) throws CustomException {
        Link selfLink = linkTo(methodOn(UserController.class).findUser(user.getId())).withSelfRel();
        Link ordersLink = linkTo(methodOn(UserController.class).findOrders(user.getId(), 1, 10))
                .withRel(ORDERS);
        Link createOrderLink = linkTo(methodOn(UserController.class).createOrder(user.getId(), null))
                .withRel(CREATE_ORDER);
        return List.of(selfLink, ordersLink, createOrderLink);
    }

    /**
     * Method create links by single OrderDto.
     *
     * @param order  OrderDto
     * @param userId user id
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public static List<Link> createSingleEntityLinks(OrderDto order, long userId) throws CustomException {
        Link selfLink = linkTo(methodOn(UserController.class).findOrder(userId, order.getId())).withSelfRel();
        Link ordersLink = linkTo(methodOn(UserController.class).findOrders(userId, 1, 10))
                .withRel(ORDERS);
        Link createOrderLink = linkTo(methodOn(UserController.class).createOrder(userId, null))
                .withRel(CREATE_ORDER);
        Link userLink = linkTo(methodOn(UserController.class).findUser(userId)).withRel(USER);
        return List.of(selfLink, ordersLink, createOrderLink, userLink);
    }

    /**
     * Method create links by single CertificateDto.
     *
     * @param certificate CertificateDto
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public static List<Link> createSingleEntityLinks(CertificateDto certificate) throws CustomException {
        for (TagDto tag : certificate.getTags()) {
            tag.add(linkTo(methodOn(TagController.class).findTag(tag.getId())).withSelfRel());
        }
        Link selfLink = linkTo(methodOn(CertificateController.class).findCertificate(certificate.getId()))
                .withSelfRel();
        Link updateLink = linkTo(methodOn(CertificateController.class).updateCertificate(certificate.getId(), null))
                .withRel(UPDATE);
        Link deleteLink = linkTo(methodOn(CertificateController.class).deleteCertificate(certificate.getId()))
                .withRel(DELETE);
        Link createLink = linkTo(methodOn(CertificateController.class).createCertificate(null))
                .withRel(CREATE);
        Link findAllLink = linkTo(methodOn(CertificateController.class).findAllCertificates(1, 10))
                .withRel(FIND_ALL);
        String tagName = certificate.getTags().stream()
                .findFirst()
                .map(TagDto::getName)
                .orElse(null);
        Link searchLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(1, 10, tagName, certificate.getName(),
                        certificate.getDescription(), SortingType.NAME_ASC.getType()))
                .withRel(SEARCH);
        String[] tags = new String[certificate.getTags().size()];
        List<String> tagNames = certificate.getTags().stream().map(TagDto::getName).toList();
        tags = tagNames.toArray(tags);
        Link findByTagsLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(1, 10, tags))
                .withRel(FIND_BY_TAGS);
        return List.of(selfLink, updateLink, deleteLink, createLink, findAllLink, searchLink, findByTagsLink);
    }

    /**
     * Method create links with pagination by list TagDto. Also, it adds selfLink to each tag of list.
     *
     * @param tags list TagDto
     * @param page page
     * @param size page size
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public static List<Link> createPaginationListEntityLinks(List<TagDto> tags, int page, int size)
            throws CustomException {
        for (TagDto tag : tags) {
            tag.add(linkTo(methodOn(TagController.class).findTag(tag.getId())).withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        if (page > 1) {
            Link previousPageLink = linkTo(methodOn(TagController.class).findAllTags(page - 1, size))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(TagController.class).findAllTags(page, size)).withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (tags.size() == size) {
            Link nexPageLink = linkTo(methodOn(TagController.class).findAllTags(page + 1, size))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link createLink = linkTo(methodOn(TagController.class).createCustomTag(null)).withRel(CREATE);
        links.add(createLink);
        Link findAllLink = linkTo(methodOn(TagController.class).findAllTags(1, 10)).withRel(FIND_ALL);
        links.add(findAllLink);
        Link findTheMostWidelyTagLink = linkTo(methodOn(TagController.class).findTheMostWidelyTag())
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
    public static List<Link> createPaginationListEntityLinks(List<OrderDto> orders, long userId, int page, int size)
            throws CustomException {
        for (OrderDto order : orders) {
            order.add(linkTo(methodOn(UserController.class).findOrder(userId, order.getId())).withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        if (page > 1) {
            Link previousPageLink = linkTo(methodOn(UserController.class).findOrders(userId, page - 1, size))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(UserController.class).findOrders(userId, page, size))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (orders.size() == size) {
            Link nexPageLink = linkTo(methodOn(UserController.class).findOrders(userId, page + 1, size))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link createOrderLink = linkTo(methodOn(UserController.class).createOrder(userId, null))
                .withRel(CREATE_ORDER);
        links.add(createOrderLink);
        Link userLink = linkTo(methodOn(UserController.class).findUser(userId)).withRel(USER);
        links.add(userLink);
        return links;
    }

    /**
     * Method create links with pagination by list CertificateDto. Also, it adds selfLink to each certificate of list.
     *
     * @param certificates list CertificateDto
     * @param page         page
     * @param size         page size
     * @return Link list
     * @throws CustomException if some linked methods throw CustomException
     */
    public static List<Link> createPaginationCertificateListLinks(List<CertificateDto> certificates,
                                                                  int page, int size) throws CustomException {
        for (CertificateDto certificate : certificates) {
            certificate.add(linkTo(methodOn(CertificateController.class).findCertificate(certificate.getId()))
                    .withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        if (page > 1) {
            Link previousPageLink = linkTo(methodOn(CertificateController.class).findAllCertificates(page - 1, size))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(CertificateController.class).findAllCertificates(page, size))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (certificates.size() == size) {
            Link nexPageLink = linkTo(methodOn(CertificateController.class).findAllCertificates(page + 1, size))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link searchLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(1, 10, "tag_name", "name_part",
                        "description_part", SortingType.DATE_ASC.getType()))
                .withRel(SEARCH);
        links.add(searchLink);
        String[] tags = new String[]{"tag_name_1", "tag_name_2"};
        Link findByTagsLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(1, 10, tags))
                .withRel(FIND_BY_TAGS);
        links.add(findByTagsLink);
        Link createLink = linkTo(methodOn(CertificateController.class).createCertificate(null))
                .withRel(CREATE);
        links.add(createLink);
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
    public static List<Link> createPaginationCertificateListLinks(List<CertificateDto> certificates, String[] tags,
                                                                  int page, int size) throws CustomException {
        for (CertificateDto certificate : certificates) {
            certificate.add(linkTo(methodOn(CertificateController.class).findCertificate(certificate.getId()))
                    .withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        if (page > 1) {
            Link previousPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificatesByTags(page - 1, size, tags))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(page, size, tags))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (certificates.size() == size) {
            Link nexPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificatesByTags(page + 1, size, tags))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link searchLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(1, 10, "tag_name", "name_part",
                        "description_part", SortingType.DATE_ASC.getType()))
                .withRel(SEARCH);
        links.add(searchLink);
        Link findAllLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(1, 10))
                .withRel(FIND_ALL);
        links.add(findAllLink);
        Link createLink = linkTo(methodOn(CertificateController.class).createCertificate(null))
                .withRel(CREATE);
        links.add(createLink);
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
    public static List<Link> createPaginationCertificateListLinks(List<CertificateDto> certificates,
                                                                  String tag, String name, String description,
                                                                  String sortBy, int page, int size)
            throws CustomException {
        for (CertificateDto certificate : certificates) {
            certificate.add(linkTo(methodOn(CertificateController.class).findCertificate(certificate.getId()))
                    .withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        if (page > 1) {
            Link previousPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificatesByParameters(page - 1, size, tag, name, description, sortBy))
                    .withRel(PREVIOUS_PAGE);
            links.add(previousPageLink);
        }
        Link currentPageLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByParameters(page, size, tag, name, description, sortBy))
                .withRel(CURRENT_PAGE);
        links.add(currentPageLink);
        if (certificates.size() == size) {
            Link nexPageLink = linkTo(methodOn(CertificateController.class)
                    .findAllCertificatesByParameters(page + 1, size, tag, name, description, sortBy))
                    .withRel(NEXT_PAGE);
            links.add(nexPageLink);
        }
        Link findAllLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificates(1, 10))
                .withRel(FIND_ALL);
        links.add(findAllLink);
        String[] tags = new String[]{"tag_name_1", "tag_name_2"};
        Link findByTagsLink = linkTo(methodOn(CertificateController.class)
                .findAllCertificatesByTags(1, 10, tags))
                .withRel(FIND_BY_TAGS);
        links.add(findByTagsLink);
        Link createLink = linkTo(methodOn(CertificateController.class).createCertificate(null))
                .withRel(CREATE);
        links.add(createLink);
        return links;
    }

}
