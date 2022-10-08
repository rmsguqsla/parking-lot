package com.zerobase.parkinglot.member.model;

import com.zerobase.parkinglot.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    private String email;

    private String name;

    private String phone;

    private String role;

    private LocalDateTime regDt;

    private LocalDateTime updateDt;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
            .email(member.getEmail())
            .name(member.getName())
            .phone(member.getPhone())
            .role(member.getRole())
            .regDt(member.getRegDt())
            .updateDt(member.getUpdateDt())
            .build();
    }
}
