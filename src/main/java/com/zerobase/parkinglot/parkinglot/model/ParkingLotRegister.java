package com.zerobase.parkinglot.parkinglot.model;

import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ParkingLotRegister {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "주차장명 입력은 필수 입니다.")
        private String name;
        @NotBlank(message = "주소 입력은 필수 입니다.")
        private String address;
        @Min(1)
        private int spaceCount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String name;
        private String address;
        private int spaceCount;
        private double lat;
        private double lng;
        private LocalDateTime regDt;
        private boolean useYn;

        public static Response from(ParkingLotDto parkingLotDto) {
            return Response.builder()
                .name(parkingLotDto.getName())
                .address(parkingLotDto.getAddress())
                .spaceCount(parkingLotDto.getSpaceCount())
                .lat(parkingLotDto.getLat())
                .lng(parkingLotDto.getLng())
                .regDt(parkingLotDto.getRegDt())
                .useYn(parkingLotDto.isUseYn())
                .build();
        }
    }

}
