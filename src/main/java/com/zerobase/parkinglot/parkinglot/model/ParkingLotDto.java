package com.zerobase.parkinglot.parkinglot.model;

import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ParkingLotDto {

    private Long id;

    private String name;

    private String address;

    private double lat; // 위도

    private double lng; // 경도

    private int spaceCount;

    private int reserveCount;

    private LocalDateTime regDt;

    private LocalDateTime updateDt;

    private boolean useYn;


    public static ParkingLotDto fromEntity(ParkingLot parkingLot) {
        return ParkingLotDto.builder()
            .id(parkingLot.getId())
            .name(parkingLot.getName())
            .address(parkingLot.getAddress())
            .lat(parkingLot.getLat())
            .lng(parkingLot.getLng())
            .spaceCount(parkingLot.getSpaceCount())
            .reserveCount(parkingLot.getReserveCount())
            .regDt(parkingLot.getRegDt())
            .updateDt(parkingLot.getUpdateDt())
            .useYn(parkingLot.isUseYn())
            .build();
    }

    public static List<ParkingLotDto> fromEntityList(List<ParkingLot> parkingLotList) {

        List<ParkingLotDto> parkingLotDtoList = new ArrayList<>();

        for (ParkingLot parkingLot : parkingLotList) {

            parkingLotDtoList.add(ParkingLotDto.fromEntity(parkingLot));

        }

        return parkingLotDtoList;

    }


}
