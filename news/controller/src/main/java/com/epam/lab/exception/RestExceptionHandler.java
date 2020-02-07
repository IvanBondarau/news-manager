package com.epam.lab.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Handles exceptions received by controllers,
 * In all cases exception would be extinguished and RequestError would be returned.
 * RequestError consists of single 'error' field, that contains localised message, that describes
 * an error.
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AuthorNotFoundException.class, TagNotFoundException.class, NewsNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RequestError> resolveItemNotFound(
            ItemNotFoundException e,
            Locale locale,
            HttpServletResponse response) throws IOException {
        String errorMessage;
        ResourceBundle errorMessages = ResourceBundle.getBundle("errorMessages", locale);
        if (e instanceof AuthorNotFoundException) {
            errorMessage = errorMessages.getString("authorNotFound");
        } else if (e instanceof TagNotFoundException) {
            errorMessage = errorMessages.getString("tagNotFound");
        } else {
            errorMessage = errorMessages.getString("newsNotFound");
        }

        errorMessage = new String(errorMessage.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        HttpHeaders httpHeaders = new HttpHeaders();

        response.setCharacterEncoding("utf-8");
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<>(new RequestError(String.format(errorMessage, e.getId())),
                httpHeaders, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({TagAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<RequestError> resolveTagAlreadyExist(
            TagAlreadyExistException e,
            Locale locale,
            HttpServletResponse response) throws IOException {

        ResourceBundle errorMessages = ResourceBundle.getBundle("errorMessages", locale);
        String errorMessage = errorMessages.getString("tagAlreadyExist");
        errorMessage = new String(errorMessage.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        HttpHeaders httpHeaders = new HttpHeaders();
        response.setCharacterEncoding("utf-8");
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<>(new RequestError(String.format(errorMessage, e.getName(), e.getTagId())),
                httpHeaders, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({DataAccessException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<RequestError> resolveDataAccessException(
            DataAccessException e,
            Locale locale,
            HttpServletResponse response) throws IOException {

        ResourceBundle errorMessages = ResourceBundle.getBundle("errorMessages", locale);
        String errorMessage = errorMessages.getString("databaseError");
        errorMessage = new String(errorMessage.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        HttpHeaders httpHeaders = new HttpHeaders();
        response.setCharacterEncoding("utf-8");
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<>(new RequestError(errorMessage),
                httpHeaders, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RequestError> resolveParseException(
            ParseException e,
            Locale locale,
            HttpServletResponse response) throws IOException {

        ResourceBundle errorMessages = ResourceBundle.getBundle("errorMessages", locale);
        String errorMessage = errorMessages.getString("invalidRequestParams");
        errorMessage = new String(errorMessage.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        HttpHeaders httpHeaders = new HttpHeaders();
        response.setCharacterEncoding("utf-8");
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<>(new RequestError(String.format(errorMessage, e.getParamName(), e.getValue())),
                httpHeaders, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NewsAuthorNotFoundException.class,
    NewsAuthorAlreadySetException.class,
    NewsTagNotFoundException.class,
    NewsTagAlreadySetException.class,
    ResourceNotFoundException.class,
    RoleAlreadyExistException.class,
    NullPointerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<RequestError> resolveInternalException(
            Exception e,
            Locale locale,
            HttpServletResponse response) throws IOException {

        ResourceBundle errorMessages = ResourceBundle.getBundle("errorMessages", locale);
        String errorMessage = errorMessages.getString("internalServerError");
        errorMessage = new String(errorMessage.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        HttpHeaders httpHeaders = new HttpHeaders();
        response.setCharacterEncoding("utf-8");
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<>(new RequestError(String.format(errorMessage, e.getLocalizedMessage())),
                httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
