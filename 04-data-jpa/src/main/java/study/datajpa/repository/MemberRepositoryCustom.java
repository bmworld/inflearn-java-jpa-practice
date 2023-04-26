package study.datajpa.repository;

import study.datajpa.domain.Member;

import java.util.List;


/**
 * [실무 필수] <br>
 * QueryDSL 사용 시, Custom Repository 방식을 자주 사용한다.
 */
public interface MemberRepositoryCustom {


  List<Member> findMemberCustom();

}
