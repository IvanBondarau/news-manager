package com.epam.lab.exception;

public class ParseException extends RuntimeException {
    private String paramName;
    private String value;

    public ParseException(String paramName, String value) {
        this.paramName = paramName;
        this.value = value;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
