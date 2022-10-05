package com.zerobase.parkinglot.reserve.model;

import com.zerobase.parkinglot.aop.ParkingLotIdInterface;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PathVariable;

public class ReserveRegister {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request implements ParkingLotIdInterface {
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
