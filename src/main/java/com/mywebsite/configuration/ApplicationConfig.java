package com.mywebsite.configuration;

import com.mywebsite.configuration.beans.EncryptionHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Value("${project.encryption.password}")
    String password;

    @Bean
    EncryptionHelper encryptionHelper() {
        return new EncryptionHelper(password);
    }
}
