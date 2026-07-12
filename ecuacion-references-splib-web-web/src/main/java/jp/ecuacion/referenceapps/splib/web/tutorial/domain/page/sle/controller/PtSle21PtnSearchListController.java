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
package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.controller;

import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form.PtSle21PtnListForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.form.PtSle21PtnSearchForm;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.sle.service.PtSle21PtnSearchListService;
import jp.ecuacion.splib.web.controller.SplibSearchListController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//@formatter:off
@Controller
@Scope("prototype")
@RequestMapping("/public/ptSle21Ptn/searchList")
public class PtSle21PtnSearchListController extends SplibSearchListController
    <PtSle21PtnSearchForm, PtSle21PtnListForm, PtSle21PtnSearchListService> {
  //@formatter:on

  public PtSle21PtnSearchListController() {
    super("ptSle21Ptn", newContext().mainRootRecordName("month").functionKinds("02-page"));
  }
}
