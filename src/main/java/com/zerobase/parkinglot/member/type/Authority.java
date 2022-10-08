package com.zerobase.parkinglot.member.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Authority {
    ROLE_USER,
    ROLE_ADMIN;
}
