package com.zerobase.parkinglot.reserve.model;

import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReserveRegister {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private Long carId;

        private Long parkingLotId;

        private Long ticketId;

        private Integer estimatedHour;

        private Integer estimatedMinute;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String name;

        private String parkingLot;

        private String address;

        private String ticket;

        private Integer fee;

        private LocalDateTime minEstimatedDt;

        private LocalDateTime maxEstimatedDt;

        private LocalDateTime reserveDt;

        public static ReserveRegister.Response from(ReserveDto reserveDto) {
            return Response.builder()
                .name(reserveDto.getName())
                .parkingLot(reserveDto.getParkingLot())
                .address(reserveDto.getAddress())
                .ticket(reserveDto.getTicket())
                .fee(reserveDto.getFee())
                .minEstimatedDt(reserveDto.getMinEstimatedDt())
                .maxEstimatedDt(reserveDto.getMaxEstimatedDt())
                .reserveDt(reserveDto.getReserveDt())
                .build();
        }
    }

}
