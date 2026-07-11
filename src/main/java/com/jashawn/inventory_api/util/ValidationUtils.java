package com.jashawn.inventory_api.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern US_PHONE_PATTERN = Pattern.compile(
            "^(\\+1[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    public static boolean isValidUSPhone(String phone) {
        if (phone == null) {
            return false;
        }

        return US_PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static String formatPhone(String phone) {
        if (phone == null) {
            return null;
        }

        return phone.replaceAll("[\\s\\-()]", "");
    }
}
