package com.zerobase.parkinglot.parkinglot.controller;

import com.zerobase.parkinglot.parkinglot.model.ParkingLotInfo;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotRegister;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUpdate;
import com.zerobase.parkinglot.parkinglot.model.TicketInfo;
import com.zerobase.parkinglot.parkinglot.model.TicketRegister;
import com.zerobase.parkinglot.parkinglot.model.TicketUpdate;
import com.zerobase.parkinglot.parkinglot.service.ParkingLotService;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotRegister.Response;
import java.time.LocalTime;
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

    // 이용권 등록 API
    @PostMapping("/api/admin/parking-lot/{id}/ticket")
    public TicketRegister.Response ticketRegister(
        @PathVariable Long id,
        @RequestBody @Valid TicketRegister.Request request) {

        return TicketRegister.Response.from(
            parkingLotService.ticketRegister(
                id, request.getName(), request.getFee(),
                convertLocalTime(
                    request.getStartHour(),
                    request.getStartMinute(),
                    request.getStartSecond()
                ),
                convertLocalTime(
                    request.getEndHour(),
                    request.getEndMinute(),
                    request.getEndSecond()
                ),
                request.isHolidayYn()
            )
        );
    }

    // 이용권 목록 API
    @GetMapping("/api/admin/parking-lot/{id}/tickets")
    public List<TicketInfo> getTickets (@PathVariable Long id) {
        return TicketInfo.listFrom(parkingLotService.getTickets(id));
    }

    // 이용권 상세 API
    @GetMapping("/api/admin/parking-lot/{parkingLotId}/ticket/{ticketId}")
    public TicketInfo getTicket (@PathVariable Long parkingLotId,
        @PathVariable Long ticketId) {
        return TicketInfo.from(parkingLotService.getTicket(parkingLotId, ticketId));
    }

    // 이용권 수정(식제) API
    @PutMapping("/api/admin/parking-lot/{parkingLotId}/ticket/{ticketId}")
    public TicketUpdate.Response ticketUpdate (
        @PathVariable(value = "parkingLotId") Long parkingLotId,
        @PathVariable(value = "ticketId") Long ticketId,
        @RequestBody @Valid TicketUpdate.Request request
    ) {

        return TicketUpdate.Response.from(
            parkingLotService.ticketUpdate(
                parkingLotId, ticketId, request.getName(), request.getFee(),
                convertLocalTime(
                    request.getStartHour(),
                    request.getStartMinute(),
                    request.getStartSecond()
                ),
                convertLocalTime(
                    request.getEndHour(),
                    request.getEndMinute(),
                    request.getEndSecond()
                ),
                request.isHolidayYn(),
                request.isUseYn()
            )
        );

    }

    private LocalTime convertLocalTime(int hour, int minute, int second) {
        return LocalTime.of(hour, minute, second, 0);
    }
}
