package com.zerobase.parkinglot.parkinglot.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchType {

    PARKING_LOT_NAME("name"),
    PARKING_LOT_ADDRESS("address");

    private final String description;
}
