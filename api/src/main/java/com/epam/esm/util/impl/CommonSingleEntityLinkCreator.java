package com.epam.esm.util.impl;

import com.epam.esm.exception.CustomException;
import com.epam.esm.service.dto.OrderDto;
import com.epam.esm.service.dto.UserDto;
import com.epam.esm.util.SingleEntityLinkCreator;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommonSingleEntityLinkCreator extends SingleEntityLinkCreator {
    @Override
    public List<Link> createLinks(UserDto user) throws CustomException {
        return new ArrayList<>();
    }

    @Override
    public List<Link> createLinks(OrderDto order, Long userId) throws CustomException {
        return new ArrayList<>();
    }
}
