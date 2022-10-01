package com.zerobase.parkinglot.reserve.service;

import com.zerobase.parkinglot.reserve.model.ReserveDto;
import com.zerobase.parkinglot.reserve.model.ReserveInfo;
import java.util.List;

public interface ReserveService {

     // 예약등록
     ReserveDto reserveRegister(Long memberId, Long carId, Long parkingLotId,
         Long ticketId, Integer estimatedHour, Integer estimatedMinute);

     ReserveDto reserveCancel(Long memberId, Long reserveId);

    List<ReserveInfo> getReserves(Long id);

    List<ReserveInfo> getAdminReserves();

    ReserveInfo getReserve(Long memberId, Long reserveId);

    ReserveInfo getAdminReserve(Long id);
}
