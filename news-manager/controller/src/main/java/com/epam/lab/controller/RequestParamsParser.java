package com.epam.lab.controller;

import com.epam.lab.dto.SortOrder;
import com.epam.lab.exception.ParseException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
class RequestParamsParser {

    private static final Logger logger = Logger.getLogger(RequestParamsParser.class);

    private static final Pattern tagNamesPattern = Pattern.compile("([a-zA-Z0-9-]+)(,[a-zA-Z0-9-]+)*");
    private static final Pattern orderParamsPattern = Pattern.compile("([a-zA-Z]+)(,[a-zA-Z]+)*");

    Set<String> parseTagNames(String strTagNames) {
        if (strTagNames == null) {
            logger.warn("Empty parse request");
            return new HashSet<>();
        }
        Matcher matcher = tagNamesPattern.matcher(strTagNames);
        if (!matcher.matches()) {
            logger.error("tagNames='" + strTagNames + "' don't match tag names pattern");
            logger.error("ParseException occurred");
            throw new ParseException("tagNames", strTagNames);
        }

        String[] tagNamesArr = strTagNames.split(",");
        return new HashSet<>(Arrays.asList(tagNamesArr));
    }

     List<SortOrder> parseOrderParams(String orderParams) {
        if (orderParams == null) {
            return Collections.emptyList();
        }
        Matcher matcher = orderParamsPattern.matcher(orderParams);
        if (!matcher.matches()) {
            logger.error("orderParams='" + orderParams + "' don't match order params pattern");
            logger.error("ParseException occurred");
            throw new ParseException("order", orderParams);
        }

        String[] orderParamsArr = orderParams.split(",");
        return Arrays.stream(orderParamsArr).map(SortOrder::fromString).collect(Collectors.toList());
    }
}


