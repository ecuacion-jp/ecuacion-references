package jp.ecuacion.referenceapps.splib.web.tutorial.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.web.config")

@PropertySources({@PropertySource(value = "classpath:application_profile.properties")})
public class AppConfig {

}
