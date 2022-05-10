package com.epam.esm.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Class represent GiftCertificate entity and CustomTags which belong it
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDto extends RepresentationModel<CertificateDto> {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
    private Set<TagDto> tags;

}
