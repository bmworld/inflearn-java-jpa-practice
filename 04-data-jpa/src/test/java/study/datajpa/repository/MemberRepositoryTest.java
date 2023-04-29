package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UserNameOnly;
import study.datajpa.dto.UserNameOnlyDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Rollback(false)
@Transactional
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;


  @Autowired
  TeamRepository teamRepository;

  @PersistenceContext
  EntityManager em;

  @DisplayName("testMemberUsingByDataJpaRepository")
  @Test
  public void testMemberUsingByDataJpaRepository() throws Exception{
    // Given
    Member member = new Member("member1");
    Member savedMember = memberRepository.save(member);
    Optional<Member> optionalMember = memberRepository.findById(savedMember.getId());
    Member foundMember = null;
    if (optionalMember.isPresent()) {
      foundMember = optionalMember.get();

    }

    assertThat(foundMember.getId()).isEqualTo(savedMember.getId());
    assertThat(foundMember.getName()).isEqualTo(savedMember.getName());
    assertThat(foundMember).isEqualTo(savedMember);

  }


  @DisplayName("basicCRUD")
  @Test
  public void basicCRUDOfSpringDataJPA() throws Exception{
    // Given
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");

    memberRepository.save(member1);
    memberRepository.save(member2);

    Member foundMember1 = memberRepository.findById(member1.getId()).get();
    Member foundMember2 = memberRepository.findById(member2.getId()).get();

    // 단건 조회 건
    assertThat(foundMember1).isEqualTo(member1);
    assertThat(foundMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberRepository.findAll();
    assertThat(all.size()).isEqualTo(2);

    // 카운트 검증
    long count = memberRepository.count();
    assertThat(count).isEqualTo(2);

    // 삭제 검증
    memberRepository.delete(member1);
    memberRepository.delete(member2);

    long countAfterDelete = memberRepository.count();
    assertThat(countAfterDelete).isEqualTo(0);


  }


  @DisplayName("findByNameAndAgeGreaterThanOfSpringDataJPA")
  @Test
  public void findByNameAndAgeGreaterThanOfSpringDataJPA() throws Exception{
    // Given
    createMembers();


    List<Member> result = memberRepository.findByNameAndAgeGreaterThan("USERNAME", 15);

    assertThat(result.get(0).getName()).isEqualTo("USERNAME");
    assertThat(result.get(0).getAge()).isEqualTo(30);
    assertThat(result.size()).isEqualTo(1);
  }

  @DisplayName("find...By에서 ...에 식별하기 위한 내용(설명)이 들어가도 된다. ")
  @Test
  void findHelloooooowBy() {
    createMembers();
    List<Member> all = memberRepository.findHelloooooowBy();
    assertThat(all.size()).isEqualTo(3);

  }

  @DisplayName("findTop`N`...By: N개만큼의 limit Query가 나간다.")
  @Test
  void findTop3HelloBy() {
    List<Member> all = memberRepository.findTop3HelloBy(); // => ... from member member0_ limit 3;
  }


  @DisplayName("findUser 메서드를 테스트 해보자`")
  @Test
  public void testQueryMethod() throws Exception{
    // Given
    String name = "USERNAME";
    int age = 10;
    Member m1 = createMembersByNameAndAge(name, age);

    // When
    List<Member> foundUsers = memberRepository.findUser(name, age);

    // Then
    assertThat(foundUsers.get(0)).isEqualTo(m1);

  }

  @Test
  void findUserNameList() {
    createMembers();
    List<String> userNameList = memberRepository.findUserNameList();
    for (String s : userNameList) {
      System.out.println("------ s = " + s);
    }
  }


  @Test
  void findMemberDto() {
    Team t1 = new Team("teamA");

    teamRepository.save(t1);

    Member m1 = new Member("memberA");
    m1.setTeam(t1);
    memberRepository.save(m1);

    List<MemberDto> memberDtos = memberRepository.findMemberDto();
    for (MemberDto dto : memberDtos) {
      System.out.println("dto = " + dto);
    }

  }


  @Test
  void findByNames() {
    List<String> userNameList = Arrays.asList("name1", "name2", "name3");
    createMembers(userNameList);

    List<Member> foundMembers = memberRepository.findByNames(userNameList);
    for (Member member : foundMembers) {
      int index = foundMembers.indexOf(member);
//      System.out.println("----- member.getName() = " + member.getName());
//      System.out.println("----- userNameList.get(index) = " + userNameList.get(index));
      assertThat(member.getName()).isEqualTo(userNameList.get(index));
    }
  }

  @Test
  void test_returnType() {
    List<String> userNameList = Arrays.asList("name1", "name2", "name3");
    String name1 = userNameList.get(0);
    createMembers(userNameList);

    List<Member> members = memberRepository.findMemberListByName(name1);
    Member m1 = members.get(0);

    Member m2 = memberRepository.findMemberByName(name1);

    Optional<Member> optionalMemberByName = memberRepository.findOptionalMemberByName(name1);
    if (optionalMemberByName.isPresent()) {
      Member m3 = optionalMemberByName.get();
      assertThat(m1).isEqualTo(m2);
      assertThat(m2).isEqualTo(m3);
    } else {
      throw new IllegalStateException("Check you given condition");
    }
  }

  @Test
  void test_returnType_ListReturnTypeIsAlwaysNotNull() {
    List<String> userNameList = Arrays.asList("name1", "name2", "name3");
    String notExistedNameInMembers = "adflkjasdlkjf";
    createMembers(userNameList);

    List<Member> members = memberRepository.findMemberListByName(notExistedNameInMembers);

    assertThat(members.size()).isEqualTo(0);
    assertThat(members).isNotNull();
  }


  /**
   * API 개발 꿀팁 : Entity -> DTO 변환 쉽게 하기
   * <p> - Page에서 제공하는 Map 사용 </p>
   */
  @Test
  void pagingBySpringDataJPA() {

    // given
    int age = 10;
    int offset = 0;
    int limit = 4;
    int memberTotalCount = 18;
    int totalPageCount = (int) Math.ceil( (double) memberTotalCount / limit);

    createMembers(getMemberNameList(memberTotalCount), age);


    PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "name"));
    // when
    Page<Member> page = memberRepository.findMemberByAge(age, pageRequest);


    // API 개발 꿀팁 => Entity
    Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getName(), null));


    // then
    List<Member> members = page.getContent();
    long totalElements = page.getTotalElements();

    assertThat(members.size()).isEqualTo(limit);
    assertThat(totalElements).isEqualTo(memberTotalCount);
    // 현재 페이지 번호
    assertThat(page.getNumber()).isEqualTo(0);
    // 전체 페이지 수
    assertThat(page.getTotalPages()).isEqualTo(totalPageCount);
    // 첫 페이지 여부
    assertThat(page.isFirst()).isTrue();
    // 다음 페이지 존재여부
    assertThat(page.hasNext()).isTrue();
  }


  /** SLICE에 없는 기능
   * <p> 1. page.getTotalElements() </p>
   * <p> 2. page.getTotalPages() </p>
   * <p> SLICE 반환 시, limit + 1 을 해줌으로서, "더보기" 기능 등에 사용할 수 있게 해놨다. </p>
   */
  @Test
  void pagingBySpringDataJPA_usingBySlice() {

    // given
    int age = 10;
    int offset = 0;
    int limit = 4; //
    int memberTotalCount = 18;
    int totalPageCount = (int) Math.ceil( (double) memberTotalCount / limit);

    createMembers(getMemberNameList(memberTotalCount), age);


    PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "name"));
    // when
    Slice<Member> page = memberRepository.findMemberUsingSliceByAge(age, pageRequest);

    // then
    List<Member> members = page.getContent();


    System.out.println("page.getSize() = " + page.getSize());

    assertThat(members.size()).isEqualTo(limit);
    // 현재 페이지 번호
    assertThat(page.getNumber()).isEqualTo(0);
  }



  @Test
  void bulkAgePlus() {
    // given
    int targetAge = 30;
    createMembersByAges(Arrays.asList(10, 20, targetAge, 40));

    // when
    int resultCount = memberRepository.bulkAgePlus(20); // 조건 충족 시, 나이 + 1

    // then
    assertThat(resultCount).isEqualTo(3);

    
    
    // ##################################################################
    // [꿀팁]  JPA 영속성 context와 "벌크쿼리" 동작방식 이해하기
    // ##################################################################


    // given
    boolean clearPersistenceContext = true; // <- Test with condition change
    if (clearPersistenceContext) {
      // 벌크 쿼리 후, "영속성 Context" 비우고 다시 시작하시라.
      // `@Modifying(clearAutomatically = true)` Annotation으로 대체 가능
      em.clear();
      targetAge = targetAge + 1;
    }

    // when
    Member member3 = memberRepository.findMemberByName("member3");

    // then
    assertThat(member3.getAge()).isEqualTo(targetAge);
    System.out.println("-----영속성 context => target" + targetAge + "  / member3.getAge() = " + member3.getAge());


  }
  
  
  @DisplayName("fetchJoinAndLazyLoading")
  @Test
  public void findMemberByLazyLoading() throws Exception{
    // Given
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");

    teamRepository.save(teamA);
    teamRepository.save(teamB);

    Member mA = new Member("memberA", 10, teamA);
    Member mB = new Member("memberB", 20, teamB);

    memberRepository.save(mA);
    memberRepository.save(mB);

    em.flush();
    em.clear();





    // Then => SQL Query 확인하시라.
    // ################################# NON-FETCH-JOIN #################################

    System.out.println("################################# NON-FETCH-JOIN #################################");
    List<Member> members = memberRepository.findAll();
    for (Member member : members) {
      System.out.println("member.getName() = " + member.getName());
      System.out.println("member.getTeam() = " + member.getTeam().getClass());
      System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }





    // ################################# FETCH-JOIN #################################
    System.out.println("################################# FETCH-JOIN #################################");
    List<Member> membersFromFetchJoin = memberRepository.findAllByFetchJoin();
    for (Member member : membersFromFetchJoin) {
      System.out.println("member.getName() = " + member.getName());
      System.out.println("member.getTeam() = " + member.getTeam().getClass());
      System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }



    // ################################# FETCH-JOIN VIA @EntityGraph(attributePaths = {"entityField"}) #################################
    System.out.println("################################# FETCH-JOIN VIA @EntityGraph(attributePaths = {\"entityField\"})#################################");
    List<Member> membersFromFetchJoinViaEntityGraph = memberRepository.findAllByFetchJoinViaEntityGraph();
    for (Member member : membersFromFetchJoinViaEntityGraph) {
      System.out.println("member.getName() = " + member.getName());
      System.out.println("member.getTeam() = " + member.getTeam().getClass());
      System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }
  
  }


  @DisplayName("[성능튜닝] queryHints -> ReadOnly 기능 적용 가능 -> 단, 이건 남발하지말고, 캐시서버를 사용하지 않고, 특별히 성능이 안나오는 몇몇 케이스에만 적용하시라")
  @Test
  public void queryHints() throws Exception{
    // Given
    String name = "member1";
    Member m1 = new Member(name);
    memberRepository.save(m1);
    em.flush();
    em.clear();
    // When
    Member foundMember = memberRepository.findByNameViaQueryHintsAsReadOnly(name);
    foundMember.setName("newName");

    em.flush();
    // Then

  }



  @DisplayName("[성능튜닝] JPA LOCK")
  @Test
  public void queryViaLock() throws Exception{
    // Given
    String name = "member1";
    Member m1 = new Member(name);
    memberRepository.save(m1);
    em.flush();
    em.clear();
    // When
    Member foundMember = memberRepository.findMemberViaJPALock(name);
    foundMember.setName("newName");

    em.flush();
    // Then

  }

  @DisplayName("Custom repository test")
  @Test
  void findMemberCustom() {
    List<Member> result = memberRepository.findMemberCustom();
    System.out.println(result);
  }





  @DisplayName("[실무에서 사용 X] specificationBasic: JPA Criteria를 사용한 Query ")
  @Test
  public void specificationBasic() throws Exception{
    // Given
    Team team = new Team("teamA");
    em.persist(team);

    String targetName = "name1";
    List<String> userNameList = Arrays.asList(targetName, "name2", "name3");
    createMembers(userNameList, team);

    em.flush();
    em.clear();


    // When
    Specification<Member> spec = MemberSpecification.username(targetName).and(MemberSpecification.teamName("teamA"));
    List<Member> result = memberRepository.findAll(spec);

    // Then
    Member foundMember = result.get(0);
    assertThat(result.size()).isEqualTo(1);
    assertThat(foundMember.getName()).isEqualTo(targetName);
    System.out.println("----- targetName = " + targetName);

  }


  @DisplayName("queryByExample")
  @Test
  public void queryByExample() throws Exception{
    // Given
    String targetTeamName = "teamA";
    Team team = new Team(targetTeamName);
    em.persist(team);

    String targetName = "name1";
    List<String> userNameList = Arrays.asList(targetName, "name2", "name3");
    createMembers(userNameList, team);

    em.flush();
    em.clear();

    // When
    Member member = new Member(targetName, 10, team);

    // 무시할 조건 추가 가능
    ExampleMatcher matcher = ExampleMatcher .matching().withIgnorePaths("age");

    // Query Start.
    Example<Member> example = Example.of(member, matcher);

    List<Member> result = memberRepository.findAll(example);


    // Then
    System.out.println("----- result = " + result);
    Member foundMember = result.get(0);
    assertThat(result.size()).isEqualTo(1);
    assertThat(foundMember.getName()).isEqualTo(targetName);
    assertThat(foundMember.getTeam().getName()).isEqualTo(targetTeamName);
    System.out.println("------ foundMember.getName() = " + foundMember.getName());
    System.out.println("------ foundMember.getTeam().getName() = " + foundMember.getTeam().getName());


  }



  /**
   * Project 구현방법 1. `Interface` 기반
   * Project 구현방법 2. `Class` 기반
   * <br>
   * - 주의: Projection 대상이 Root Entity면, JPQL SELECT 절 최적화 가능
   * <br>
   * - 단점: Projection 대상이 ROOT가 아니고, LEFT OUTER JOIN 처리 시, 최적화 어려움
   * <br>
   *   -> 모든 Field를 SELECT해서, Entity로 조회한 다음 계산함.
   */
  @DisplayName("queryByProjections")
  @Test
  public void queryByProjections() throws Exception{
    // Given
    String targetTeamName = "teamA";
    Team team = new Team(targetTeamName);
    em.persist(team);

    String targetName = "name1";
    List<String> userNameList = Arrays.asList(targetName, "name2", "name3");
    createMembers(userNameList, team);

    em.flush();
    em.clear();

    // When

    List<UserNameOnly> result = memberRepository.findProjectionsInterfaceVersionByName(targetName);
    // Then
    for (UserNameOnly userNameOnly : result) {
      System.out.println("------ userNameOnly = " + userNameOnly);

    }


    // 동적 Projection
    List<UserNameOnlyDto> resultDtos = memberRepository.findProjectionsDtoVersionByName("name1", UserNameOnlyDto.class);
    // Then
    for (UserNameOnlyDto dto : resultDtos) {
      System.out.println("------ dto.getName() = " + dto.getName());
    }


    // Nested Name
    List<NestedClosedProjections> resultWithNestedProjectinosDtos = memberRepository.findProjectionsDtoVersionByName("name1", NestedClosedProjections.class);
    // Then
    for (NestedClosedProjections nestedClosedProjections : resultWithNestedProjectinosDtos) {
      String name = nestedClosedProjections.getName();
      System.out.println("------ name = " + name);
      String teamName = nestedClosedProjections.getTeam().getName();
      System.out.println("------ teamName = " + teamName);
    }

  }


// #################################################################
// #################################################################
// #################################################################
// #################################################################
// #################################################################
// #################################################################


  private Member createMembersByNameAndAge(String name, int age) {
    Member m1 = new Member(name, age);
    Member m2 = new Member("member", 133);

    memberRepository.save(m1);
    memberRepository.save(m2);
    return m1;
  }

  private static List<String> getMemberNameList(int memberTotalCount) {
    List<String> memberNameList = new ArrayList<>();
    for (int i = 0; i < memberTotalCount; i++) {
      int order = i + 1;
      memberNameList.add("member" + order);
    }
    return memberNameList;
  }


  private void createMembers(List<String> names, int age) {
    for (String name : names) {
      Member m = new Member(name, age );
      memberRepository.save(m);
    }
  }

  private void createMembers() {
    Member m1 = new Member("USERNAME", 10);
    Member m2 = new Member("USERNAME", 13);
    Member m3 = new Member("USERNAME", 30);

    memberRepository.save(m2);
    memberRepository.save(m3);
    memberRepository.save(m1);
  }


  private void createMembersByAges(List<Integer> ages) {
    int ageCount = ages.size();

    List<String> names = getMemberNameList(ageCount);

    for (int age : ages) {
      int index = ages.indexOf(age);
      Member m = new Member(names.get(index), age);
      memberRepository.save(m);
    }
  }

  private void createMembers(List<String> names) {
    for (String name : names) {
      int order = names.indexOf(name) + 1;
      Member m = new Member(name, 10 * order);
      memberRepository.save(m);
    }
  }

  private void createMembers(List<String> names, Team team) {
    for (String name : names) {
      int order = names.indexOf(name) + 1;
      Member m = new Member(name, 10 * order, team);
      memberRepository.save(m);
    }
  }
}
