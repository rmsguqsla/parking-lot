package com.zerobase.parkinglot.parkinglot.controller;

import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotInfo;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotRegister;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotRegister.Response;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUpdate;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUserInfo;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUserInfoDetail;
import com.zerobase.parkinglot.parkinglot.model.TicketInfo;
import com.zerobase.parkinglot.parkinglot.model.TicketUserInfo;
import com.zerobase.parkinglot.parkinglot.service.ParkingLotService;
import com.zerobase.parkinglot.security.TokenProvider;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @ApiOperation(value = "내 위치와 가까운 거리의 주차장 20개 검색 API")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/parking-lots/around")
    public List<ParkingLotUserInfo> getParkingLotsMyAround(@RequestParam double myLat, @RequestParam double myLng) {

        return parkingLotService.getParkingLotsMyAround(myLat, myLng);

    }

    // 주차장명 또는 주소로 검색한 주차장 목록 API
    @ApiOperation(value = "주차장명 또는 주소를 이용한 주차장 검색 API")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/parking-lots/search")
    public List<ParkingLotUserInfo> getParkingLotsSearch(
        @RequestParam double myLat, @RequestParam double myLng,
        @RequestParam String searchType, @RequestParam String searchValue) {

        return parkingLotService.getParkingLotsSearch(myLat, myLng, searchType, searchValue);

    }

    // 주차장 상세 API
    @ApiOperation(value = "주차장 상세 API")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/parking-lot/{id}")
    public ParkingLotUserInfoDetail getParkingLotUser(@PathVariable Long id) {

        ParkingLotDto parkingLotDto = parkingLotService.getParkingLotWithUseYn(id);

        List<TicketUserInfo> ticketUserInfoList = parkingLotService.getUsableTickets(id);

        return ParkingLotUserInfoDetail.from(parkingLotDto, ticketUserInfoList);
    }

}
