package jpabook.jpashop.domain;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {
  private String createdBy;
  private LocalDateTime createdDate;
  private String modifiedBy;

  private LocalDateTime modifiedDate;

  public String getCreatedBy() {
    return createdBy;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public LocalDateTime getModifiedDate() {
    return modifiedDate;
  }
}
