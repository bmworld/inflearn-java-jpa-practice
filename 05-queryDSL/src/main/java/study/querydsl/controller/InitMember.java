package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Member;
import study.querydsl.domain.Team;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {
  
  private final InitMemberService initMemberService;
  
  
  /**
   * <h3>주의사항</h3>
   * <pre>
   *   초기 데이터 추가 시, Spring LifeCycle 인하여,
   *   `@PostConstructor`, `@Transactional` 분리되도록 해야한다.</pre>
   */
  @PostConstruct
  public void init() {
    initMemberService.init();
  }
  
  
  @Component
  static class InitMemberService {
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public void init() {
      Team teamA = new Team("teamA");
      Team teamB = new Team("teamB");
      
      em.persist(teamA);
      em.persist(teamB);
      
      for (int i = 0; i < 100; i++) {
        Team selectedTeam = i % 2 == 0 ? teamA : teamB;
        int age = 10 + i;
        Member member = new Member("member" + i + 1, age, selectedTeam);
        em.persist(member);
      }
    }
  }
}
