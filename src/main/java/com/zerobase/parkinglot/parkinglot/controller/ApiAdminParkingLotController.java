package com.zerobase.parkinglot.parkinglot.controller;

import com.zerobase.parkinglot.parkinglot.model.ParkingLotInfo;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotRegister;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUpdate;
import com.zerobase.parkinglot.parkinglot.service.ParkingLotService;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotRegister.Response;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiAdminParkingLotController {

    private final ParkingLotService parkingLotService;

    // 주차장 등록 API
    @PostMapping("/api/admin/parking-lot")
    public Response parkingLotRegister(
        @RequestBody @Valid ParkingLotRegister.Request request) {

        return ParkingLotRegister.Response.from(
            parkingLotService.parkingLotRegister(
                request.getName(),
                request.getAddress(),
                request.getSpaceCount()
            )
        );
    }

    // 주차장 목록 API
    @GetMapping("/api/admin/parking-lots")
    public List<ParkingLotInfo> getParkingLots() {

        return ParkingLotInfo.listFrom(parkingLotService.getParkingLots());

    }

    // 주차장 상세 API
    @GetMapping("/api/admin/parking-lot/{id}")
    public ParkingLotInfo getParkingLot(@PathVariable Long id) {

        return ParkingLotInfo.from(parkingLotService.getParkingLot(id));

    }

    // 주차장 수정(삭제) API
    @PutMapping("/api/admin/parking-lot/{id}")
    public ParkingLotUpdate.Response parkingLotUpdate(
        @PathVariable Long id,
        @RequestBody @Valid ParkingLotUpdate.Request request) {

        return ParkingLotUpdate.Response.from(
            parkingLotService.parkingLotUpdate(
                id,
                request.getName(),
                request.getAddress(),
                request.getSpaceCount(),
                request.isUseYn()
            )
        );

    }


}
