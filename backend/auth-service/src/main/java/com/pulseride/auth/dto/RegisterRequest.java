package com.pulseride.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Format")
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$" ,
        message = "Email must be a valid structured email address (e.g., user123@gmail.com)"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8,max = 16, message = "Password must contain at least 8 characters")
    @Pattern(
         regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "USER|DRIVER", message = "Public registration role must be USER or DRIVER")
    private String role;

}
