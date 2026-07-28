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
package jp.ecuacion.references.splib.tutorial.config;

import jp.ecuacion.splib.rest.config.SplibRestSecurityConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Security config backing the {@code /api/public/hello} quickstart endpoint.
 *
 * <p>This site does not use {@code /api/key/**} or {@code /api/ecuacion-splib/key/**}, so
 *     {@code null} is passed for both key providers, which makes those prefixes deny all
 *     requests.</p>
 */
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig() {
    super(null, null);
  }
}
