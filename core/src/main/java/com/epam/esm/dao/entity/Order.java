package com.epam.esm.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Class represent Order entity
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"user", "giftCertificatesList"})
@ToString(callSuper = true, exclude = {"user", "giftCertificatesList"})
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "orders_gift_certificates",
            joinColumns = @JoinColumn(name = "id_order"),
            inverseJoinColumns = @JoinColumn(name = "id_gift_certificate"))
    private List<GiftCertificate> giftCertificatesList;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;
    @Column(name = "amount")
    private BigDecimal amount;

    @PrePersist
    public void preSave() {
        LocalDateTime now = LocalDateTime.now();
        purchaseDate = now;
    }
}
