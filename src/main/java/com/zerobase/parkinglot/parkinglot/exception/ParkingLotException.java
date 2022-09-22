package com.zerobase.parkinglot.parkinglot.exception;

import com.zerobase.parkinglot.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParkingLotException extends RuntimeException {

    private ErrorCode errorCode;

    private String errorMessage;

    public ParkingLotException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

}
