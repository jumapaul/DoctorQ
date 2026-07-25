package com.doctorq;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Config table
 */
@Entity(name = "Config")
public class ConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String serviceName;
    private String configName;
    private String configValue;

    public ConfigEntity() {
    }
    public String getServiceName() {
        return serviceName;
    }

    public String getConfigName() {
        return configName;
    }

    public String getConfigValue() {
        return configValue;
    }
}
