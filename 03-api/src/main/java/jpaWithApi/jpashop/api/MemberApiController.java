package jpaWithApi.jpashop.api;

import jpaWithApi.jpashop.domain.member.Member;
import jpaWithApi.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;



    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        // Entity를 parameter 받지도 말고, 외부에 노출하지도 마시라 => 왜냐면 Entity는 변경될 확률이 높다. => Entity가 변경되는 순간, 관련 API 에러터짐
        // 그러니, API 마다 각각 RequestDTO 만드시라
        Long memberId = memberService.join(member);
        return new CreateMemberResponse(memberId);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        // 꼭 Request DTO를 사용하시라
        // Entity와 Presentation Layer를 분리할 수 있다!
        // Entity가 변경되어도 API Spec이 변하지 않아야 한다.
        Member member = new Member();
        member.setName(request.getName());
        Long memberId = memberService.join(member);
        return new CreateMemberResponse(memberId);
    }



    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }


    @Data
    static class CreateMemberResponse {

        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }

    }
}
