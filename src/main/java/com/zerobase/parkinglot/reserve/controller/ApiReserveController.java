package com.zerobase.parkinglot.reserve.controller;

import com.zerobase.parkinglot.reserve.model.ReserveCancel;
import com.zerobase.parkinglot.reserve.model.ReserveRegister;
import com.zerobase.parkinglot.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiReserveController {

    private final ReserveService reserveService;

    // 예약
    @PostMapping("/api/member/{id}/reserve")
    public ReserveRegister.Response reserveRegister(@PathVariable(value = "id") Long memberId, @RequestBody ReserveRegister.Request request) {

        return ReserveRegister.Response.from(reserveService.reserveRegister(
            memberId, request.getCarId(),
            request.getParkingLotId(), request.getTicketId(),
            request.getEstimatedHour(), request.getEstimatedMinute()));

    }

    // 예약 취소
    @PatchMapping("/api/member/{id}/reserve")
    public ReserveCancel.Response reserveCancel(@PathVariable(value = "id") Long memberId, @RequestBody ReserveCancel.Request request) {
        return ReserveCancel.Response.from(reserveService.reserveCancel(memberId, request.getReserveId()));
    }


}
