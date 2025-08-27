package com.lmelectronica.ecommerce.shared.exceptions;

public abstract class EcommerceException extends RuntimeException{
    public EcommerceException(String message){
        super(message);
    }

    public EcommerceException(String message, Throwable cause){
        super(message, cause);
    }
}
