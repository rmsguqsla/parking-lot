package com.zerobase.parkinglot.member.model;

import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberRegister {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        @Email(message = "이메일 형식으로 입력해 주세요.")
        @NotBlank(message = "이메일 입력은 필수 입니다.")
        private String email;

        @NotBlank(message = "이름 입력은 필수 입니다.")
        private String name;

        @Size(min = 4, message = "비밀번호는 4자 이상 입력해야 합니다.")
        @NotBlank(message = "비밀번호 입력은 필수 입니다.")
        private String password;

        @Size(min = 12, message = "전화변호는 12자 이상 입력해야 합니다.")
        @NotBlank(message = "전화번호 입력은 필수 입니다.")
        private String phone;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String email;
        private LocalDateTime regDt;

        public static Response from(MemberDto memberDto) {
            return Response.builder()
                .email(memberDto.getEmail())
                .regDt(memberDto.getRegDt())
                .build();
        }
    }
}
