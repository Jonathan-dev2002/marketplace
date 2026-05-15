package com.jo.marketplace.constant;

import java.util.regex.Pattern;

public final class ValidationPatterns {

    private ValidationPatterns() {
    }

    public static final String MULTIPLE_SPACES_FORMAT = "\\s+";
    public static final String SLUG_ALLOWED_CHARS_FORMAT = "[^a-z0-9\\u0E00-\\u0E7F-]";
    public static final String SLUG_VALID_FORMAT = "^[a-z0-9\\u0E00-\\u0E7F-]+$";

    public static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile(MULTIPLE_SPACES_FORMAT);
    public static final Pattern SLUG_ALLOWED_CHARS_PATTERN = Pattern.compile(SLUG_ALLOWED_CHARS_FORMAT);
}
