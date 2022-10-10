package com.zerobase.parkinglot.reserve.controller;

import com.zerobase.parkinglot.reserve.model.ReserveInfo;
import com.zerobase.parkinglot.reserve.service.ReserveService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiAdminReserveController {

    private final ReserveService reserveService;

    // 사용자 예약 목록
    @ApiOperation(value = "사용자들의 예약 내역 목록 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/reserves")
    public List<ReserveInfo> getAdminReserves() {
        return reserveService.getAdminReserves();
    }

    // 사용자 예약 상세
    @ApiOperation(value = "사용자들의 예약 내역 상세 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/reserve/{id}")
    public ReserveInfo getAdminReserve(@PathVariable Long id) {
        return reserveService.getAdminReserve(id);
    }
}
