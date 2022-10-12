package com.zerobase.parkinglot.reserve.controller;

import com.zerobase.parkinglot.aop.ReserveLock;
import com.zerobase.parkinglot.reserve.model.ReserveCancel;
import com.zerobase.parkinglot.reserve.model.ReserveInfo;
import com.zerobase.parkinglot.reserve.model.ReserveRegister;
import com.zerobase.parkinglot.reserve.service.ReserveService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @ApiOperation(value = "예약 API")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/api/member/{id}/reserve")
    @ReserveLock
    public ReserveRegister.Response reserveRegister(@PathVariable(value = "id") Long memberId, @Valid @RequestBody ReserveRegister.Request request) {

        return ReserveRegister.Response.from(reserveService.reserveRegister(
            memberId, request.getCarId(),
            request.getParkingLotId(), request.getTicketId(),
            request.getEstimatedHour(), request.getEstimatedMinute()));

    }

    // 예약 취소
    @ApiOperation(value = "예약 취소 API")
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/api/member/{id}/reserve")
    @ReserveLock
    public ReserveCancel.Response reserveCancel(@PathVariable(value = "id") Long memberId, @Valid @RequestBody ReserveCancel.Request request) {
        return ReserveCancel.Response.from(reserveService.reserveCancel(memberId, request.getReserveId()));
    }

    // 사용자 예약 목록
    @ApiOperation(value = "나의 예약 목록 API")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/member/{id}/reserves")
    public List<ReserveInfo> getReserves(@PathVariable Long id) {
        return reserveService.getReserves(id);
    }

    // 사용자 예약 상세
    @ApiOperation(value = "나의 예약 상세 API")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/member/{memberId}/reserve/{reserveId}")
    public ReserveInfo getReserve(@PathVariable(value = "memberId") Long memberId, @PathVariable(value = "reserveId") Long reserveId) {
        return reserveService.getReserve(memberId, reserveId);
    }

}
