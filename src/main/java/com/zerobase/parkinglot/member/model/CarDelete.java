package com.zerobase.parkinglot.member.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CarDelete {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @Size(min = 7, message = "차번호는 7자 이상 입력해야 합니다.")
        @NotBlank(message = "차번호 입력은 필수 입니다.")
        private String carNumber;
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
                .message("차번호 삭제되었습니다.")
                .build();
        }
    }
}
