package com.doctorq.userservice.favorite.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String email;
    private String hospital;
    private String profilePictureUrl;
    private Double rating;
    private List<String> doctorCategory;
    private String aboutDoctor;
    private Integer yearsOfExperience;
    private Integer numberOfPatients;
    private String workingHours;
    private Integer reviewsCount;
}