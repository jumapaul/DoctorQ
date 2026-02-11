package com.doctorq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseEnvironmentRepository implements EnvironmentRepository {

    private static final Logger log = LoggerFactory.getLogger(DatabaseEnvironmentRepository.class);
    private final ConfigRepository configRepository;

    public DatabaseEnvironmentRepository(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

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

            PropertySource propertySource = new PropertySource("databaseConfig", properties);
            environment.add(propertySource);

        } else {
            log.info("No database properties found for: {}", application);
        }
        return environment;
    }
}
