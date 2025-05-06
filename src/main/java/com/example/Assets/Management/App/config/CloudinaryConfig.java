package com.example.Assets.Management.App.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dnhrxf0au");
        config.put("api_key", "874351794643963");
        config.put("api_secret", "jqaHQ4DJyc59GZizMU42pQJFiX4");
        return new Cloudinary(config);
    }
}
