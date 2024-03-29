package jpaWithApi.jpashop.service;

import jpaWithApi.jpashop.domain.member.Member;
import jpaWithApi.jpashop.repository.MemberRepository;
import jpaWithApi.jpashop.repository.MemberRepository_old;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //Lazy Loading 또한 해당 Annotation이 있을 경우 작동한다.
public class MemberService {


    private final MemberRepository_old memberRepository_old;
    private final MemberRepository memberRepository;


    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // EXCEPTION
//        List<Member> foundMembers = memberRepository_old.findByName(member.getName()); // Non-`spring-data-jpa` ver.
        List<Member> foundMembers = memberRepository.findByName(member.getName());
        memberRepository.count();
        if (!foundMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
//        return memberRepository_old.findOne(memberId); // Non-`spring-data-jpa` ver.
        return memberRepository.findById(memberId).get();
    }

    @Transactional
    public void update(Long id, String name) {
//        Member member = memberRepository_old.findOne(id); // Non-`spring-data-jpa` ver.
        Member member = memberRepository.findById(id).get();
        member.setName(name);
    }
}
