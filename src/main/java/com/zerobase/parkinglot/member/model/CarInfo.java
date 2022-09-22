package com.zerobase.parkinglot.member.model;

import com.zerobase.parkinglot.member.entity.Member;
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
public class CarInfo {

    private Long id;
    private String carNumber;

    public static List<CarInfo> listFrom(List<CarDto> carDtoList) {

        List<CarInfo> carInfoList = new ArrayList<>();

        for (CarDto carDto : carDtoList) {
            carInfoList.add(CarInfo.builder()
                .carNumber(carDto.getCarNumber())
                .build()
            );
        }

        return carInfoList;
    }
}
