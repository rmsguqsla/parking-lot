package com.zerobase.parkinglot.parkinglot.service;

import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUserInfo;
import com.zerobase.parkinglot.parkinglot.model.TicketDto;
import com.zerobase.parkinglot.parkinglot.model.TicketInfo;
import com.zerobase.parkinglot.parkinglot.model.TicketUserInfo;
import java.time.LocalTime;
import java.util.List;

public interface ParkingLotService {

    ParkingLotDto parkingLotRegister(String name, String address, int spaceCount);

    List<ParkingLotDto> getParkingLots();

    ParkingLotDto parkingLotUpdate(Long id, String name, String address, int spaceCount, boolean useYn);

    ParkingLotDto getParkingLot(Long id);

    List<ParkingLotUserInfo> getParkingLotsMyAround(double myLat, double myLng);

    List<ParkingLotUserInfo> getParkingLotsSearch(double myLat, double myLng, String searchType, String searchValue);

    TicketDto ticketRegister(Long id, String name, int fee, LocalTime startUsableTime, LocalTime endUsableTime, boolean holidayYn);

    TicketDto ticketUpdate(Long parkingLotId, Long ticketId, String name, int fee,
        LocalTime startUsableTime, LocalTime endUsableTime, boolean holidayYn, boolean useYn);

    List<TicketDto> getTickets(Long id);

    TicketDto getTicket(Long parkingLotId, Long ticketId);

    List<TicketUserInfo> getUsableTickets(Long id);
}
