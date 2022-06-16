package com.epam.esm.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Class represent GiftCertificate entity
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"tags", "orders"})
@ToString(callSuper = true, exclude = {"tags", "orders"})
@Entity
@Table(name = "gift_certificates")
public class GiftCertificate extends BaseEntity implements Persistable<Long> {

    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "duration")
    private int duration;
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;
    @Column(name = "active")
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "gift_certificates_tags",
            joinColumns = @JoinColumn(name = "id_gift_certificate"),
            inverseJoinColumns = @JoinColumn(name = "id_tag"))
    private Set<CustomTag> tags;

    @ManyToMany(mappedBy = "giftCertificatesList", fetch = FetchType.LAZY)
    private Set<Order> orders;

    public GiftCertificate() {
        this.tags = new HashSet<>();
        this.orders = new HashSet<>();
    }

    @PrePersist
    public void preSave() {
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        lastUpdateDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdateDate = LocalDateTime.now();
    }

    @PreRemove
    public void removeOrderCoupling() {
        for (Order order : orders) {
            order.getGiftCertificatesList().remove(this);
        }
    }

    @Override
    public boolean isNew() {
        return null == this.getId() &&
                this.getTags().stream().allMatch(t -> null==t.getId());
    }
}
