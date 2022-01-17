package com.jdbaptista.Office;

import com.jdbaptista.Office.labor.ReportFileProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("com.jdbaptista.Office")
@PropertySource("classpath:application.properties")
@EnableConfigurationProperties({
        ReportFileProperties.class
})
public class AppConfig {

    @Value("${MATERIALS_PATH:files/materials}")
    private String MATERIALS_PATH;

    @Value("${LABOR_PATH:files/labor}")
    private String LABOR_PATH;

    @Bean
    public OfficeConfig officeConfig() {
        return new OfficeConfig(MATERIALS_PATH, LABOR_PATH);
    }

}
