package com.ousl.lfs.ousl_lfs_backend.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.regex.Pattern;

@Component
public class PasswordPolicy {
    @Value("${ulfs.auth.minPasswordLength:8}") private int minLen;
    private static final Pattern RULE = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).+$"
    );
    public void validate(String password) {
        if (password.length() < minLen || !RULE.matcher(password).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password too weak (min " + minLen + ", include upper, lower, digit, special)");
        }
    }
}
