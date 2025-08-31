package jp.ecuacion.referenceapps.splib.web.tutorial.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.web.config")
// @PropertySources({@PropertySource(value = "classpath:application-profile.properties")})
public class AppConfig {

}
