package study.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import study.querydsl.domain.Member;
import study.querydsl.domain.QMember;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.repository.support.Querydsl4RepositoryCustomSupport;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.domain.QMember.member;
import static study.querydsl.domain.QTeam.team;

@Repository
public class MemberTestRepository extends Querydsl4RepositoryCustomSupport {
  public MemberTestRepository() {
    super(Member.class);
  }
  
  public List<Member> basicSelect() {
    return select(member)
        .from(member)
        .fetch();
  }
  
  public List<Member> basicSelectFrom() {
    return selectFrom(member)
        .fetch();
  }
  
  public Page<Member> searchPageByApplyPage(MemberSearchCondition cond, Pageable pageable) {
    JPAQuery<Member> query = selectFrom(member)
        .leftJoin(member.team, team)
        .where(
            userNameContains(cond.getName()),
            teamNameEq(cond.getTeamName()),
            ageGoe(cond.getAgeGoe()),
            ageLoe(cond.getAgeLoe())
        );
    
    List<Member> content = getQuerydsl().applyPagination(pageable, query).fetch();
    return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    
  }
  
  
  public Page<Member> applyPagination(MemberSearchCondition cond, Pageable pageable) {
    return applyPagination(pageable, query ->
        query
            .selectFrom(member)
            .leftJoin(member.team, team)
            .where(
                userNameContains(cond.getName()),
                teamNameEq(cond.getTeamName()),
                ageGoe(cond.getAgeGoe()),
                ageLoe(cond.getAgeLoe())
            )
    );
    
  }
  
  
  public Page<Member> applyPaginationWithCountQuery(MemberSearchCondition cond, Pageable pageable) {
    return applyPagination(pageable,
        contentQuery -> contentQuery
            .selectFrom(member)
            .leftJoin(member.team, team)
            .where(
                userNameContains(cond.getName()),
                teamNameEq(cond.getTeamName()),
                ageGoe(cond.getAgeGoe()),
                ageLoe(cond.getAgeLoe())
            ),
        countQuery -> countQuery
            .select(member.id)
            .from(member)
            .leftJoin(member.team, team)
            .where(
                userNameContains(cond.getName()),
                teamNameEq(cond.getTeamName()),
                ageGoe(cond.getAgeGoe()),
                ageLoe(cond.getAgeLoe())
            )
    );
    
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
