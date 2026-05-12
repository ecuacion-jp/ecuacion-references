package jp.ecuacion.referenceapps.splib.web.tutorial.base.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.BookBaseRecord;
import jp.ecuacion.lib.validation.constraints.*;

@Entity
@Table(name = "BOOK")
public final class Book extends SystemCommon implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "ID", nullable = false, columnDefinition = "bigserial")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOOK_ID_SEQ_GEN")
  @SequenceGenerator(name = "BOOK_ID_SEQ_GEN", sequenceName = "BOOK_ID_SEQ", allocationSize = 1)
  protected Long id;

  @NotEmpty
  @SizeString(min = 1, max = 30)
  @PatternWithDescription(regexp = "^[^!\"#\\$%&\\(\\)=\\^~\\\\\\|`\\[\\{;\\+:\\\\*\\]\\},<>/\\?]*$", description = "prohibitedChars")
  @Column(name = "NAME", nullable = false, length = 30)
  protected String name;

  public static final String FIELD_ID = "id";
  public static final String FIELD_NAME = "name";

  @Override
  public String[] getFieldNameArr() {
    return new String[] {"id", "name", "createAccId", "createTime", "lstUpdAccId", "lstUpdTime", "isDeleted", "version"};
  }

  public Book() {}

  public Book(BookBaseRecord rec) {
    super(rec);

    if (rec.getId() != null) setId(rec.getIdOfEntityDataType());
    if (rec.getName() != null) setName(rec.getName());
  }

  public void update(BookBaseRecord rec, String... skipUpdateFields) {
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
    return null;
  }

  @Nonnull
  public Set<List<String>> getSetOfUniqueConstraintFieldList() {
    Set<List<String>> rtnSet = new HashSet<>();
    List<String> list = getNaturalKeyFieldList();
    if (list != null) {
      rtnSet.add(list);
    }

    return rtnSet;
  }

}
