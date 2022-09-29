package com.zerobase.parkinglot.reserve.service;

import com.zerobase.parkinglot.reserve.model.ReserveDto;
import com.zerobase.parkinglot.reserve.model.ReserveRegister;

public interface ReserveService {

     // 예약등록
     ReserveDto reserveRegister(Long memberId, Long carId, Long parkingLotId,
         Long ticketId, Integer estimatedHour, Integer estimatedMinute);

     ReserveDto reserveCancel(Long memberId, Long reserveId);
}
