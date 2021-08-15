package edu.bu.cs673.stockportfolio.service.utilities;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

/**********************************************************************************************************************
 * The IexCloudConfig class configures this application with an IEX Cloud API Key.
 *********************************************************************************************************************/
@Configuration
@PropertySource("classpath:secrets.properties")
public class IexCloudConfig {
    private static final String IEX_CLOUD = "IEX_CLOUD_API_KEY";
    private final String TOKEN;

    public IexCloudConfig(Environment env) {
        TOKEN = env.getProperty(IEX_CLOUD);
    }

    @Bean
    public String getToken() {
        return TOKEN;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
