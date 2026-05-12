package jp.ecuacion.referenceapps.splib.web.tutorial.base.record;

import jp.ecuacion.referenceapps.splib.web.tutorial.base.entity.Book;
import jp.ecuacion.lib.core.annotation.ItemNameKeyClass;
import jp.ecuacion.lib.core.item.*;
import jp.ecuacion.lib.core.util.StringUtil;
import jp.ecuacion.lib.validation.constraints.*;
import jp.ecuacion.splib.core.container.*;

@ItemNameKeyClass("book")
public abstract class BookBaseRecord extends SystemCommonBaseRecord implements ItemContainer {

  @LongString
  protected String id;
  @SizeString(min = 1, max = 30)
  @PatternWithDescription(regexp = "^[^!\"#\\$%&\\(\\)=\\^~\\\\\\|`\\[\\{;\\+:\\\\*\\]\\},<>/\\?]*$", description = "prohibitedChars")
  protected String name;

  static {
    getStringLengthMap().put("id", null);
    getStringLengthMap().put("name", 30);
  }

  public BookBaseRecord() {
    super();
  }

  public BookBaseRecord(Book e, DatetimeFormatParameters params) {
    super(e, params);
    this.id = (e.getId() == null) ? "" : Long.toString(e.getId());
    this.name = e.getName();
  }

  public BookBaseRecord(BookBaseRecord rec) {
    super(rec);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getIdOfEntityDataType() {
    return (getId() == null || getId().equals("")) ? null : Long.valueOf(id.replaceAll(",", ""));
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIds() {
    return StringUtil.getSeparatedValuesString(new String[] {getId() == null ? "" : getId()}, "-");
  }

  public void setIds(String idCsv) {
    String[] ids = idCsv.split("-");
    if (ids.length < 1) return;

    setId(ids[0]);
  }

  public String getOptimisticLockVersions() {
    return StringUtil.getSeparatedValuesString(new String[] {getVersion() == null ? "" : getVersion()}, "-");
  }

  public void setOptimisticLockVersions(String versionCsv) {
    String[] versions = versionCsv.split("-");
    if (versions.length < 1) return;

    setVersion(versions[0]);
  }
}
