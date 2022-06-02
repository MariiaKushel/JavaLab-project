package com.epam.esm.specification;

import com.epam.esm.enumeration.SearchParameterName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class represent a couple of search parameter name and its value.
 */
@Data
@AllArgsConstructor
public class SearchCriteria {

    private SearchParameterName key;
    private String value;

}
