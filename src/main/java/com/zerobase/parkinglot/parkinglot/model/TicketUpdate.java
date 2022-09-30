package com.zerobase.parkinglot.parkinglot.model;

import java.time.LocalTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TicketUpdate {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "이용권 이름 입력은 필수 입니다.")
        private String name;
        @Min(value = 1000, message = "이용권 가격은 1000원 이상 입력하세요.")
        private int fee;
        private int startHour;
        private int startMinute;
        private int startSecond;
        private int endHour;
        private int endMinute;
        private int endSecond;
        private int maxHour;
        private int maxMinute;
        private int maxSecond;
        private boolean holidayYn;
        private boolean useYn;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String parkingLotName;
        private String name;
        private int fee;
        private LocalTime startUsableTime;
        private LocalTime endUsableTime;
        private boolean holidayYn;
        private boolean useYn;

        public static Response from(TicketDto ticketDto) {
            return Response.builder()
                .parkingLotName(ticketDto.getParkingLot().getName())
                .name(ticketDto.getName())
                .fee(ticketDto.getFee())
                .startUsableTime(ticketDto.getStartUsableTime())
                .endUsableTime(ticketDto.getEndUsableTime())
                .holidayYn(ticketDto.isHolidayYn())
                .useYn(ticketDto.isUseYn())
                .build();
        }
    }

}
