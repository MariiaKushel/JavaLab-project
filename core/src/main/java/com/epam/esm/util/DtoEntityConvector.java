package com.epam.esm.util;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.Order;
import com.epam.esm.dao.entity.User;
import com.epam.esm.enumeration.AppRole;
import com.epam.esm.service.dto.CertificateDto;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.RegistrationFormDto;
import com.epam.esm.service.dto.TagDto;
import com.epam.esm.service.dto.UserDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Util class helps to convert entity to dto and vice versa
 */
public class DtoEntityConvector {

    /**
     * Convert CustomTag to TagDto
     *
     * @param tag Custom Tag
     * @return TagDto
     */
    public static TagDto convert(CustomTag tag) {
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    /**
     * Convert CustomTag list to TagDto list
     *
     * @param tags Custom Tag list
     * @return TagDto list
     */
    public static List<TagDto> convertTags(List<CustomTag> tags) {
        return tags.stream()
                .map(DtoEntityConvector::convert)
                .toList();
    }

    /**
     * Convert CustomTag set to TagDto set
     *
     * @param tags Custom Tag set
     * @return TagDto set
     */
    public static Set<TagDto> convertTags(Set<CustomTag> tags) {
        return tags.stream()
                .map(DtoEntityConvector::convert)
                .collect(Collectors.toSet());
    }

    /**
     * Convert TagDto to CustomTag
     *
     * @param dto TagDto
     * @return CustomTag
     */
    public static CustomTag convert(TagDto dto) {
        CustomTag tag = new CustomTag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        return tag;
    }

    /**
     * Convert TagDto set to CustomTag set
     *
     * @param dtos TagDto set
     * @return CustomTag set
     */
    public static Set<CustomTag> convertDtos(Set<TagDto> dtos) {
        return dtos.stream()
                .map(DtoEntityConvector::convert)
                .collect(Collectors.toSet());
    }

    /**
     * Convert GiftCertificate to CertificateDto
     *
     * @param certificate GiftCertificate
     * @return CertificateDto
     */
    public static CertificateDto convert(GiftCertificate certificate) {
        CertificateDto dto = new CertificateDto();
        dto.setId(certificate.getId());
        dto.setName(certificate.getName());
        dto.setDescription(certificate.getDescription());
        dto.setPrice(certificate.getPrice());
        dto.setDuration(certificate.getDuration());
        dto.setCreateDate(certificate.getCreateDate());
        dto.setLastUpdateDate(certificate.getLastUpdateDate());
        Set<CustomTag> tags = certificate.getTags();
        Set<TagDto> tagDtos = convertTags(tags);
        dto.setTags(tagDtos);
        return dto;
    }

    /**
     * Convert GiftCertificate list to CertificateDto list
     *
     * @param certificates GiftCertificate list
     * @return CertificateDto list
     */
    public static List<CertificateDto> convertCertificates(List<GiftCertificate> certificates) {
        return certificates.stream()
                .map(DtoEntityConvector::convert)
                .toList();
    }

    /**
     * Convert CertificateDto to GiftCertificate
     *
     * @param dto CertificateDto
     * @return GiftCertificate
     */
    public static GiftCertificate convert(CertificateDto dto) {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(dto.getId());
        certificate.setName(dto.getName());
        certificate.setDescription(dto.getDescription());
        certificate.setPrice(dto.getPrice());
        certificate.setDuration(dto.getDuration());
        Set<TagDto> dtos = dto.getTags();
        Set<CustomTag> tags = convertDtos(dtos);
        certificate.setTags(tags);
        return certificate;
    }

    /**
     * Convert User to UserDto
     *
     * @param user User
     * @return UserDto
     */
    public static UserDto convert(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getLogin());
        dto.setName(user.getName());
        String role = user.getRole().getName();
        dto.setRole(AppRole.valueOf(role));
        return dto;
    }

    /**
     * Convert Order to OrderDto
     *
     * @param order Order
     * @return OrderDto
     */
    public static OrderDto convert(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setPurchaseDate(order.getPurchaseDate());
        dto.setAmount(order.getAmount());
        return dto;
    }

    /**
     * Convert Order list to OrderDto list
     *
     * @param orders Order list
     * @return OrderDto list
     */
    public static List<OrderDto> convertOrders(List<Order> orders) {
        return orders.stream()
                .map(DtoEntityConvector::convert)
                .toList();
    }

    /**
     * Convert RegistrationFormDto to User
     *
     * @param dto RegistrationFormDto
     * @return User
     */
    public static User convert(RegistrationFormDto dto) {
        User user = new User();
        user.setLogin(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        return user;
    }

}
