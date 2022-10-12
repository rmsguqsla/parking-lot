package com.zerobase.parkinglot.handler;

import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import com.zerobase.parkinglot.error.ErrorResponse;
import com.zerobase.parkinglot.member.exception.MemberException;
import com.zerobase.parkinglot.reserve.exception.ReserveException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public ErrorResponse handleMemberException(MemberException e) {
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(ParkingLotException.class)
    public ErrorResponse handleMemberException(ParkingLotException e) {
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(ReserveException.class)
    public ErrorResponse handleMemberException(ReserveException e) {
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }
}
