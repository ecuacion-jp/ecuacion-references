package jp.ecuacion.references.lib.tutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class WebApplication extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args);
  }

  /** 
   * Needed for deploy to an existing web application server. 
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(WebApplication.class);
  }
}
