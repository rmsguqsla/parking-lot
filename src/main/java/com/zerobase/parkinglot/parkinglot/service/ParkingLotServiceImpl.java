package com.zerobase.parkinglot.parkinglot.service;

import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import com.zerobase.parkinglot.parkinglot.repository.ParkingLotRepository;
import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.utils.GeoCodingUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl implements ParkingLotService{

    private final ParkingLotRepository parkingLotRepository;

    @Override
    public ParkingLotDto parkingLotRegister(String name, String address, int spaceCount) {

        // 주소를 위도, 경도로 변환
        double[] coordinate = GeoCodingUtil.getGeoCode(address);
        double lat = coordinate[0];
        double lng = coordinate[1];

        return ParkingLotDto.fromEntity(
            parkingLotRepository.save(
                ParkingLot.builder()
                    .name(name)
                    .address(address)
                    .lat(lat)
                    .lng(lng)
                    .spaceCount(spaceCount)
                    .regDt(LocalDateTime.now())
                    .useYn(true)
                    .build()
            )
        );

    }

    @Override
    public List<ParkingLotDto> getParkingLots() {

        return ParkingLotDto.fromEntityList(
            parkingLotRepository.findAll()
        );

    }

    @Override
    public ParkingLotDto parkingLotUpdate(Long id, String name,
        String address, int spaceCount, boolean useYn) {

        ParkingLot parkingLot = findParkingLotById(id);

        double[] coordinate = GeoCodingUtil.getGeoCode(address);
        double lat = coordinate[0];
        double lng = coordinate[1];

        parkingLot.setName(name);
        parkingLot.setAddress(address);
        parkingLot.setLat(lat);
        parkingLot.setLng(lng);
        parkingLot.setSpaceCount(spaceCount);
        parkingLot.setUseYn(useYn);
        parkingLot.setUpdateDt(LocalDateTime.now());

        return ParkingLotDto.fromEntity(
            parkingLotRepository.save(parkingLot)
        );

    }

    @Override
    public ParkingLotDto getParkingLot(Long id) {
        return ParkingLotDto.fromEntity(findParkingLotById(id));
    }

    private ParkingLot findParkingLotById(Long id) {
        return parkingLotRepository.findById(id)
            .orElseThrow(() -> new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));
    }
}
