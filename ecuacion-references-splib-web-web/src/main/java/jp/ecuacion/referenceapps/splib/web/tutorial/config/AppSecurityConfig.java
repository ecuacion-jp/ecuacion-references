/*
 * Copyright © 2012 ecuacion.jp (info@ecuacion.jp)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.ecuacion.referenceapps.splib.web.tutorial.config;

import java.util.List;
import jp.ecuacion.splib.core.bean.AuthorizationBean;
import jp.ecuacion.splib.web.config.SplibWebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibWebSecurityConfig {

  protected AppSecurityConfig() {
    super(null, null, null);
  }

  @Override
  protected String getDefaultSuccessUrl() {
    return "/afterLogin/default";
  }

  @Override
  protected List<AuthorizationBean> getRoleInfo() {
    return List.of();
  }

  @Override
  protected List<AuthorizationBean> getAuthorityInfo() {
    return List.of();
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
