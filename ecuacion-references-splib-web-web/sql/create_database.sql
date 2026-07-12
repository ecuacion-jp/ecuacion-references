--
-- Copyright © 2012 ecuacion.jp (info@ecuacion.jp)
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

--↓↓↓　postgresユーザで設定　↓↓↓--

--DB作成（macOS / Linux の場合は以下のようにしないと文字化ける）
create database ecuacion_references_splib_web_tutorial with encoding 'utf8' template template0;

--接続するDBの変更
\c ecuacion_references_splib_web_tutorial


--ユーザ作成
create user ecuacion_references_splib_web_tutorial with password 'tutorial123' nocreatedb;

--schema作成
CREATE SCHEMA ecuacion_references_splib_web_tutorial;

--権限設定
ALTER SCHEMA ecuacion_references_splib_web_tutorial OWNER TO ecuacion_references_splib_web_tutorial;

--（データ作成ではないが、よく忘れるので）スキーマの切り替え方法
select current_schema();
SET search_path = ecuacion_references_splib_web_tutorial;
select current_schema();
