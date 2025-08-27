package com.lmelectronica.ecommerce.shared.exceptions;

public class ValidationException extends EcommerceException {
    public ValidationException(String field, String message){
        super(String.format("Validation failed for field '%s': '%s'", field, message));
    }

    public ValidationException(String message){
        super(message);
    }
}
