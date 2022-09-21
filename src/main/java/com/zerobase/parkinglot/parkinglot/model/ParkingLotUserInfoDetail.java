package com.zerobase.parkinglot.parkinglot.model;

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
public class ParkingLotUserInfoDetail {

    private Long id;

    private String name;

    private String address;

    private int spaceCount;

    private int remainCount;

    private List<TicketUserInfo> ticketInfoList;

    public static ParkingLotUserInfoDetail from(ParkingLotDto dto, int reservedCount,
        List<TicketUserInfo> ticketUserInfoList) {
        return ParkingLotUserInfoDetail.builder()
            .id(dto.getId())
            .name(dto.getName())
            .address(dto.getAddress())
            .spaceCount(dto.getSpaceCount())
            .remainCount(dto.getSpaceCount() - reservedCount)
            .ticketInfoList(ticketUserInfoList)
            .build();
    }
}
