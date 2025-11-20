package com.doctorq.favoriteservice.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String email;
    private String hospital;
    private String profilePictureUrl;
    private Double rating;
    private List<String> doctorCategory;
}