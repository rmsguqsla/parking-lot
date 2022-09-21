package com.zerobase.parkinglot.parkinglot.service;

import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import com.zerobase.parkinglot.parkinglot.entity.Ticket;
import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUserInfo;
import com.zerobase.parkinglot.parkinglot.model.TicketDto;
import com.zerobase.parkinglot.parkinglot.model.TicketUserInfo;
import com.zerobase.parkinglot.parkinglot.repository.ParkingLotCustomRepository;
import com.zerobase.parkinglot.parkinglot.repository.ParkingLotRepository;
import com.zerobase.parkinglot.parkinglot.repository.TicketRepository;
import com.zerobase.parkinglot.parkinglot.type.SearchType;
import com.zerobase.parkinglot.utils.GeoCodingUtil;
import com.zerobase.parkinglot.utils.HolidayUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl implements ParkingLotService{

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLotCustomRepository parkingLotCustomRepository;
    private final TicketRepository ticketRepository;
    private final GeoCodingUtil geoCodingUtil;
    @Override
    public ParkingLotDto parkingLotRegister(String name, String address, int spaceCount) {

        // 주소를 위도, 경도로 변환
        double[] coordinate = geoCodingUtil.getGeoCode(address);
        double lat = coordinate[0];
        double lng = coordinate[1];

        return ParkingLotDto.fromEntity(
            parkingLotRepository.save(
                ParkingLot.builder()
                    .name(name)
                    .address(address)
                    .lat(lat)
                    .lng(lng)
                    .spaceCount(spaceCount)
                    .regDt(LocalDateTime.now())
                    .useYn(true)
                    .build()
            )
        );

    }

    @Override
    public List<ParkingLotDto> getParkingLots() {

        return ParkingLotDto.fromEntityList(
            parkingLotRepository.findAll()
        );

    }

    @Override
    public ParkingLotDto parkingLotUpdate(Long id, String name,
        String address, int spaceCount, boolean useYn) {

        ParkingLot parkingLot = findParkingLotById(id);

        double[] coordinate = geoCodingUtil.getGeoCode(address);
        double lat = coordinate[0];
        double lng = coordinate[1];

        parkingLot.setName(name);
        parkingLot.setAddress(address);
        parkingLot.setLat(lat);
        parkingLot.setLng(lng);
        parkingLot.setSpaceCount(spaceCount);
        parkingLot.setUseYn(useYn);
        parkingLot.setUpdateDt(LocalDateTime.now());

        return ParkingLotDto.fromEntity(
            parkingLotRepository.save(parkingLot)
        );

    }

    @Override
    public ParkingLotDto getParkingLot(Long id) {
        return ParkingLotDto.fromEntity(findParkingLotByIdAndUseYn(id));
    }

    @Override
    public List<ParkingLotUserInfo> getParkingLotsMyAround(double myLat, double myLng) {
        return parkingLotCustomRepository.findAllByDistanceLimit20(myLat, myLng);
    }

    @Override
    public List<ParkingLotUserInfo> getParkingLotsSearch(
        double myLat, double myLng,
        String searchType, String searchValue) {

        InvalidSearchType(searchType);

        return parkingLotCustomRepository.findAllBySearch(myLat, myLng, searchType, searchValue);
    }

    @Override
    public TicketDto ticketRegister(Long id, String name, int fee,
        LocalTime startUsableTime, LocalTime endUsableTime, boolean holidayYn) {

        ParkingLot parkingLot = findParkingLotById(id);

        return TicketDto.fromEntity(
            ticketRepository.save(
                Ticket.builder()
                    .parkingLot(parkingLot)
                    .name(name)
                    .fee(fee)
                    .startUsableTime(startUsableTime)
                    .endUsableTime(endUsableTime)
                    .holidayYn(holidayYn)
                    .useYn(true)
                    .regDt(LocalDateTime.now())
                    .build()
            )
        );

    }

    @Override
    public TicketDto ticketUpdate(Long parkingLotId, Long ticketId, String name, int fee,
        LocalTime startUsableTime, LocalTime endUsableTime, boolean holidayYn, boolean useYn) {

        ParkingLot parkingLot = findParkingLotById(parkingLotId);

        Ticket ticket = findTicketByIdAndParkingLot(ticketId, parkingLot);

        ticket.setName(name);
        ticket.setFee(fee);
        ticket.setStartUsableTime(startUsableTime);
        ticket.setEndUsableTime(endUsableTime);
        ticket.setHolidayYn(holidayYn);
        ticket.setUseYn(useYn);
        ticket.setUpdateDt(LocalDateTime.now());

        return TicketDto.fromEntity(ticketRepository.save(ticket));
    }

    @Override
    public List<TicketDto> getTickets(Long id) {

        ParkingLot parkingLot = findParkingLotById(id);

        return TicketDto.fromEntityList(findTicketByParkingLot(parkingLot));

    }

    @Override
    public TicketDto getTicket(Long parkingLotId, Long ticketId) {

        ParkingLot parkingLot = findParkingLotById(parkingLotId);

        return TicketDto.fromEntity(findTicketByIdAndParkingLot(ticketId, parkingLot));

    }

    @Override
    public List<TicketUserInfo> getUsableTickets(Long id) {

        return TicketUserInfo.listFrom(getUsableTicketDtoList(id));

    }

    private List<TicketDto> getUsableTicketDtoList(Long id) {

        ParkingLot parkingLot = findParkingLotById(id);

        boolean isHoliday = false;

        // 현재 날짜가 평일인지 휴일인지
        // useYn이 true인지
        if (isHolidayNow()) {
            isHoliday = true;
        }

        List<Ticket> ticketList = ticketRepository.findByParkingLotAndHolidayYnAndUseYn(parkingLot, isHoliday, true);

        // 현재 시간이 티켓의 이용가능시간 사이에 있는지
        for (int i = ticketList.size() - 1; i >= 0; i--) {
            Ticket ticket = ticketList.get(i);
            if (!isUsableNow(ticket.getStartUsableTime(), ticket.getEndUsableTime())) {
                ticketList.remove(ticket);
            }
        }

        return TicketDto.fromEntityList(ticketList);
    }

    private boolean isHolidayNow() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = LocalDateTime.now().format(df).toString();
        return HolidayUtil.isHoliday(now);
    }

    private boolean isUsableNow(LocalTime startUsableTime, LocalTime endUsableTime) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("HHmmss");

        int now = Integer.parseInt(LocalDateTime.now().format(df));
        int start = Integer.parseInt(startUsableTime.format(df));
        int end = Integer.parseInt(endUsableTime.format(df));

        if (start < now && now < end) {
            return true;
        } else {
            return false;
        }

    }

    private List<Ticket> findTicketByParkingLot(ParkingLot parkingLot) {
        return ticketRepository.findByParkingLot(parkingLot);
    }

    private Ticket findTicketByIdAndParkingLot(Long ticketId, ParkingLot parkingLot) {

        return ticketRepository.findByIdAndParkingLot(ticketId, parkingLot)
            .orElseThrow(() -> new ParkingLotException(ErrorCode.PARKING_LOT_TICKET_NOT_MATCH));

    }

    private void InvalidSearchType(String searchType) {

        boolean exist = false;

        for (SearchType type : SearchType.values()) {
            if (type.getDescription().equals(searchType)) {
                exist = true;
                break;
            }
        }

        if (!exist) {
            throw new ParkingLotException(ErrorCode.SEARCH_TYPE_NOT_EXIST);
        }
    }

    private ParkingLot findParkingLotById(Long id) {
        return parkingLotRepository.findById(id)
            .orElseThrow(() -> new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));
    }

    private ParkingLot findParkingLotByIdAndUseYn(Long id) {
        return parkingLotRepository.findByIdAndUseYn(id, true)
            .orElseThrow(() -> new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));
    }
}
