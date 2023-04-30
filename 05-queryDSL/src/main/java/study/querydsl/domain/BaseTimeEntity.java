package study.querydsl.domain;


import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

  @Column(updatable = false)
  @CreatedDate
  private LocalDateTime createDate;

  @LastModifiedDate
  private LocalDateTime lastModifiedDate;

  @Column(updatable = false)
  @CreatedBy
  private String createdBy; // 수정자 활성화 방법 => xxApplication.java > AuditorAware 처리해줘야함.

  @Column(updatable = false)
  @LastModifiedBy
  private String lastModifiedBy;
}
