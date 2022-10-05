package com.zerobase.parkinglot.reserve.service;

import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.reserve.exception.ReserveException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(Long parkingLotId) {
        RLock lock = redissonClient.getLock(getLockKey(parkingLotId));
        log.debug("Trying lock for parkingLotId : {}", parkingLotId);

        try {
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            if (!isLock) {
                log.error("================Lock acquisition failed=============");
                throw new ReserveException(ErrorCode.RESERVE_TRANSACTION_LOCK);
            }
        } catch (ReserveException e) {
            throw e;
        } catch (Exception e) {
            log.error("Redis lock failed", e);
        }
    }

    public void unlock(Long parkingLotId) {
        log.debug("Unlock for accountNumber : {}", parkingLotId);
        redissonClient.getLock(getLockKey(parkingLotId)).unlock();
    }

    private String getLockKey(Long parkingLotId)   {
        return "RSLK:" + parkingLotId;
    }
}
