package com.doctorq.appointmentservice.appointment.mail;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    APPOINTMENT_APPROVAL_TEMPLATE("send_appointment_mail.html"),
    DOCTOR_MAIL("doctor_mail.html");

    private final String template;

    EmailTemplate(String template) {
        this.template = template;
    }
}
