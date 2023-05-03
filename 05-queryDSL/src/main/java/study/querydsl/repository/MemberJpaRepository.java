package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import study.querydsl.domain.Member;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.domain.QMember.member;
import static study.querydsl.domain.QTeam.team;

@Repository
public class MemberJpaRepository {
  
  private final EntityManager em;
  private final JPAQueryFactory queryFactory;
  
  public MemberJpaRepository(EntityManager em) {
    this.em = em;
    this.queryFactory = new JPAQueryFactory(em);
  }
  
  public void save(Member member) {
    em.persist(member);
  }
  
  public Optional<Member> findById(Long id) {
    Member foundMember = em.find(Member.class, id);
    return Optional.ofNullable(foundMember);
  }
  
  public List<Member> findAll() {
    return em.createQuery("select m from Member m", Member.class)
        .getResultList();
  }
  
  
  public List<Member> findAll_querydsl() {
    return queryFactory
        .select(member)
        .from(member)
        .fetch();
    
  }
  
  public List<Member> findByName(String name) {
    return em.createQuery("select m from Member m where m.name = : name", Member.class)
        .setParameter("name", name)
        .getResultList();
  }
  
  public List<Member> findByName_querydsl(String name) {
    return queryFactory
        .selectFrom(member)
        .where(member.name.eq(name))
        .fetch();
  }
  
  
  public List<MemberTeamDto> searchByBuilder(MemberSearchCondition cond) {
    BooleanBuilder builder = new BooleanBuilder();
    if (hasText(cond.getName())) {
      builder.and(member.name.eq(cond.getName()));
    }
    
    if (hasText(cond.getTeamName())) {
      builder.and(team.name.eq(cond.getTeamName()));
    }
    
    if (cond.getAgeGoe() != null) {
      builder.and(member.age.goe(cond.getAgeGoe()));
      
    }
    
    if (cond.getAgeLoe() != null) {
      builder.and(member.age.loe(cond.getAgeLoe()));
      
    }
    
    
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
        .where(builder)
        .fetch();
  }
  
  
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
  
  
  public List<Member> searchMemberByWhereCond(MemberSearchCondition cond) {
    return queryFactory
        .selectFrom(member)
        .leftJoin(member.team, team)
        .where(
            userNameContains(cond.getName()),
            teamNameEq(cond.getTeamName()),
            ageBetween(cond.getAgeLoe(), cond.getAgeGoe())
        )
        .fetch();
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
