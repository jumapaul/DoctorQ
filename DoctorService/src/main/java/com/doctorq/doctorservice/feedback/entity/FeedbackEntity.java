package com.doctorq.doctorservice.feedback.entity;

import com.doctorq.doctorservice.entities.DoctorEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String username;
    private Long doctorId;
    private String review;
    private Integer rating;
    @CreatedDate
    private LocalDateTime time;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor", referencedColumnName = "id")
    @JsonIgnore
    private DoctorEntity doctor;
}
