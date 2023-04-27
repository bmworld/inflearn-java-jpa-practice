package study.datajpa.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Item implements Persistable<String> {

  /**
   * - @GeneratedValue 대신 `ID`를 직접할당하려면 ? <br><br>
   *  -> JPA의 영속성 Context > `persist` & `merge`의 개념을 이해해함. <br>
   *     Persistable 인터페이스 상속 & `isNew()` 메서드 구현해야함. <br>
   *     => How? createdDate를 사용 (강사님 추천)
   */

  @Id
  // @GeneratedValue
  private String id;

  @CreatedDate
  private LocalDateTime createdDate;

  public Item(String id) {
    this.id = id;
  }

  @Override
  public boolean isNew() {
    return this.createdDate == null;
  }
}
