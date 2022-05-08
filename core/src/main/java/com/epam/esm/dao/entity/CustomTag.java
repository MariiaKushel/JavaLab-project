package com.epam.esm.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import java.util.Set;

/**
 * Class represent CustomTag entity
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "giftCertificates")
@ToString(callSuper = true, exclude = "giftCertificates")
@Entity
@Table(name = "tags")
public class CustomTag extends BaseEntity {

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<GiftCertificate> giftCertificates;

    public CustomTag(String name) {
        super();
        this.name = name;
    }

    public CustomTag(Long id, String name) {
        super(id);
        this.name = name;
    }

    @PreRemove
    public void removeGiftCertificateCoupling(){
        for( GiftCertificate giftCertificate : giftCertificates){
            giftCertificate.getTags().remove(this);
        }
    }
}
