package com.pulseride.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
    @Id
    private Long id;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public UserProfile(Long id, String displayName, String email, String role) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }
}
