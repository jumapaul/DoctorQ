package com.doctorq.userservice.user_profile;

import com.doctorq.userservice.user_profile.dtos.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
