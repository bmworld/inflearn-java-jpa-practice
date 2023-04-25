package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;
import study.datajpa.dto.MemberDto;

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





  private void createMembers(List<String> names) {
    for (String name : names) {
      int order = names.indexOf(name) + 1;
      Member m = new Member(name, 10 * order);
      memberRepository.save(m);
    }
  }


  private void createMembers() {
    Member m1 = new Member("USERNAME", 10);
    Member m2 = new Member("USERNAME", 13);
    Member m3 = new Member("USERNAME", 30);

    memberRepository.save(m1);
    memberRepository.save(m2);
    memberRepository.save(m3);
  }


  private void createMembers(List<String> names, int age) {
    for (String name : names) {
      int order = names.indexOf(name) + 1;
      Member m = new Member(name, age );
      memberRepository.save(m);
    }
  }



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
      memberNameList.add("member" + i);
    }
    return memberNameList;
  }

}
