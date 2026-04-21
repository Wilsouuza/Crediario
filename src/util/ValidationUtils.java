package util;

import exception.BusinessException;

public class ValidationUtils {

    public static void notNullOrBlank(String value, String fieldName){
        if (value == null || value.isBlank()){
            throw new BusinessException(fieldName + " cannot be empty.");
        }
    }

    public static void notNull(Object value, String fieldName){
        if (value == null){
            throw new BusinessException(fieldName + " cannot be empty.");
        }
    }

    public static void exists(Object value, String entityName){
        if (value == null){
            throw new BusinessException(entityName + " not found");
        }
    }


}
