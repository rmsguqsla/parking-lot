package com.zerobase.parkinglot.parkinglot.entity;

import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.reserve.exception.ReserveException;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private double lat; // 위도

    private double lng; // 경도

    private int spaceCount; // 주차장 자리 수

    private int reserveCount; // 주차장 예약 수

    private LocalDateTime regDt;

    private LocalDateTime updateDt;

    private boolean useYn;

    public void plusReserveCount() {
        if (reserveCount >= spaceCount) {
            throw new ReserveException(ErrorCode.RESERVE_FULL);
        }
        reserveCount += 1;
    }

    public void minusReserveCount(Long amount) {
        if (amount <= 0) {
            throw new ReserveException(ErrorCode.NOT_CANCEL_RESERVE);
        }
        reserveCount -= 1;
    }
}
