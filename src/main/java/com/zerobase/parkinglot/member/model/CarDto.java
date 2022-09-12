package com.zerobase.parkinglot.member.model;

import com.zerobase.parkinglot.member.entity.Car;
import com.zerobase.parkinglot.member.entity.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDto {

    private Member member;

    private String carNumber;

    private LocalDateTime regDt;

    private LocalDateTime updateDt;

    public static CarDto fromEntity(Car car) {
        return CarDto.builder()
            .member(car.getMember())
            .carNumber(car.getCarNumber())
            .regDt(car.getRegDt())
            .updateDt(car.getUpdateDt())
            .build();
    }

    public static List<CarDto> fromEntityList(List<Car> carList) {

        List<CarDto> carDtoList = new ArrayList<>();

        for (Car car : carList) {
            CarDto carDto = CarDto.fromEntity(car);
            carDtoList.add(carDto);
        }

        return carDtoList;
    }
}
