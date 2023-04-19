package jpaWithApi.jpashop.api.memberDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateMemberResponseDto {
    private Long id;
    private String name;
}
