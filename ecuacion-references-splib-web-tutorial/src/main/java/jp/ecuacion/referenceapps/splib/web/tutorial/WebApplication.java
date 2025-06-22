package jp.ecuacion.referenceapps.splib.web.tutorial;

import jp.ecuacion.lib.core.util.PropertyFileUtil;
import jp.ecuacion.splib.web.bean.SplibModelAttributes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

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
  
  /**
   * Returns SplibModelAttributes for app settings.
   */
  @Bean
  SplibModelAttributes appCommonModelAttributes() {
    SplibModelAttributes atr = new SplibModelAttributes();
    atr.setBsBgGradient(true);
    atr.setShowsMessagesLinkedToItemsAtTheTop(false);
    atr.setShowsMessagesLinkedToItemsAtEachItem(true);
    if (PropertyFileUtil.hasApplication("app.html.body.backgroundColor")) {
      atr.setBodyBgColor(PropertyFileUtil.getApplication("app.html.body.backgroundColor"));
    }
    
    return atr;
  }
}
