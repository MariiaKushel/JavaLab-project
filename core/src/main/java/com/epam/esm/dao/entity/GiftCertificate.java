package com.epam.esm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Class represent GiftCertificate entity
 */
public class GiftCertificate extends BaseEntity {

    private String name;
    private String description;
    private BigDecimal price;
    private int duration;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;

    private GiftCertificate() {

    }

    public static Builder newBuilder() {
        return new GiftCertificate().new Builder();
    }

    public class Builder {
        private Builder() {

        }

        public Builder setEntityId(long entityId) {
            GiftCertificate.this.setId(entityId);
            return this;
        }

        public Builder setName(String name) {
            GiftCertificate.this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            GiftCertificate.this.description = description;
            return this;
        }

        public Builder setPrice(BigDecimal price) {
            GiftCertificate.this.price = price;
            return this;
        }

        public Builder setDuration(int duration) {
            GiftCertificate.this.duration = duration;
            return this;
        }

        public Builder setCreateDate(LocalDateTime createDate) {
            GiftCertificate.this.createDate = createDate;
            return this;
        }

        public Builder setLastUpdateDate(LocalDateTime lastUpdateDate) {
            GiftCertificate.this.lastUpdateDate = lastUpdateDate;
            return this;
        }

        public GiftCertificate build() {
            return GiftCertificate.this;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GiftCertificate that = (GiftCertificate) o;

        if (duration != that.duration) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        return lastUpdateDate != null ? lastUpdateDate.equals(that.lastUpdateDate) : that.lastUpdateDate == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + duration;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (lastUpdateDate != null ? lastUpdateDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("GiftCertificate [")
                .append("name=")
                .append(name)
                .append(", description=")
                .append(description)
                .append(", price=")
                .append(price)
                .append(", duration=")
                .append(duration)
                .append(", createDate=")
                .append(createDate)
                .append(", lastUpdateDate=")
                .append(lastUpdateDate)
                .append("]")
                .toString();
    }
}
