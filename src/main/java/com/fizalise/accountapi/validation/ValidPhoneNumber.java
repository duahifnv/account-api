package com.fizalise.accountapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "Номер телефона должен начинаться с 7 и содержать 11 цифр";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}