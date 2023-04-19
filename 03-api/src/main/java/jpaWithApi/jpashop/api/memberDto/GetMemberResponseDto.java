package jpaWithApi.jpashop.api.memberDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class GetMemberResponseDto {
    private List<MemberDto> data;
    private int count;
    private LocalDateTime responseTime;

    @Data
    @AllArgsConstructor
    public static class MemberDto {
        private Long id;
        private String name;
    }

}
