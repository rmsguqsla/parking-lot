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
    MEMBER_CAR_NUMBER_NOT_MATCH("회원님에게 등록되지 않은 차입니다."),
    PARKING_LOT_NOT_FOUND("주차장이 존재하지 않습니다."),
    SEARCH_TYPE_NOT_EXIST("존재하지 않는 검색타입입니다."),
    PARKING_LOT_TICKET_NOT_MATCH("주차장에 존재하지 않는 이용권입니다."),
    INVALID_ADDRESS("올바르지 않은 주소입니다."),
    INVALID_RANGE_HOUR_MINUTE("시간은 0~23시, 분은 0~59분 입니다."),
    RESERVE_NOT_FOUND("주차장 예약 내역이 존재하지 않습니다."),
    STATUS_CANCEL("이미 취소되었습니다."),
    STATUS_COMPLETE("이미 사용하셨습니다."),
    NOT_CANCEL_RESERVE("예약 취소할 수 없습니다.");

    private final String description;
}
