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

public class MemberUpdate {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        private String name;

        @Size(min = 12, message = "전화변호는 12자 이상 입력해야 합니다.")
        private String phone;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String name;
        private String phone;
        private LocalDateTime updateDt;

        public static Response from(MemberDto memberDto) {
            return Response.builder()
                .name(memberDto.getName())
                .phone(memberDto.getPhone())
                .updateDt(memberDto.getUpdateDt())
                .build();
        }
    }
}
