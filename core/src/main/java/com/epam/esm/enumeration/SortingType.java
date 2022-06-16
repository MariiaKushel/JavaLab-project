package com.epam.esm.enumeration;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enum represents types of sorting
 */
public enum SortingType {
    NAME_ASC("name.asc"),
    NAME_DESC("name.desc"),
    DATE_ASC("date.asc"),
    DATE_DESC("date.desc"),
    DATE_DESC_NAME_ASC("date.desc,name.asc");

    private final String type;

    SortingType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static SortingType getSortingType(String type) {
        Optional<SortingType> currentType = Stream.of(SortingType.values())
                .filter(s -> type.equals(s.getType()))
                .findFirst();
        return currentType
                .orElseThrow(() -> new EnumConstantNotPresentException(SortingType.class,
                        "Sorting type: " + type + "not present"));
    }
}
