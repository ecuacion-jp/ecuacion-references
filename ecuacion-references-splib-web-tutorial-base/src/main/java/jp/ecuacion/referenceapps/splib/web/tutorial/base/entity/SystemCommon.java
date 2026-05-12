package jp.ecuacion.referenceapps.splib.web.tutorial.base.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.*;
import jp.ecuacion.referenceapps.splib.web.tutorial.base.record.SystemCommonBaseRecord;
import jp.ecuacion.splib.jpa.entity.SplibEntity;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.*;

@FilterDef(name = "softDeleteFilter", defaultCondition = "IS_DELETED = false")
@Filter(name = "softDeleteFilter")
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class SystemCommon extends SplibEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @CreatedBy
  @Column(name = "CREATE_ACC_ID", nullable = false)
  protected Long createAccId;

  @CreatedDate
  @Column(name = "CREATE_TIME", nullable = false, columnDefinition = "timestamp with time zone")
  protected OffsetDateTime createTime;

  @LastModifiedBy
  @Column(name = "LST_UPD_ACC_ID", nullable = false)
  protected Long lstUpdAccId;

  @LastModifiedDate
  @Column(name = "LST_UPD_TIME", nullable = false, columnDefinition = "timestamp with time zone")
  protected OffsetDateTime lstUpdTime;

  @Column(name = "IS_DELETED", nullable = false)
  protected Boolean isDeleted;

  @Version
  @Column(name = "VERSION", nullable = false)
  protected Long version;

  public static final String FIELD_CREATE_ACC_ID = "createAccId";
  public static final String FIELD_CREATE_TIME = "createTime";
  public static final String FIELD_LST_UPD_ACC_ID = "lstUpdAccId";
  public static final String FIELD_LST_UPD_TIME = "lstUpdTime";
  public static final String FIELD_IS_DELETED = "isDeleted";
  public static final String FIELD_VERSION = "version";

  public SystemCommon() {}

  public SystemCommon(SystemCommonBaseRecord rec) {
    super();

    if (rec.getCreateAccId() != null) setCreateAccId(rec.getCreateAccIdOfEntityDataType());
    if (createTime != null) setCreateTime(createTime);
    if (rec.getLstUpdAccId() != null) setLstUpdAccId(rec.getLstUpdAccIdOfEntityDataType());
    if (lstUpdTime != null) setLstUpdTime(lstUpdTime);
    if (rec.getIsDeleted() != null) setIsDeleted(rec.getIsDeleted());
    if (rec.getVersion() != null) setVersion(rec.getVersionOfEntityDataType());
  }

  public Long getCreateAccId() {
    return createAccId;
  }

  public void setCreateAccId(Long createAccId) {
    this.createAccId = createAccId;
  }

  public OffsetDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(OffsetDateTime createTime) {
    this.createTime = createTime;
  }

  public Long getLstUpdAccId() {
    return lstUpdAccId;
  }

  public void setLstUpdAccId(Long lstUpdAccId) {
    this.lstUpdAccId = lstUpdAccId;
  }

  public OffsetDateTime getLstUpdTime() {
    return lstUpdTime;
  }

  public void setLstUpdTime(OffsetDateTime lstUpdTime) {
    this.lstUpdTime = lstUpdTime;
  }

  public Boolean getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  @PrePersist
  public void preInsert() {
    createTime = OffsetDateTime.now();
    lstUpdTime = OffsetDateTime.now();
    if (isDeleted == null) isDeleted = false;
    if (version == null) version = 1L;
  }

  @PreUpdate
  public void preUpdate() {
    lstUpdTime = OffsetDateTime.now();
  }

  public boolean hasSoftDeleteField() {
    return true;
  }
}
