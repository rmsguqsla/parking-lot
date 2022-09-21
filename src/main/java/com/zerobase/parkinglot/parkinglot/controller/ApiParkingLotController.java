package com.zerobase.parkinglot.parkinglot.controller;

import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotInfo;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotRegister;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotRegister.Response;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUpdate;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUserInfo;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUserInfoDetail;
import com.zerobase.parkinglot.parkinglot.service.ParkingLotService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiParkingLotController {

    private final ParkingLotService parkingLotService;

    // 내 위치와 가장 가까운 거리의 20개 주차장 목록 API
    @GetMapping("/api/parking-lots/my-around")
    public List<ParkingLotUserInfo> getParkingLotsMyAround(@RequestParam double myLat, @RequestParam double myLng) {

        return parkingLotService.getParkingLotsMyAround(myLat, myLng);

    }

    // 주차장명 또는 주소로 검색한 주차장 목록 API
    @GetMapping("/api/parking-lots/search")
    public List<ParkingLotUserInfo> getParkingLotsSearch(
        @RequestParam double myLat, @RequestParam double myLng,
        @RequestParam String searchType, @RequestParam String searchValue) {

        return parkingLotService.getParkingLotsSearch(myLat, myLng, searchType, searchValue);

    }

    // 주차장 상세 API
    @GetMapping("/api/parking-lot/{id}")
    public ParkingLotUserInfoDetail getParkingLotDetail(@PathVariable Long id) {

        ParkingLotDto parkingLotDto = parkingLotService.getParkingLot(id);
        int reservedCount = 0;

        return ParkingLotUserInfoDetail.from(parkingLotDto, reservedCount);
    }

}