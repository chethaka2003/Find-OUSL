package com.ousl.lfs.ousl_lfs_backend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "ulfs.auth")
public class AuthProperties {
    private List<String> allowedDomains = new ArrayList<>();
    private String verificationLinkBase = "http://localhost:8080/api/auth/verify";
    private long tokenTtlMinutes = 60;
    private int rateLimitPerIpPerHour = 20;
    private int minPasswordLength = 8;

    public List<String> getAllowedDomains() { return allowedDomains; }
    public void setAllowedDomains(List<String> allowedDomains) { this.allowedDomains = allowedDomains; }
    public String getVerificationLinkBase() { return verificationLinkBase; }
    public void setVerificationLinkBase(String verificationLinkBase) { this.verificationLinkBase = verificationLinkBase; }
    public long getTokenTtlMinutes() { return tokenTtlMinutes; }
    public void setTokenTtlMinutes(long tokenTtlMinutes) { this.tokenTtlMinutes = tokenTtlMinutes; }
    public int getRateLimitPerIpPerHour() { return rateLimitPerIpPerHour; }
    public void setRateLimitPerIpPerHour(int rateLimitPerIpPerHour) { this.rateLimitPerIpPerHour = rateLimitPerIpPerHour; }
    public int getMinPasswordLength() { return minPasswordLength; }
    public void setMinPasswordLength(int minPasswordLength) { this.minPasswordLength = minPasswordLength; }
}
