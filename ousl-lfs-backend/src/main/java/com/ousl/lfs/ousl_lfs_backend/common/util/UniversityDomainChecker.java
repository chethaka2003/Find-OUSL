package com.ousl.lfs.ousl_lfs_backend.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UniversityDomainChecker {
    private final Set<String> allowed;

    public UniversityDomainChecker(@Value("${ulfs.auth.allowedDomains}") List<String> domains) {
        this.allowed = domains.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    public void assertAllowed(String email) {
        int at = email.lastIndexOf('@');
        String domain = (at >= 0) ? email.substring(at + 1).toLowerCase() : "";
        if (!allowed.contains(domain)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email must be a university address");
        }
    }
}