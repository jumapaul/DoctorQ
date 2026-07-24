package com.doctorq.gatewayservice.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorResponse = super.getErrorAttributes(request, options);

        HttpStatus status = HttpStatus.valueOf((Integer) errorResponse.get("status"));
        String Constant = "message";

        switch (status) {
            case UNAUTHORIZED -> errorResponse.put(Constant, "Invalid token");
            case BAD_REQUEST -> errorResponse.put(Constant, "Authorization token not passed");
            case FORBIDDEN -> errorResponse.put(Constant, "Forbidden request");
            case SERVICE_UNAVAILABLE, INTERNAL_SERVER_ERROR -> errorResponse.put(Constant, "Service not available");
            case NOT_FOUND -> errorResponse.put(Constant, "Not found");
            default -> errorResponse.put(Constant, "Something went wrong");
        }

        return errorResponse;
    }
}
