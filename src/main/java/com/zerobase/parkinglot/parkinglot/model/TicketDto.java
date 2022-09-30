package com.zerobase.parkinglot.parkinglot.model;

import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import com.zerobase.parkinglot.parkinglot.entity.Ticket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
public class TicketDto {

    private Long id;

    private ParkingLot parkingLot;

    private String name;

    private int fee;

    private LocalTime startUsableTime;

    private LocalTime endUsableTime;

    private LocalTime maxUsableTime;

    private boolean holidayYn;

    private boolean useYn;

    private LocalDateTime regDt;

    private LocalDateTime updateDt;

    public static TicketDto fromEntity(Ticket ticket) {

        return TicketDto.builder()
            .id(ticket.getId())
            .parkingLot(ticket.getParkingLot())
            .name(ticket.getName())
            .fee(ticket.getFee())
            .startUsableTime(ticket.getStartUsableTime())
            .endUsableTime(ticket.getEndUsableTime())
            .maxUsableTime(ticket.getMaxUsableTime())
            .holidayYn(ticket.isHolidayYn())
            .useYn(ticket.isUseYn())
            .regDt(ticket.getRegDt())
            .updateDt(ticket.getUpdateDt())
            .build();

    }

    public static List<TicketDto> fromEntityList(List<Ticket> ticketList) {

        List<TicketDto> ticketDtoList = new ArrayList<>();

        for (Ticket ticket : ticketList) {

            ticketDtoList.add(TicketDto.fromEntity(ticket));

        }

        return ticketDtoList;

    }
}
