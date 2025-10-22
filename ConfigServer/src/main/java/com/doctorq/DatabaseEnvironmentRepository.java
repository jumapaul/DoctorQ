package com.doctorq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseEnvironmentRepository implements EnvironmentRepository {

    @Autowired
    private ConfigRepository configRepository;

    @Override
    public Environment findOne(String application, String profile, String label) {

        Environment environment = new Environment(application, profile, label);

        List<ConfigEntity> configs = configRepository.findAllByServiceNameIn(
                List.of("application", application)
        );

        if (!configs.isEmpty()) {
            Map<String, Object> properties = new LinkedHashMap<>();

            //Global configs first
            configs.stream()
                    .filter(config -> config.getServiceName().equals("application"))
                    .forEach(config -> properties.put(config.getConfigName(), config.getConfigValue()));


            //Service specific
            configs.stream()
                    .filter(config -> config.getServiceName().equals(application))
                    .forEach(config -> properties.put(config.getConfigName(), config.getConfigValue()));
//            for (ConfigEntity config : configs) {
//                properties.put(config.getConfigName(), config.getConfigValue());
//            }

            PropertySource propertySource = new PropertySource("databaseConfig", properties);
            environment.add(propertySource);

        } else {
            System.out.println("No database properties found for: " + application);
        }
        return environment;
    }
}
