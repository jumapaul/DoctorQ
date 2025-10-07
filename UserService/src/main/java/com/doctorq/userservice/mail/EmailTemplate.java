package com.doctorq.userservice.mail;

import lombok.Getter;

@Getter
public enum EmailTemplate {

    VERIFICATION_CODE("verification-code.html", "Email verification code"),
    RESET_PASSWORD_CODE("reset-password-code.html", "Password reset code");

    private final String template;
    private final String  subject;

    EmailTemplate(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}
