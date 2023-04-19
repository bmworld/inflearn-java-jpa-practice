package jpaWithApi.jpashop.api;

import jpaWithApi.jpashop.api.memberDto.*;
import jpaWithApi.jpashop.domain.member.Member;
import jpaWithApi.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;


    @GetMapping("/api/v1/members")
    public List<Member> getMembersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public GetMemberResponseDto getMembersV2() {
        List<Member> foundMembers = memberService.findMembers();

        List<GetMemberResponseDto.MemberDto> collect = foundMembers.stream()
                .map((Member t) -> new GetMemberResponseDto.MemberDto(t.getId(), t.getName()))
                .collect(Collectors.toList());

        return new GetMemberResponseDto(collect, collect.size(), LocalDateTime.now());
    }


    @PostMapping("/api/v1/members")
    public CreateMemberResponseDto saveMemberV1(@RequestBody @Valid Member member) {
        // Entity를 parameter 받지도 말고, 외부에 노출하지도 마시라 => 왜냐면 Entity는 변경될 확률이 높다. => Entity가 변경되는 순간, 관련 API 에러터짐
        // 그러니, API 마다 각각 RequestDTO 만드시라23:
        Long memberId = memberService.join(member);
        return new CreateMemberResponseDto(memberId);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponseDto saveMemberV2(@RequestBody @Valid CreateMemberRequestDto request) {
        // 꼭 Request DTO를 사용하시라
        // Entity와 Presentation Layer를 분리할 수 있다!
        // Entity가 변경되어도 API Spec이 변하지 않아야 한다.
        Member member = new Member();
        member.setName(request.getName());
        Long memberId = memberService.join(member);
        return new CreateMemberResponseDto(memberId);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponseDto updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequestDto request
    ) {

        memberService.update(id, request.getName());
        Member foundMember = memberService.findOne(id);


        return new UpdateMemberResponseDto(foundMember.getId(), foundMember.getName());

    }


}
