package your.company.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.web.config"
    )
public class AppConfig {
  
}
