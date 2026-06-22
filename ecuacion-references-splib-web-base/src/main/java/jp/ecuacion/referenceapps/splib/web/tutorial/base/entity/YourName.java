package jp.ecuacion.referenceapps.splib.web.tutorial.base.entity;

import org.jspecify.annotations.NonNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.YourNameBaseRecord;
import jp.ecuacion.lib.validation.constraints.*;

@Entity
@Table(name = "YOUR_NAME", uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
public final class YourName extends SystemCommon implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "ID", nullable = false, columnDefinition = "bigserial")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "YOUR_NAME_ID_SEQ_GEN")
  @SequenceGenerator(name = "YOUR_NAME_ID_SEQ_GEN", sequenceName = "YOUR_NAME_ID_SEQ", allocationSize = 1)
  protected Long id;

  @NotEmpty
  @SizeString(min = 1, max = 30)
  @PatternWithDescription(regexp = "^[^$%&=\\^~,<>/\\?]*$", description = "yourName")
  @Column(name = "NAME", nullable = false, length = 30)
  protected String name;

  public static final String FIELD_ID = "id";
  public static final String FIELD_NAME = "name";

  @Override
  public String[] getFieldNameArr() {
    return new String[] {"id", "name", "createAccId", "createTime", "lstUpdAccId", "lstUpdTime", "isDeleted", "version"};
  }

  public YourName() {}

  public YourName(YourNameBaseRecord rec) {
    super(rec);

    if (rec.getId() != null) setId(rec.getIdOfEntityDataType());
    if (rec.getName() != null) setName(rec.getName());
  }

  public YourName(String name) {
    this();
    setName(name);
  }

  public void update(YourNameBaseRecord rec, String... skipUpdateFields) {
    List<String> skipUpdateFieldList = Arrays.asList(skipUpdateFields);

    if (rec.getId() != null && !skipUpdateFieldList.contains(FIELD_ID)) setId(rec.getIdOfEntityDataType());
    if (rec.getName() != null && !skipUpdateFieldList.contains(FIELD_NAME)) setName(rec.getName());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getNaturalKeyFieldList() {
    List<String> rtnList = new ArrayList<>();
    rtnList.add("name");
    return rtnList;
  }

  @NonNull
  public Set<List<String>> getSetOfUniqueConstraintFieldList() {
    Set<List<String>> rtnSet = new HashSet<>();
    List<String> list = getNaturalKeyFieldList();
    if (list != null) {
      rtnSet.add(list);
    }

    return rtnSet;
  }

}
