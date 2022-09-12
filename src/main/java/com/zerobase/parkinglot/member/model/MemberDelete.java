package com.zerobase.parkinglot.member.model;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberDelete {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

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
        private String message;

        public static Response delete() {
            return Response.builder()
                .message("회원삭제 되었습니다.")
                .build();
        }
    }
}
