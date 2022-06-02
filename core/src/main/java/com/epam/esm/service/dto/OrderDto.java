package com.epam.esm.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Class represent Order entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "orders")
public class OrderDto extends RepresentationModel<OrderDto> {

    private Long id;
    private LocalDateTime purchaseDate;
    private BigDecimal amount;

}
