package com.ousl.lfs.ousl_lfs_backend.auth.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record RegisterRequest(  @NotBlank(message="Full name is required") String fullName,
                                @Email(message="Invalid email") @NotBlank String email,
                                @NotBlank(message="University ID is required") String universityId,
                                @NotBlank(message="Password is required") String password,
                                @NotBlank(message="Role is required") String role ) {
}
