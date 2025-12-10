package com.doctorq.appointmentservice.mail;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    APPOINTMENT_CREATION("send_appointment_mail.html"),
    DOCTOR_MAIL("doctor_mail.html");

    private final String template;

    EmailTemplate(String template) {
        this.template = template;
    }
}
