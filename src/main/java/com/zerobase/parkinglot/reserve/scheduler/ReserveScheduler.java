package com.zerobase.parkinglot.reserve.scheduler;

import com.zerobase.parkinglot.reserve.entity.Reserve;
import com.zerobase.parkinglot.reserve.repository.ReserveRepository;
import com.zerobase.parkinglot.reserve.type.StatusType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReserveScheduler {

    private final ReserveRepository reserveRepository;

    // 예약 종료 시간이 지나면 Using상태를 Complete으로 바꿈
    @Scheduled(fixedDelay = 1000)
    public void reserveCompleteScheduling() {

        List<Reserve> reserveList = reserveRepository.findByStatus(StatusType.Using);

        for (Reserve reserve : reserveList) {
            if (reserve.getReserveEndDt().isBefore(LocalDateTime.now())) {
                reserve.setStatus(StatusType.Complete);
                reserveRepository.save(reserve);
            }
        }

    }

}
