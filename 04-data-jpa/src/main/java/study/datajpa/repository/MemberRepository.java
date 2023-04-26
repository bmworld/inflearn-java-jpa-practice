package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.domain.Member;
import study.datajpa.dto.MemberDto;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

  List<Member> findByNameAndAgeGreaterThan(String name, int age);

  List<Member> findHelloooooowBy();
  List<Member> findTop3HelloBy();

  @Query("select m from Member m where m.name = :name and m.age = :age")
  List<Member> findUser(@Param("name") String name, @Param("age") int age);


  @Query("select m.name from Member m")
  List<String> findUserNameList();


  @Query("select new study.datajpa.dto.MemberDto(m.id, m.name, t.name) from Member m join m.team t")
  List<MemberDto> findMemberDto();


  @Query("select m from Member m where m.name in :names")
  List<Member> findByNames(@Param("names") Collection<String> names);



  // 반환타입 >  유연하게 지정할 수 있다.

  /**
   * List 타입 반환 시, null값이 아닌, "Empty Collection" 반환함
   * => 항상 Collection 값 반환을 JPA가 보장해준다.
   */
  List<Member> findMemberListByName(String name); // 컬렉션
  Member findMemberByName(String name); // 단건
  Optional<Member> findOptionalMemberByName(String name); // 단건 Optional


  /**
   * Paging 유의사항 > `Count Query`를 분리하시라.
   * <p>페이징 시, 데이터 가져오는 것은 큰 데이터가 들지 않는다.</p>
   * <p>다만, totalCount 계산은 데이터가 많아질 수록 데이터소비가 크다.</p>
   */
  @Query(value = "select m from Member m left join m.team t",
      countQuery = "select count(m) from Member m"
  )
  Page<Member> findMemberByAge(int age, Pageable pageable);

  Slice<Member> findMemberUsingSliceByAge(int age, Pageable pageable);


  /**
   * Annotation @Modifying <br>
   *  - executeUpdate() 실행 <br>
   *  - 미사용 시, getResultList() 또는 getSingleResult() 실행 <br>
   *  - @Modifying(clearAutomatically = true): 쿼리 실행 후,  em.clear() 실행
   */
  @Modifying
  @Query(value = "UPDATE Member m SET m.age = m.age +1 WHERE m.age >= :age")
  int bulkAgePlus(@Param("age") int age);


  /**
   * [중요] Fetch Join
   */
  @Query(value = "SELECT m FROM Member m LEFT JOIN FETCH m.team")
  List<Member> findAllByFetchJoin();


  /**
   * `@EntityGraph` 사용 후, attributePaths에 연동되는 Entity Field 입력 시, `FETCH JOIN` 적용됨
   */
  @EntityGraph(attributePaths = {"team"})
  @Query(value = "SELECT m FROM Member m LEFT JOIN FETCH m.team")
  List<Member> findAllByFetchJoinViaEntityGraph();


  /**
   * [성능튜닝] queryHints -> ReadOnly 기능 적용 가능 <br>
   * 단, 이건 남발하지말 것 <br>
   * 언제 쓰나 ? 캐시서버를 사용하기에는 애매하게 많은 트래픽이고, 특히 성능이 안나오는 케이스
   */
  @QueryHints(value = @QueryHint(name ="org.hibernate.readOnly", value = "true"))
  @Query(value = "SELECT m FROM Member m")
  Member findByNameViaQueryHintsAsReadOnly(String name);


  /**
   * [성능튜닝] -> JPA가 제공하는 DB SQL Lock 기능을 사용할 수 있다. <br>
   * [EXAMPLE] select ... from member for update <------ `for update` 이게 Lock 기능이다.
   *
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(value = "SELECT m FROM Member m")
  Member findMemberViaJPALock(String name);


}
