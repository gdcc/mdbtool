package io.gdcc.mdbtool.util;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.net.URISyntaxException;

@Documented
@Constraint(validatedBy = URIValidator.Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface URIValidator {
    
    String message() default "Invalid URI format. Must conform to RFC 2396.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    class Validator implements ConstraintValidator<URIValidator, String> {
        
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return isValidURI(value);
        }
        
        /**
         * Validates whether the given string value is a valid URI.
         *
         * @param value the string to validate as a URI. It can be null or empty.
         * @return true if the value is a valid URI or null/empty, false if it is an invalid URI.
         */
        public static boolean isValidURI(String value) {
            if (value == null || value.isEmpty()) {
                return true; // Null or empty values should be handled by @NotBlank or similar annotations
            }
            try {
                new URI(value);
                return true; // URI successfully parsed
            } catch (URISyntaxException e) {
                return false; // Failed to parse as valid URI
            }
        }
    }
}
