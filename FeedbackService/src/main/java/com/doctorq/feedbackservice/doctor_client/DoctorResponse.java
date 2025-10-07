package com.doctorq.feedbackservice.doctor_client;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String email;
    private String hospital;
}