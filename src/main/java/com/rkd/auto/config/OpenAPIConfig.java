package com.rkd.auto.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

/**
 * Access: <a href="http://localhost:3003/swagger-ui/index.html#/">Auto Park System API</a>
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Auto Park System API",
                description = "API for managing vehicle access, occupancy and revenue for a smart parking system.",
                version = "1.0.0",
                contact = @Contact(
                        name = "madrijkaard",
                        email = "mad.rijkaard@gmail.com",
                        url = "https://github.com/madrijkaard"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://mit-license.org/"
                )
        )
)
public class OpenAPIConfig {
}

