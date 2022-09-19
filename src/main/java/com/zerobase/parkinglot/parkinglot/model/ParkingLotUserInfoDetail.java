package com.zerobase.parkinglot.parkinglot.model;

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

    public static ParkingLotUserInfoDetail from(ParkingLotDto dto, int reservedCount) {
        return ParkingLotUserInfoDetail.builder()
            .id(dto.getId())
            .name(dto.getName())
            .address(dto.getAddress())
            .spaceCount(dto.getSpaceCount())
            .remainCount(dto.getSpaceCount() - reservedCount)
            .build();
    }
}
