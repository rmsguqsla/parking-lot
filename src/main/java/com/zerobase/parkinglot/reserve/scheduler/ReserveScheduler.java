package com.zerobase.parkinglot.reserve.scheduler;

import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import com.zerobase.parkinglot.parkinglot.repository.ParkingLotRepository;
import com.zerobase.parkinglot.reserve.entity.Reserve;
import com.zerobase.parkinglot.reserve.repository.ReserveRepository;
import com.zerobase.parkinglot.reserve.type.StatusType;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReserveScheduler {

    private final ReserveRepository reserveRepository;
    private final ParkingLotRepository parkingLotRepository;

    // 예약 종료 시간이 지나면 Using상태를 Complete으로 바꿈
    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void reserveCompleteScheduling() {

        List<Reserve> reserveList = reserveRepository.findByStatus(StatusType.Using);

        for (Reserve reserve : reserveList) {
            if (reserve.getReserveEndDt().isBefore(LocalDateTime.now())) {
                ParkingLot parkingLot = parkingLotRepository.findByNameAndAddress(reserve.getParkingLot(), reserve.getAddress())
                    .orElseThrow(() -> new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));

                parkingLot.minusReserveCount();
                reserve.setStatus(StatusType.Complete);
            }
        }
    }

}
