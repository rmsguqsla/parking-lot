package com.zerobase.parkinglot.parkinglot.model;

import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import com.zerobase.parkinglot.parkinglot.entity.Ticket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
public class TicketInfo {

    private Long id;

    private Long parkingLotId;

    private String name;

    private int fee;

    private LocalTime startUsableTime;

    private LocalTime endUsableTime;

    private LocalTime maxUsableTime;

    private boolean holidayYn;

    private boolean useYn;

    private LocalDateTime regDt;

    private LocalDateTime updateDt;

    public static List<TicketInfo> listFrom(List<TicketDto> ticketDtoList) {

        List<TicketInfo> list = new ArrayList<>();

        for (TicketDto dto : ticketDtoList) {
            list.add(from(dto));
        }

        return list;

    }

    public static TicketInfo from(TicketDto dto) {

        return TicketInfo.builder()
            .id(dto.getId())
            .parkingLotId(dto.getParkingLot().getId())
            .name(dto.getName())
            .fee(dto.getFee())
            .startUsableTime(dto.getStartUsableTime())
            .endUsableTime(dto.getEndUsableTime())
            .maxUsableTime(dto.getMaxUsableTime())
            .holidayYn(dto.isHolidayYn())
            .useYn(dto.isUseYn())
            .regDt(dto.getRegDt())
            .updateDt(dto.getUpdateDt())
            .build();

    }
}
