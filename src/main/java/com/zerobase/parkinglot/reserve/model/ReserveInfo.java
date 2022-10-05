package com.zerobase.parkinglot.reserve.model;

import com.zerobase.parkinglot.reserve.entity.Reserve;
import com.zerobase.parkinglot.reserve.type.StatusType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReserveInfo {

    private Long id;

    private String email;

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

    private LocalDateTime reserveEndDt;

    @Enumerated(value = EnumType.STRING)
    private StatusType status;

    private LocalDateTime cancelDt;

    public static ReserveInfo fromEntity(Reserve reserve) {
        return ReserveInfo.builder()
            .id(reserve.getId())
            .email(reserve.getEmail())
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
            .reserveEndDt(reserve.getReserveEndDt())
            .status(reserve.getStatus())
            .cancelDt(reserve.getCancelDt())
            .build();
    }

    public static List<ReserveInfo> fromEntityList(List<Reserve> reserveList) {
        List<ReserveInfo> reserveInfoList = new ArrayList<>();

        for(Reserve r : reserveList) {
            ReserveInfo reserveInfo = fromEntity(r);
            reserveInfoList.add(reserveInfo);
        }

        return reserveInfoList;
    }
}
