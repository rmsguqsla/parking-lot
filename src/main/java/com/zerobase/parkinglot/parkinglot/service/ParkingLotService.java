package com.zerobase.parkinglot.parkinglot.service;

import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUserInfo;
import java.util.List;

public interface ParkingLotService {

    ParkingLotDto parkingLotRegister(String name, String address, int spaceCount);

    List<ParkingLotDto> getParkingLots();

    ParkingLotDto parkingLotUpdate(Long id, String name, String address, int spaceCount, boolean useYn);

    ParkingLotDto getParkingLot(Long id);

    List<ParkingLotUserInfo> getParkingLotsMyAround(double myLat, double myLng);

    List<ParkingLotUserInfo> getParkingLotsSearch(double myLat, double myLng, String searchType, String searchValue);
}
