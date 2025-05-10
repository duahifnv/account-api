package com.fizalise.accountapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^7\\d{10}$");

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
}