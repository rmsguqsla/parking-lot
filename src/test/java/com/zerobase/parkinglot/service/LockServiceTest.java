package com.zerobase.parkinglot.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.reserve.exception.ReserveException;
import com.zerobase.parkinglot.reserve.service.LockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
public class LockServiceTest {
    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @InjectMocks
    private LockService lockService;

    @Test
    void getLock_success() throws InterruptedException {
        //given
        given(redissonClient.getLock(anyString()))
            .willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any()))
            .willReturn(true);
        //when
        //then
        assertDoesNotThrow(() -> lockService.lock(1L));
    }

    @Test
    void getLock_fail() throws InterruptedException {
        //given
        given(redissonClient.getLock(anyString()))
            .willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any()))
            .willReturn(false);
        //when
        ReserveException exception = assertThrows(ReserveException.class, () -> lockService.lock(1L));
        //then
        assertEquals(ErrorCode.RESERVE_TRANSACTION_LOCK, exception.getErrorCode());
    }

}
