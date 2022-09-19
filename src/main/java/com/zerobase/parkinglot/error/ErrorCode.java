package com.zerobase.parkinglot.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MEMBER_ALREADY_EXIST("이미 가입된 회원입니다."),
    CAR_ALREADY_EXIST("이미 등록된 차번호입니다."),
    MEMBER_NOT_FOUND("회원이 존재하지 않습니다."),
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다."),
    MEMBER_CAR_NUMBER_NOT_MATCH("회원님에게 등록되지 않은 차번호입니다."),
    PARKING_LOT_NOT_FOUND("주차장이 존재하지 않습니다.");

    private final String description;
}
