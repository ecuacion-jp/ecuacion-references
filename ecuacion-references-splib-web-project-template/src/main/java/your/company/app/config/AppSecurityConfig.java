package your.company.app.config;

import java.util.List;
import jp.ecuacion.splib.core.bean.AuthorizationBean;
import jp.ecuacion.splib.web.config.SplibWebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibWebSecurityConfig {

  @Override
  protected String getDefaultSuccessUrl() {
    return "/afterLogin/default";
  }

  @Override
  protected List<AuthorizationBean> getRoleInfo() {
    return null;
  }

  @Override
  protected List<AuthorizationBean> getAuthorityInfo() {
    return null;
  }

  @Override
  protected String getLoginNeededPage() {
    return "/public/show/page?id=home&accessDenied";
  }
  
  @Override
  protected String getAccessDeniedPage() {
    return "/public/show/page?id=home&accessDenied";
  }
}
