package com.zerobase.parkinglot.reserve.exception;

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
public class ReserveException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;

    public ReserveException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

}
