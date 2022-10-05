package com.zerobase.parkinglot.reserve.model;

import com.zerobase.parkinglot.aop.ParkingLotIdInterface;
import com.zerobase.parkinglot.reserve.type.StatusType;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReserveCancel {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request implements ParkingLotIdInterface {
        private Long reserveId;
        private Long parkingLotId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String parkingLot;

        private String address;

        private String ticket;

        private Integer fee;

        @Enumerated(value = EnumType.STRING)
        private StatusType status;

        private LocalDateTime cancelDt;

        public static ReserveCancel.Response from(ReserveDto reserveDto) {
            return Response.builder()
                .parkingLot(reserveDto.getParkingLot())
                .address(reserveDto.getAddress())
                .ticket(reserveDto.getTicket())
                .fee(reserveDto.getFee())
                .status(reserveDto.getStatus())
                .cancelDt(reserveDto.getCancelDt())
                .build();
        }
    }

}
