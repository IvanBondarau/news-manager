package com.epam.lab.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

    private static final String ERROR_MESSAGES_BUNDLE_NAME = "errorMessages";
    private static final String INTERNAL_ERROR_MESSAGE_CODE = "internalServerError";
    private static final String INVALID_REQUEST_PARAM_MESSAGE_CODE = "invalidRequestParams";
    private static final String DATA_ACCESS_ERROR_MESSAGE_CODE = "databaseError";
    private static final String TAG_ALREADY_EXIST_MESSAGE_CODE = "tagAlreadyExist";
    private static final String TAG_NOT_FOUND_MESSAGE_CODE = "tagNotFound";
    private static final String AUTHOR_NOT_FOUND_MESSAGE_CODE = "authorNotFound";
    private static final String NEWS_NOT_FOUND_MESSAGE_CODE = "newsNotFound";
    private final ThreadLocal<ResourceBundle> errorMessagesBundle = new ThreadLocal<>();

    @ExceptionHandler(value = ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RequestError> handleItemNotFound(ItemNotFoundException e, Locale locale) {
        setLocalizedResourceBundle(locale);

        String errorMessageCode;
        if (e instanceof AuthorNotFoundException) {
            errorMessageCode = AUTHOR_NOT_FOUND_MESSAGE_CODE;
        } else if (e instanceof TagNotFoundException) {
            errorMessageCode = TAG_NOT_FOUND_MESSAGE_CODE;
        } else {
            errorMessageCode = NEWS_NOT_FOUND_MESSAGE_CODE;
        }

        RequestError requestError = createRequestError(errorMessageCode, e.getId());
        HttpHeaders httpHeaders = getDefaultHeadersJson();

        return new ResponseEntity<>(requestError, httpHeaders, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({TagAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)

    public ResponseEntity<RequestError> handleTagAlreadyExistException(TagAlreadyExistException e, Locale locale) {
        setLocalizedResourceBundle(locale);
        RequestError requestError = createRequestError(TAG_ALREADY_EXIST_MESSAGE_CODE,
                e.getName(), e.getTagId());
        HttpHeaders httpHeaders = getDefaultHeadersJson();
        return new ResponseEntity<>(requestError, httpHeaders, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({DataAccessException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<RequestError> handleDataAccessException(DataAccessException e, Locale locale) {
        setLocalizedResourceBundle(locale);

        RequestError requestError = createRequestError(DATA_ACCESS_ERROR_MESSAGE_CODE, e.getLocalizedMessage());
        HttpHeaders httpHeaders = getDefaultHeadersJson();

        return new ResponseEntity<>(requestError,
                httpHeaders, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RequestError> handleParseException(ParseException e, Locale locale) {
        setLocalizedResourceBundle(locale);

        RequestError error = createRequestError(INVALID_REQUEST_PARAM_MESSAGE_CODE,
                e.getParamName(), e.getValue());

        HttpHeaders httpHeaders = getDefaultHeadersJson();

        return new ResponseEntity<>(error, httpHeaders, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<RequestError> handleInternalException(RuntimeException e, Locale locale) {
        setLocalizedResourceBundle(locale);

        RequestError error = createRequestError(INTERNAL_ERROR_MESSAGE_CODE, e.getLocalizedMessage());

        HttpHeaders httpHeaders = getDefaultHeadersJson();
        return new ResponseEntity<>(error, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpHeaders getDefaultHeadersJson() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        return httpHeaders;
    }

    private void setLocalizedResourceBundle(Locale locale) {
        errorMessagesBundle.set(ResourceBundle.getBundle(ERROR_MESSAGES_BUNDLE_NAME, locale));
    }

    private RequestError createRequestError(String errorMessageCode, Object... errorParams) {
        String errorMessage = getLocalizedErrorMessage(errorMessageCode);
        String formattedErrorMessage = formatErrorMessage(errorMessage, errorParams);
        return new RequestError(formattedErrorMessage);
    }

    private String getLocalizedErrorMessage(String errorMessageCode) {
        String errorMessage = errorMessagesBundle.get().getString(errorMessageCode);
        return convertFromIsoToUtf8(errorMessage);
    }

    private String formatErrorMessage(String errorMessage, Object... args) {
        return String.format(errorMessage, args);
    }

    private String convertFromIsoToUtf8(String isoString) {
        return new String(isoString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
