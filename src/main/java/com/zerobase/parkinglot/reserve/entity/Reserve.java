package com.zerobase.parkinglot.reserve.entity;

import com.zerobase.parkinglot.reserve.type.StatusType;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Reserve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String phone;

    private String carNumber;

    private String parkingLot;

    private String address;

    private String ticket;

    private Integer fee;

    private LocalDateTime minEstimatedDt;

    private LocalDateTime maxEstimatedDt;

    private LocalDateTime reserveDt;

    private LocalDateTime reserveEndDt;

    @Enumerated(value = EnumType.STRING)
    private StatusType status;

    private LocalDateTime cancelDt;
}
