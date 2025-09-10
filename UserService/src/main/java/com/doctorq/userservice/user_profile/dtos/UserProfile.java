package com.doctorq.userservice.user_profile.dtos;

import com.doctorq.userservice.user.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString(exclude = "user")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gender;
    private String dateOfBirth;
    private String address;
    private String profileUrl;
    @OneToOne(mappedBy = "userProfile", fetch = FetchType.EAGER)
    @JsonIgnore
    private User user;
}
