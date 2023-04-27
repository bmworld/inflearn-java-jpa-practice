package study.datajpa.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

  @Column(updatable = false, nullable = false)
  private LocalDateTime createDate;

  @Column(nullable = false)
  private LocalDateTime updateDate;

  @PrePersist
  public void prePersist() {
    createDate = now();
    updateDate = now();
  }

  @PreUpdate
  public void preUpdate() {
    updateDate = now();
  }
}
