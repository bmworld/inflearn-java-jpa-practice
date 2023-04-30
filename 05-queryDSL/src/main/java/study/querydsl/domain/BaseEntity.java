package study.querydsl.domain;


import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity extends BaseTimeEntity{


  /**
   * 생성자, 수정자 활성화 방법 => xxApplication.java > AuditorAware 처리해줘야함.
   */
  @Column(updatable = false)
  @CreatedBy
  private String createdBy;

  @Column(updatable = false)
  @LastModifiedBy
  private String lastModifiedBy;
}
