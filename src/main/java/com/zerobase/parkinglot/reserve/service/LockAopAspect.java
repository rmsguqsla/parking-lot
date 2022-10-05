package com.zerobase.parkinglot.reserve.service;

import com.zerobase.parkinglot.aop.ParkingLotIdInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {
    private final LockService lockService;

    @Around("@annotation(com.zerobase.parkinglot.aop.ReserveLock) && args(memberId, request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp,
            Long memberId,
            ParkingLotIdInterface request
    ) throws Throwable {
        // lock 취득 시도
        log.info("lock 취득 시도");
        lockService.lock(request.getParkingLotId());
        try {
            return pjp.proceed();
        } finally {
            // lock 해제
            log.info("lock 해제");
            lockService.unlock(request.getParkingLotId());
        }
    }
}
