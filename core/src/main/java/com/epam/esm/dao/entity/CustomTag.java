package com.epam.esm.dao.entity;

/**
 * Class represent CustomTag entity
 */
public class CustomTag extends BaseEntity {

    private String name;

    public CustomTag(){

    }

    public CustomTag(String name) {
        this.name = name;
    }

    public CustomTag(long entityId, String name) {
        this.setId(entityId);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CustomTag customTag = (CustomTag) o;

        return name != null ? name.equals(customTag.name) : customTag.name == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("CustomTag [")
                .append("name=")
                .append(name)
                .append("]")
                .toString();
    }
}
