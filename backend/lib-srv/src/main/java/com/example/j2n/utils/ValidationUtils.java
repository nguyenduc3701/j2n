package com.example.j2n.utils;

import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.impl.BaseMessage;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,11}$");

    /**
     * Validate Long value with default BAD_REQUEST message
     */
    public static void validateLong(Long value) {
        validateLong(value, BaseMessageEnum.BAD_REQUEST);
    }

    /**
     * Validate Long value with custom message
     */
    public static void validateLong(Long value, BaseMessage message) {
        if (value == null || value <= 0) {
            log.error("Validation failed: Long value is invalid. Message: {}", message.getMessage());
            throw new InvalidInputException(message);
        }
    }

    /**
     * Validate String with default BAD_REQUEST message
     */
    public static void validateString(String str) {
        validateString(str, BaseMessageEnum.BAD_REQUEST);
    }

    /**
     * Validate String with custom message
     */
    public static void validateString(String str, BaseMessage message) {
        if (!StringUtils.hasText(str)) {
            log.error("Validation failed: String is null or blank. Message: {}", message.getMessage());
            throw new InvalidInputException(message);
        }
    }

    /**
     * Validate a required field is not null or blank
     */
    public static void validateRequired(Object value, String fieldName) {
        if (value == null || (value instanceof String s && !StringUtils.hasText(s))) {
            log.error("Validation failed: Field '{}' is required", fieldName);
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs(fieldName));
        }
    }

    /**
     * Validate Integer value is not null and positive
     */
    public static void validateInteger(Integer value) {
        if (value == null || value <= 0) {
            log.error("Validation failed: Integer value is null or <= 0");
            throw new InvalidInputException(BaseMessageEnum.BAD_REQUEST);
        }
    }

    /**
     * Validate Collection is not null or empty
     */
    public static void validateCollection(Collection<?> collection, String fieldName) {
        if (CollectionUtils.isEmpty(collection)) {
            log.error("Validation failed: Collection '{}' is null or empty", fieldName);
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs(fieldName));
        }
    }

    /**
     * Validate Email format
     */
    public static void validateEmail(String email) {
        if (!StringUtils.hasText(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            log.error("Validation failed: Invalid email format [{}]", email);
            throw new InvalidInputException(BaseMessageEnum.BAD_REQUEST);
        }
    }

    /**
     * Validate Phone format
     */
    public static void validatePhone(String phone) {
        if (!StringUtils.hasText(phone) || !PHONE_PATTERN.matcher(phone).matches()) {
            log.error("Validation failed: Invalid phone format [{}]", phone);
            throw new InvalidInputException(BaseMessageEnum.BAD_REQUEST);
        }
    }

    /**
     * Validate string matches pattern
     */
    public static void validatePattern(String value, Pattern pattern, String fieldName) {
        if (value == null || !pattern.matcher(value).matches()) {
            log.error("Validation failed: {} does not match pattern", fieldName);
            throw new InvalidInputException(BaseMessageEnum.BAD_REQUEST);
        }
    }

    /**
     * Validate boolean expression
     */
    public static void validateTrue(boolean expression, BaseMessage message) {
        if (!expression) {
            log.error("Validation failed: {}", message.getMessage());
            throw new InvalidInputException(message);
        }
    }

    /**
     * Validate value range
     */
    public static void validateRange(long value, long min, long max, String fieldName) {
        if (value < min || value > max) {
            log.error("Validation failed: {} value [{}] is out of range [{}, {}]", fieldName, value, min, max);
            throw new InvalidInputException(BaseMessageEnum.BAD_REQUEST);
        }
    }

    /**
     * Validate String value is part of an Enum
     */
    public static <T extends Enum<T>> void validateEnum(String value, Class<T> enumClass, String fieldName) {
        if (!StringUtils.hasText(value)) {
            log.error("Validation failed: {} is required for enum {}", fieldName, enumClass.getSimpleName());
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs(fieldName));
        }
        try {
            Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            log.error("Validation failed: {} value [{}] is invalid for enum {}", fieldName, value, enumClass.getSimpleName());
            throw new InvalidInputException(BaseMessageEnum.BAD_REQUEST);
        }
    }
}
