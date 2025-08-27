package com.lmelectronica.ecommerce.shared.exceptions;

public class BusinessRuleException extends EcommerceException{
    public BusinessRuleException(String message){
        super(message);
    }

    public static BusinessRuleException insufficentStock(String productName, int requestId, int available){
        return new BusinessRuleException(
            String.format("Insufficent stock for '%s'. Requested: %d, Available: %d", 
                productName, requestId, available    
            )
        );
    }

    public static BusinessRuleException duplicateResource(String resource, String field, Object value){
        return new BusinessRuleException(
            String.format("%s with %s '%s' already exists. ",
                resource, field, value
            )
        );
    }

}
