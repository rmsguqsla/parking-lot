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
public class ParkingLotUserInfo {

    private Long id;

    private String name;

    private String address;

    private int spaceCount;

    private double distance;
}
