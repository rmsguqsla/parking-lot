package com.zerobase.parkinglot.parkinglot.model;

import java.time.LocalDateTime;
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
public class ParkingLotInfo {

    private Long id;

    private String name;

    private String address;

    private int spaceCount;

    private double lat;

    private double lng;

    private LocalDateTime regDt;

    private LocalDateTime updateDt;

    private boolean useYn;

    public static List<ParkingLotInfo> listFrom(List<ParkingLotDto> parkingLotDtoList) {

        List<ParkingLotInfo> list = new ArrayList<>();

        for (ParkingLotDto dto : parkingLotDtoList) {
            list.add(from(dto));
        }

        return list;

    }

    public static ParkingLotInfo from(ParkingLotDto dto) {

        return ParkingLotInfo.builder()
            .id(dto.getId())
            .name(dto.getName())
            .address(dto.getAddress())
            .spaceCount(dto.getSpaceCount())
            .lat(dto.getLat())
            .lng(dto.getLng())
            .regDt(dto.getRegDt())
            .updateDt(dto.getUpdateDt())
            .useYn(dto.isUseYn())
            .build();

    }
}
