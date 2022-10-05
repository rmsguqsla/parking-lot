package com.zerobase.parkinglot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import com.zerobase.parkinglot.reserve.model.ReserveRegister;
import com.zerobase.parkinglot.reserve.service.LockAopAspect;
import com.zerobase.parkinglot.reserve.service.LockService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LockAopAspectTest {

    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @Test
    void lockAndUnlock() throws Throwable {
        //given
        ArgumentCaptor<Long> lockArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> unLockArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        ReserveRegister.Request request =
            new ReserveRegister.Request(1L, 1L, 1L, 18, 0);
        //when
        lockAopAspect.aroundMethod(proceedingJoinPoint, 1L,request);
        //then
        verify(lockService, times(1))
            .lock(lockArgumentCaptor.capture());
        verify(lockService, times(1))
            .unlock(unLockArgumentCaptor.capture());
        assertEquals(1L, lockArgumentCaptor.getValue());
        assertEquals(1L, unLockArgumentCaptor.getValue());
    }

    @Test
    void lockAndUnlock_evenIfThrow() throws Throwable {
        //given
        ArgumentCaptor<Long> lockArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> unLockArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        ReserveRegister.Request request =
            new ReserveRegister.Request(1L, 2L, 1L, 18, 0);

        given(proceedingJoinPoint.proceed())
            .willThrow(new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));
        //when
        assertThrows(ParkingLotException.class, () ->
            lockAopAspect.aroundMethod(proceedingJoinPoint, 1L, request));

        //then
        verify(lockService, times(1))
            .lock(lockArgumentCaptor.capture());
        verify(lockService, times(1))
            .unlock(unLockArgumentCaptor.capture());
        assertEquals(2L, lockArgumentCaptor.getValue());
        assertEquals(2L, unLockArgumentCaptor.getValue());
    }

}
