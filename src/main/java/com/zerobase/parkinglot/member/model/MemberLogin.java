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

public class MemberLogin {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        @Email(message = "이메일 형식으로 입력해 주세요.")
        @NotBlank(message = "이메일 입력은 필수 입니다.")
        private String email;

        @Size(min = 4, message = "비밀번호는 4자 이상 입력해야 합니다.")
        @NotBlank(message = "비밀번호 입력은 필수 입니다.")
        private String password;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String token;
        public static Response token(String token) {
            return Response.builder()
                .token(token)
                .build();
        }
    }
}
