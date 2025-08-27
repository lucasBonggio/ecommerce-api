package com.lmelectronica.ecommerce.shared.exceptions;

public class AuthorizationException extends EcommerceException{
    public AuthorizationException(String message){
        super(message);
    }

    public static AuthorizationException accessDenied(String resource){
        return new AuthorizationException("Access denied to " + resource);
    }

    public static AuthorizationException invalidCredentials(){
        return new AuthorizationException("Invalid username or password. ");
    }
}
