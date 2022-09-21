package com.zerobase.parkinglot.parkinglot.model;

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
public class TicketUserInfo {

    private Long id;

    private Long parkingLotId;

    private String name;

    private int fee;

    private LocalTime startUsableTime;

    private LocalTime endUsableTime;

    public static List<TicketUserInfo> listFrom(List<TicketDto> ticketDtoList) {

        List<TicketUserInfo> list = new ArrayList<>();

        for (TicketDto dto : ticketDtoList) {
            list.add(from(dto));
        }

        return list;

    }

    public static TicketUserInfo from(TicketDto dto) {

        return TicketUserInfo.builder()
            .id(dto.getId())
            .parkingLotId(dto.getParkingLot().getId())
            .name(dto.getName())
            .fee(dto.getFee())
            .startUsableTime(dto.getStartUsableTime())
            .endUsableTime(dto.getEndUsableTime())
            .build();

    }
}
