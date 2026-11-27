
package com.aja.mediaevents.Config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    @Value("${CLOUDINARY_CLOUD_NAME:}")
    private String cloudName;

    @Value("${CLOUDINARY_API_KEY:}")
    private String apiKey;

    @Value("${CLOUDINARY_API_SECRET:}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {

        if (!StringUtils.hasText(cloudName)
                || !StringUtils.hasText(apiKey)
                || !StringUtils.hasText(apiSecret)) {

            throw new IllegalStateException(
                    "Cloudinary configuration is incomplete. "
                    + "Set CLOUDINARY_CLOUD_NAME, "
                    + "CLOUDINARY_API_KEY and CLOUDINARY_API_SECRET."
            );
        }

        Map<String, String> config = new HashMap<>();

        config.put("cloud_name", cloudName.trim());
        config.put("api_key", apiKey.trim());
        config.put("api_secret", apiSecret.trim());
        config.put("secure", "true");

        return new Cloudinary(config);
    }
}
