package com.epam.esm.util;

import com.epam.esm.dao.entity.CustomTag;
import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.service.dto.GiftCertificateDto;

import java.util.List;

/**
 * Util class helps to convert GiftCertificate to GiftCertificateDto and vice versa
 */
public class DtoEntityConvector {

    /**
     * Method helps to make blank GiftCertificate from GiftCertificateDto for create or update operations
     * @param giftCertificateDto GiftCertificateDto, significant fields - name, description, duration, price
     * @return blank of GiftCertificate
     */
    public static GiftCertificate convert(GiftCertificateDto giftCertificateDto) {
        GiftCertificate giftCertificate = GiftCertificate.newBuilder()
                .setName(giftCertificateDto.getName())
                .setDescription(giftCertificateDto.getDescription())
                .setDuration(giftCertificateDto.getDuration())
                .setPrice(giftCertificateDto.getPrice())
                .build();
        return giftCertificate;
    }

    /**
     * Method helps to make GiftCertificateDto from GiftCertificate and list of its tags
     * @param giftCertificate - GiftCertificate
     * @param tags - list of GiftCertificate`s tags
     * @return GiftCertificateDto to view
     */
    public static GiftCertificateDto convert(GiftCertificate giftCertificate, List<CustomTag> tags) {
        GiftCertificateDto giftCertificateDto = new GiftCertificateDto();
        giftCertificateDto.setDtoId(giftCertificate.getId());
        giftCertificateDto.setName(giftCertificate.getName());
        giftCertificateDto.setDescription(giftCertificate.getDescription());
        giftCertificateDto.setPrice(giftCertificate.getPrice());
        giftCertificateDto.setDuration(giftCertificate.getDuration());
        giftCertificateDto.setCreateDate(giftCertificate.getCreateDate());
        giftCertificateDto.setLastUpdateDate(giftCertificate.getLastUpdateDate());
        giftCertificateDto.setTags(tags);
        return giftCertificateDto;
    }

}
