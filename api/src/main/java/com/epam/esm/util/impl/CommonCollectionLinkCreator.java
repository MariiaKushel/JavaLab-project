package com.epam.esm.util.impl;

import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.util.CollectionLinkCreator;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommonCollectionLinkCreator extends CollectionLinkCreator {

    @Override
    public List<Link> createLinks(List<OrderDto> orders, long userId, int page, int size, int lastPage) throws CustomException {
        return new ArrayList<>();
    }
}
