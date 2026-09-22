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
package jp.ecuacion.references.coreformarkdownonlyapps.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/** Application configuration. */
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.web.markdown.config"
    + ", jp.ecuacion.references.coreformarkdownonlyapps.exceptionhandler")
@PropertySources({
  @PropertySource("classpath:application-builtin_core.properties"),
  @PropertySource("classpath:application-builtin.properties")
})
public class AppCommonConfig {

}
