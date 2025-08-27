package com.lmelectronica.ecommerce.shared.exceptions;

public class ResourceNotFoundException extends EcommerceException{

    public ResourceNotFoundException(String resource, Object id) {
        super(String.format("%s with id '%s' not found. ", resource, id));
    }

    public ResourceNotFoundException(String message){
        super(message);
    }
}
