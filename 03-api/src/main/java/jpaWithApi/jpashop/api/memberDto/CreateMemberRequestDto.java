package jpaWithApi.jpashop.api.memberDto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateMemberRequestDto {
    @NotEmpty
    private String name;
}
