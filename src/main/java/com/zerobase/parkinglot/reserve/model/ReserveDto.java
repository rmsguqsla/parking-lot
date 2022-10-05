package com.zerobase.parkinglot.reserve.model;

import com.zerobase.parkinglot.reserve.entity.Reserve;
import com.zerobase.parkinglot.reserve.type.StatusType;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.bytebuddy.asm.Advice.Local;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReserveDto {
    private Long id;

    private String name;

    private String phone;

    private String carNumber;

    private String parkingLot;

    private String address;

    private String ticket;

    private Integer fee;

    private LocalDateTime minEstimatedDt;

    private LocalDateTime maxEstimatedDt;

    private LocalDateTime reserveDt;

    @Enumerated(value = EnumType.STRING)
    private StatusType status;

    private LocalDateTime cancelDt;

    public static ReserveDto fromEntity(Reserve reserve) {

        return ReserveDto.builder()
            .id(reserve.getId())
            .name(reserve.getName())
            .phone(reserve.getPhone())
            .carNumber(reserve.getCarNumber())
            .parkingLot(reserve.getParkingLot())
            .address(reserve.getAddress())
            .ticket(reserve.getTicket())
            .fee(reserve.getFee())
            .minEstimatedDt(reserve.getMinEstimatedDt())
            .maxEstimatedDt(reserve.getMaxEstimatedDt())
            .reserveDt(reserve.getReserveDt())
            .status(reserve.getStatus())
            .cancelDt(reserve.getCancelDt())
            .build();

    }
}
