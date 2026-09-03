package com.pulseride.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pulseride.user.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
