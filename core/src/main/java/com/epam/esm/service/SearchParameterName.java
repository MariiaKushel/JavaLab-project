package com.epam.esm.service;

import java.util.HashSet;
import java.util.Set;

/**
 * Class-helper. It contains only search parameters name constants
 */
public class SearchParameterName {
    public static final String TAG = "tag";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String SORT_BY = "sort_by";

    private static Set<String> allParamNames = new HashSet<>();

    static {
        allParamNames.add(TAG);
        allParamNames.add(NAME);
        allParamNames.add(DESCRIPTION);
        allParamNames.add(SORT_BY);
    }

    public static Set<String> getAllParamNames() {
        return allParamNames;
    }
}
