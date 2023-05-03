package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import study.querydsl.domain.Member;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.domain.QMember.member;
import static study.querydsl.domain.QTeam.team;

public class MemberRepositoryImpl implements MemberRepositoryCustom{
  
  private final JPAQueryFactory queryFactory;
  
  public MemberRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }
  
  
  @Override
  public List<MemberTeamDto> searchMemberTeamDtoByWhereCond(MemberSearchCondition cond) {
    return queryFactory
        .select(new QMemberTeamDto(
            member.id.as("memberId"),
            member.name,
            member.age,
            team.id.as("teamId"),
            team.name.as("teamName")
        ))
        .from(member)
        .leftJoin(member.team, team)
        .where(
            userNameContains(cond.getName()),
            teamNameEq(cond.getTeamName()),
            ageGoe(cond.getAgeGoe()),
            ageLoe(cond.getAgeLoe())
        )
        .fetch();
  }
  
  
  /**
   * <h2>최적화 방법 - Count Query & 조회 Query 분리</h2>
   * <pre>
   *   - 데이터가 꽤 많아졌다면, Count Query를 최적화할 필요가 생긴다.
   *     (Count Query에는 JOIN 등...조회 비용이 적지 않다.)
   *   - 상황에 적절하게 Count 와 Data 조회 Query를 분리할 경우, 최적화 할 수 있다.</pre>
   */
  @Override
  public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition cond, Pageable pageable) {
    QueryResults<MemberTeamDto> results = queryFactory
        .select(new QMemberTeamDto(
            member.id.as("memberId"),
            member.name,
            member.age,
            team.id.as("teamId"),
            team.name.as("teamName")
        ))
        .from(member)
        .leftJoin(member.team, team)
        .where(
            userNameContains(cond.getName()),
            teamNameEq(cond.getTeamName()),
            ageGoe(cond.getAgeGoe()),
            ageLoe(cond.getAgeLoe())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetchResults();
    
    List<MemberTeamDto> content = results.getResults();
    
    
    long total = queryFactory
        .selectFrom(member)
        .leftJoin(member.team, team)
        .where(
            userNameContains(cond.getName()),
            teamNameEq(cond.getTeamName()),
            ageGoe(cond.getAgeGoe()),
            ageLoe(cond.getAgeLoe())
        ).fetchCount();
    
    
    return new PageImpl<>(content, pageable, total);
    
  }
  
  @Override
  public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition cond, Pageable pageable) {
    List<MemberTeamDto> content = queryFactory
        .select(new QMemberTeamDto(
            member.id.as("memberId"),
            member.name,
            member.age,
            team.id.as("teamId"),
            team.name.as("teamName")
        ))
        .from(member)
        .leftJoin(member.team, team)
        .where(
            userNameContains(cond.getName()),
            teamNameEq(cond.getTeamName()),
            ageGoe(cond.getAgeGoe()),
            ageLoe(cond.getAgeLoe())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
    
    
    JPAQuery<Member> countQuery = queryFactory
        .select(member)
        .from(member)
        .leftJoin(member.team, team)
        .where(
            userNameContains(cond.getName()),
            teamNameEq(cond.getTeamName()),
            ageGoe(cond.getAgeGoe()),
            ageLoe(cond.getAgeLoe())
        );


    // ** PageableExecutionUtils에서 '특정 조건'에 부합할 경우에만 CountQuery를 실행하도록 한다. **
    // Ex. 페이즈 사이즈보다, content 사이즈가 적어, 그럼 count Query 날리지 않고, Content 개수에 맞게 알아서 계산해줌.
    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
  }
  
  
  
  private BooleanExpression ageBetween(Integer ageLoe, Integer ageGoe) {
    return ageLoe(ageLoe).and(ageGoe(ageGoe));
  }
  
  
  private BooleanExpression userNameContains(String username) {
    return hasText(username) ? member.name.contains(username) : null;
  }
  
  private BooleanExpression teamNameEq(String teamName) {
    return hasText(teamName) ? team.name.eq(teamName) : null;
  }
  
  private BooleanExpression ageGoe(Integer ageGoe) {
    return ageGoe != null ? member.age.goe(ageGoe) : null;
  }
  
  private BooleanExpression ageLoe(Integer ageLoe) {
    return ageLoe != null ? member.age.loe(ageLoe) : null;
  }
}
