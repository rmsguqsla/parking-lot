package com.zerobase.parkinglot.parkinglot.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne
    private ParkingLot parkingLot;

    private String name;

    private int fee;

    private LocalTime startUsableTime;

    private LocalTime endUsableTime;

    private boolean holidayYn;

    private boolean useYn;

    private LocalDateTime regDt;

    private LocalDateTime updateDt;
}
