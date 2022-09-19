package com.zerobase.parkinglot.parkinglot.service;

import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import java.util.List;

public interface ParkingLotService {

    ParkingLotDto parkingLotRegister(String name, String address, int spaceCount);

    List<ParkingLotDto> getParkingLots();

    ParkingLotDto parkingLotUpdate(Long id, String name, String address, int spaceCount, boolean useYn);

    ParkingLotDto getParkingLot(Long id);
}
