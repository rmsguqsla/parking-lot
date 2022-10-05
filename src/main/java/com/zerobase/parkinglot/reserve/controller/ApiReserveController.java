package com.zerobase.parkinglot.reserve.controller;

import com.zerobase.parkinglot.reserve.model.ReserveCancel;
import com.zerobase.parkinglot.reserve.model.ReserveInfo;
import com.zerobase.parkinglot.reserve.model.ReserveRegister;
import com.zerobase.parkinglot.reserve.service.ReserveService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    // 사용자 예약 목록
    @GetMapping("/api/member/{id}/reserves")
    public List<ReserveInfo> getReserves(@PathVariable Long id) {
        return reserveService.getReserves(id);
    }

    // 사용자 예약 상세
    @GetMapping("/api/member/{memberId}/reserve/{reserveId}")
    public ReserveInfo getReserve(@PathVariable(value = "memberId") Long memberId, @PathVariable(value = "reserveId") Long reserveId) {
        return reserveService.getReserve(memberId, reserveId);
    }

}
