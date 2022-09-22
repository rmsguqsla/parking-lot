package com.zerobase.parkinglot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.zerobase.parkinglot.parkinglot.service.ParkingLotServiceImpl;
import com.zerobase.parkinglot.utils.GeoCodingUtil;
import com.zerobase.parkinglot.utils.HolidayUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParkingLotServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private ParkingLotCustomRepository parkingLotCustomRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private GeoCodingUtil geoCodingUtil;

    @InjectMocks
    private ParkingLotServiceImpl parkingLotServiceImpl;

    @Test
    void parkingLotRegisterTest_success() {

        // given
        double[] coordinate = {35, 120};

        given(geoCodingUtil.getGeoCode("서울 어딘가"))
            .willReturn(coordinate);

        given(parkingLotRepository.save(any()))
            .willReturn(
                ParkingLot.builder()
                    .name("서울주차장")
                    .address("서울 어딘가")
                    .lat(coordinate[0])
                    .lng(coordinate[1])
                    .spaceCount(100)
                    .useYn(true)
                    .build()
            );

        // when
        ParkingLotDto parkingLotDto = parkingLotServiceImpl.parkingLotRegister("서울주차장", "서울 어딘가", 100);

        ArgumentCaptor<ParkingLot> captor = ArgumentCaptor.forClass(ParkingLot.class);

        // then
        verify(parkingLotRepository, times(1)).save(captor.capture());
        assertEquals("서울주차장", parkingLotDto.getName());
        assertEquals("서울 어딘가", parkingLotDto.getAddress());
        assertEquals(35, parkingLotDto.getLat());
        assertEquals(120, parkingLotDto.getLng());
        assertEquals(100, parkingLotDto.getSpaceCount());
        assertEquals(true, parkingLotDto.isUseYn());

    }

    @Test
    void parkingLotRegisterTest_fail_InvalidAddress() {

        // given
        given(geoCodingUtil.getGeoCode("서울 어딘가"))
            .willThrow(new ParkingLotException(ErrorCode.INVALID_ADDRESS));

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.parkingLotRegister("서울주차장", "서울 어딘가", 100));

        // then
        assertEquals(ErrorCode.INVALID_ADDRESS, exception.getErrorCode());

    }

    @Test
    void getParkingLotsTest_success() {

        // given
        List<ParkingLot> parkingLotList = Arrays.asList(
            ParkingLot.builder()
                .name("서울주차장")
                .address("서울특별시 어딘가")
                .lat(36)
                .lng(120)
                .spaceCount(100)
                .useYn(true)
                .build(),
            ParkingLot.builder()
                .name("인천주차장")
                .address("인천광역시 어딘가")
                .lat(35)
                .lng(115)
                .spaceCount(50)
                .useYn(true)
                .build(),
            ParkingLot.builder()
                .name("경기주차장")
                .address("경기도 어딘가")
                .lat(34)
                .lng(114)
                .spaceCount(30)
                .useYn(true)
                .build()
        );

        given(parkingLotRepository.findAll())
            .willReturn(parkingLotList);

        // when
        List<ParkingLotDto> parkingLotDtoList = parkingLotServiceImpl.getParkingLots();

        // then
        assertEquals("서울주차장", parkingLotDtoList.get(0).getName());
        assertEquals("서울특별시 어딘가", parkingLotDtoList.get(0).getAddress());
        assertEquals(36, parkingLotDtoList.get(0).getLat());
        assertEquals(120, parkingLotDtoList.get(0).getLng());
        assertEquals(100, parkingLotDtoList.get(0).getSpaceCount());
        assertTrue(parkingLotDtoList.get(0).isUseYn());
        assertEquals("인천주차장", parkingLotDtoList.get(1).getName());
        assertEquals("인천광역시 어딘가", parkingLotDtoList.get(1).getAddress());
        assertEquals(35, parkingLotDtoList.get(1).getLat());
        assertEquals(115, parkingLotDtoList.get(1).getLng());
        assertEquals(50, parkingLotDtoList.get(1).getSpaceCount());
        assertTrue(parkingLotDtoList.get(1).isUseYn());
        assertEquals("경기주차장", parkingLotDtoList.get(2).getName());
        assertEquals("경기도 어딘가", parkingLotDtoList.get(2).getAddress());
        assertEquals(34, parkingLotDtoList.get(2).getLat());
        assertEquals(114, parkingLotDtoList.get(2).getLng());
        assertEquals(30, parkingLotDtoList.get(2).getSpaceCount());
        assertTrue(parkingLotDtoList.get(2).isUseYn());

    }

    @Test
    void parkingLotUpdateTest_success() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .build();

        given(parkingLotRepository.findById(any()))
            .willReturn(Optional.of(parkingLot));

        double[] coordinate = {35, 120};

        given(geoCodingUtil.getGeoCode("서울 어딘가"))
            .willReturn(coordinate);

        given(parkingLotRepository.save(any()))
            .willReturn(
                ParkingLot.builder()
                    .id(parkingLot.getId())
                    .name("서울주차장")
                    .address("서울 어딘가")
                    .lat(coordinate[0])
                    .lng(coordinate[1])
                    .spaceCount(100)
                    .useYn(true)
                    .build()
            );

        // when
        ParkingLotDto parkingLotDto = parkingLotServiceImpl.parkingLotUpdate(1L, "서울주차장", "서울 어딘가", 100, true);

        ArgumentCaptor<ParkingLot> captor = ArgumentCaptor.forClass(ParkingLot.class);

        // then
        verify(parkingLotRepository, times(1)).save(captor.capture());
        assertEquals(1L, parkingLotDto.getId());
        assertEquals("서울주차장", parkingLotDto.getName());
        assertEquals("서울 어딘가", parkingLotDto.getAddress());
        assertEquals(35, parkingLotDto.getLat());
        assertEquals(120, parkingLotDto.getLng());
        assertEquals(100, parkingLotDto.getSpaceCount());
        assertTrue(parkingLotDto.isUseYn());

    }

    @Test
    void parkingLotUpdateTest_fail_ParkingLotNotFound() {

        // given
        given(parkingLotRepository.findById(any()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.parkingLotUpdate(1L, "서울주차장", "서울 어딘가", 100, true));

        // then
        assertEquals(ErrorCode.PARKING_LOT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void parkingLotUpdateTest_fail_InvalidAddress() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .build();

        given(parkingLotRepository.findById(any()))
            .willReturn(Optional.of(parkingLot));

        given(geoCodingUtil.getGeoCode("서울 어딘가"))
            .willThrow(new ParkingLotException(ErrorCode.INVALID_ADDRESS));

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.parkingLotUpdate(1L, "서울주차장", "서울 어딘가", 100, true));

        // then
        assertEquals(ErrorCode.INVALID_ADDRESS, exception.getErrorCode());

    }

    @Test
    void getParkingLotTest_success() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
                                .id(1L)
                                .name("서울주차장")
                                .address("서울 어딘가")
                                .lat(35)
                                .lng(120)
                                .spaceCount(100)
                                .useYn(true)
                                .build();

        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.of(parkingLot));

        //when
        ParkingLotDto parkingLotDto = parkingLotServiceImpl.getParkingLot(1L);

        //then
        assertEquals(1L, parkingLotDto.getId());
        assertEquals("서울주차장", parkingLotDto.getName());
        assertEquals("서울 어딘가", parkingLotDto.getAddress());
        assertEquals(35, parkingLotDto.getLat());
        assertEquals(120, parkingLotDto.getLng());
        assertEquals(100, parkingLotDto.getSpaceCount());
        assertTrue(parkingLotDto.isUseYn());

    }

    @Test
    void getParkingLotTest_fail_ParkingLotNotFound() {

        // given
        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.getParkingLot(1L));

        // then
        assertEquals(ErrorCode.PARKING_LOT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void getParkingLotsMyAroundTest_success() {

        // given
        List<ParkingLotUserInfo> parkingLotUserInfoList = Arrays.asList(
            ParkingLotUserInfo.builder()
                .id(1L)
                .name("서울주차장1")
                .address("서울특별시 어딘가")
                .spaceCount(100)
                .distance(0.1)
                .build(),
            ParkingLotUserInfo.builder()
                .id(2L)
                .name("서울주차장2")
                .address("서울특별시 어딘가")
                .spaceCount(60)
                .distance(0.2)
                .build(),
            ParkingLotUserInfo.builder()
                .id(3L)
                .name("서울주차장3")
                .address("서울특별시 어딘가")
                .spaceCount(40)
                .distance(0.3)
                .build()
        );

        given(parkingLotCustomRepository.findAllByDistanceLimit20(anyDouble(), anyDouble()))
            .willReturn(parkingLotUserInfoList);

        // when
        List<ParkingLotUserInfo> parkingLotUserInfoList1 = parkingLotServiceImpl.getParkingLotsMyAround(35, 120);

        // then
        assertEquals(1L, parkingLotUserInfoList1.get(0).getId());
        assertEquals("서울주차장1", parkingLotUserInfoList1.get(0).getName());
        assertEquals("서울특별시 어딘가", parkingLotUserInfoList1.get(0).getAddress());
        assertEquals(100, parkingLotUserInfoList1.get(0).getSpaceCount());
        assertEquals(0.1, parkingLotUserInfoList1.get(0).getDistance());
        assertEquals(2L, parkingLotUserInfoList1.get(1).getId());
        assertEquals("서울주차장2", parkingLotUserInfoList1.get(1).getName());
        assertEquals("서울특별시 어딘가", parkingLotUserInfoList1.get(1).getAddress());
        assertEquals(60, parkingLotUserInfoList1.get(1).getSpaceCount());
        assertEquals(0.2, parkingLotUserInfoList1.get(1).getDistance());
        assertEquals(3L, parkingLotUserInfoList1.get(2).getId());
        assertEquals("서울주차장3", parkingLotUserInfoList1.get(2).getName());
        assertEquals("서울특별시 어딘가", parkingLotUserInfoList1.get(2).getAddress());
        assertEquals(40, parkingLotUserInfoList1.get(2).getSpaceCount());
        assertEquals(0.3, parkingLotUserInfoList1.get(2).getDistance());
    }

    @Test
    void getParkingLotsSearch_success() {

        // given
        List<ParkingLotUserInfo> parkingLotUserInfoList = Arrays.asList(
            ParkingLotUserInfo.builder()
                .id(1L)
                .name("서울주차장1")
                .address("서울특별시 어딘가")
                .spaceCount(100)
                .distance(0.1)
                .build(),
            ParkingLotUserInfo.builder()
                .id(2L)
                .name("서울주차장2")
                .address("서울특별시 어딘가")
                .spaceCount(60)
                .distance(0.2)
                .build(),
            ParkingLotUserInfo.builder()
                .id(3L)
                .name("서울주차장3")
                .address("서울특별시 어딘가")
                .spaceCount(40)
                .distance(0.3)
                .build()
        );

        given(parkingLotCustomRepository.findAllBySearch(35, 120, "name", "서울"))
            .willReturn(parkingLotUserInfoList);

        // when
        List<ParkingLotUserInfo> parkingLotUserInfoList1 = parkingLotServiceImpl.getParkingLotsSearch(35, 120, "name", "서울");


        // then
        assertEquals(1L, parkingLotUserInfoList1.get(0).getId());
        assertEquals("서울주차장1", parkingLotUserInfoList1.get(0).getName());
        assertEquals("서울특별시 어딘가", parkingLotUserInfoList1.get(0).getAddress());
        assertEquals(100, parkingLotUserInfoList1.get(0).getSpaceCount());
        assertEquals(0.1, parkingLotUserInfoList1.get(0).getDistance());
        assertEquals(2L, parkingLotUserInfoList1.get(1).getId());
        assertEquals("서울주차장2", parkingLotUserInfoList1.get(1).getName());
        assertEquals("서울특별시 어딘가", parkingLotUserInfoList1.get(1).getAddress());
        assertEquals(60, parkingLotUserInfoList1.get(1).getSpaceCount());
        assertEquals(0.2, parkingLotUserInfoList1.get(1).getDistance());
        assertEquals(3L, parkingLotUserInfoList1.get(2).getId());
        assertEquals("서울주차장3", parkingLotUserInfoList1.get(2).getName());
        assertEquals("서울특별시 어딘가", parkingLotUserInfoList1.get(2).getAddress());
        assertEquals(40, parkingLotUserInfoList1.get(2).getSpaceCount());
        assertEquals(0.3, parkingLotUserInfoList1.get(2).getDistance());

    }

    @Test
    void ticketRegisterTest_success() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울 어딘가")
            .lat(35)
            .lng(120)
            .spaceCount(100)
            .useYn(true)
            .build();

        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.of(parkingLot));

        given(ticketRepository.save(any()))
            .willReturn(
                Ticket.builder()
                    .id(1L)
                    .parkingLot(parkingLot)
                    .name("평일 이용권")
                    .fee(10000)
                    .startUsableTime(LocalTime.of(0,0,0))
                    .endUsableTime(LocalTime.of(23,59,59))
                    .holidayYn(false)
                    .useYn(true)
                    .build()
            );

        // when
        TicketDto ticketDto = parkingLotServiceImpl.ticketRegister(1L, "평일 이용권", 10000,
            LocalTime.of(0,0,0),
            LocalTime.of(23,59,59), false);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);

        // then
        verify(ticketRepository, times(1)).save(captor.capture());
        assertEquals(1L, ticketDto.getId());
        assertEquals(1L, ticketDto.getParkingLot().getId());
        assertEquals("평일 이용권", ticketDto.getName());
        assertEquals(10000, ticketDto.getFee());
        assertEquals(LocalTime.of(0,0,0), ticketDto.getStartUsableTime());
        assertEquals(LocalTime.of(23,59,59), ticketDto.getEndUsableTime());
        assertFalse(ticketDto.isHolidayYn());
        assertTrue(ticketDto.isUseYn());

    }

    @Test
    void ticketRegisterTest_fail_ParkingLotNotFound() {

        // given
        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.ticketRegister(1L, "평일 이용권", 10000,
                LocalTime.of(0,0,0),
                LocalTime.of(23,59,59), false));

        //then
        assertEquals(ErrorCode.PARKING_LOT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void ticketUpdateTest_success() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울 어딘가")
            .lat(35)
            .lng(120)
            .spaceCount(100)
            .useYn(true)
            .build();

        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.of(parkingLot));

        Ticket ticket = Ticket.builder()
            .id(1L)
            .parkingLot(parkingLot)
            .name("평일 이용권")
            .fee(10000)
            .startUsableTime(LocalTime.of(0,0,0))
            .endUsableTime(LocalTime.of(23,59,59))
            .holidayYn(false)
            .useYn(true)
            .build();

        given(ticketRepository.findByIdAndParkingLot(anyLong(), any()))
            .willReturn(Optional.of(ticket));

        given(ticketRepository.save(any()))
            .willReturn(ticket);

        // when
        TicketDto ticketDto = parkingLotServiceImpl.ticketUpdate(
            1L, 1L, "평일 이용권", 10000,
            LocalTime.of(0,0,0),
            LocalTime.of(23,59,59),
            false, true
            );

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);

        // then
        verify(ticketRepository, times(1)).save(captor.capture());
        assertEquals(1L, ticketDto.getId());
        assertEquals(1L, ticketDto.getParkingLot().getId());
        assertEquals("평일 이용권", ticketDto.getName());
        assertEquals(10000, ticketDto.getFee());
        assertEquals(LocalTime.of(0,0,0), ticketDto.getStartUsableTime());
        assertEquals(LocalTime.of(23,59,59), ticketDto.getEndUsableTime());
        assertFalse(ticketDto.isHolidayYn());
        assertTrue(ticketDto.isUseYn());

    }

    @Test
    void ticketUpdate_fail_ParkingLotNotFound() {

        // given
        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.ticketUpdate(1L, 1L, "평일 이용권", 10000,
                LocalTime.of(0,0,0),
                LocalTime.of(23,59,59),
                false, true));

        // then
        assertEquals(ErrorCode.PARKING_LOT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void ticketUpdate_fail_ParkingLotTicketNotMatch() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울 어딘가")
            .lat(35)
            .lng(120)
            .spaceCount(100)
            .useYn(true)
            .build();

        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.of(parkingLot));

        given(ticketRepository.findByIdAndParkingLot(anyLong(), any()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.ticketUpdate(1L, 1L, "평일 이용권", 10000,
                LocalTime.of(0,0,0),
                LocalTime.of(23,59,59),
                false, true));

        // then
        assertEquals(ErrorCode.PARKING_LOT_TICKET_NOT_MATCH, exception.getErrorCode());

    }

    @Test
    void getTicketsTest_success() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울 어딘가")
            .lat(35)
            .lng(120)
            .spaceCount(100)
            .useYn(true)
            .build();

        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.of(parkingLot));

        List<Ticket> ticketList = Arrays.asList(
            Ticket.builder()
                .id(1L)
                .parkingLot(parkingLot)
                .name("평일 이용권")
                .fee(10000)
                .holidayYn(false)
                .useYn(true)
                .build(),
            Ticket.builder()
                .id(2L)
                .parkingLot(parkingLot)
                .name("주말 이용권")
                .fee(10000)
                .holidayYn(true)
                .useYn(true)
                .build()
        );

        given(ticketRepository.findByParkingLot(any()))
            .willReturn(ticketList);

        // when
        List<TicketDto> ticketDtoList = parkingLotServiceImpl.getTickets(1L);

        // then
        assertEquals(1L, ticketDtoList.get(0).getId());
        assertEquals(1L, ticketDtoList.get(0).getParkingLot().getId());
        assertEquals("평일 이용권", ticketDtoList.get(0).getName());
        assertEquals(10000, ticketDtoList.get(0).getFee());
        assertFalse(ticketDtoList.get(0).isHolidayYn());
        assertTrue(ticketDtoList.get(0).isUseYn());
        assertEquals(2L, ticketDtoList.get(1).getId());
        assertEquals(1L, ticketDtoList.get(1).getParkingLot().getId());
        assertEquals("주말 이용권", ticketDtoList.get(1).getName());
        assertEquals(10000, ticketDtoList.get(1).getFee());
        assertTrue(ticketDtoList.get(1).isHolidayYn());
        assertTrue(ticketDtoList.get(1).isUseYn());

    }

    @Test
    void getTickets_fail_ParkingLotNotFound() {

        // given
        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.getTickets(1L));

        // then
        assertEquals(ErrorCode.PARKING_LOT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void getTicket_success() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울 어딘가")
            .lat(35)
            .lng(120)
            .spaceCount(100)
            .useYn(true)
            .build();

        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.of(parkingLot));

        Ticket ticket = Ticket.builder()
            .id(1L)
            .parkingLot(parkingLot)
            .name("평일 이용권")
            .fee(10000)
            .startUsableTime(LocalTime.of(0,0,0))
            .endUsableTime(LocalTime.of(23,59,59))
            .holidayYn(false)
            .useYn(true)
            .build();

        given(ticketRepository.findByIdAndParkingLot(anyLong(), any()))
            .willReturn(Optional.of(ticket));

        // when
        TicketDto ticketDto = parkingLotServiceImpl.getTicket(1L, 1L);

        // then
        assertEquals(1L, ticketDto.getId());
        assertEquals(1L, ticketDto.getParkingLot().getId());
        assertEquals("평일 이용권", ticketDto.getName());
        assertEquals(10000, ticketDto.getFee());
        assertEquals(LocalTime.of(0,0,0), ticketDto.getStartUsableTime());
        assertEquals(LocalTime.of(23,59,59), ticketDto.getEndUsableTime());
        assertFalse(ticketDto.isHolidayYn());
        assertTrue(ticketDto.isUseYn());

    }

    @Test
    void getTicket_fail_ParkingLotNotFound() {

        // given
        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.getTicket(1L, 1L));

        // then
        assertEquals(ErrorCode.PARKING_LOT_NOT_FOUND, exception.getErrorCode());


    }

    @Test
    void getTicket_fail_ParkingLotTicketNotMatch() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울 어딘가")
            .lat(35)
            .lng(120)
            .spaceCount(100)
            .useYn(true)
            .build();

        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.of(parkingLot));

        given(ticketRepository.findByIdAndParkingLot(anyLong(), any()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.getTicket(1L, 1L));

        // then
        assertEquals(ErrorCode.PARKING_LOT_TICKET_NOT_MATCH, exception.getErrorCode());


    }

    @Test
    void getUsableTickets_success() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울 어딘가")
            .lat(35)
            .lng(120)
            .spaceCount(100)
            .useYn(true)
            .build();

        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.of(parkingLot));

        List<Ticket> ticketList = Arrays.asList(
            Ticket.builder()
                .id(1L)
                .parkingLot(parkingLot)
                .name("평일 오후이용권")
                .fee(10000)
                .startUsableTime(LocalTime.of(12,0,0))
                .endUsableTime(LocalTime.of(23,59,59))
                .holidayYn(false)
                .useYn(true)
                .build(),
            Ticket.builder()
                .id(2L)
                .parkingLot(parkingLot)
                .name("평일 종일이용권")
                .fee(20000)
                .startUsableTime(LocalTime.of(0,0,0))
                .endUsableTime(LocalTime.of(23,59,59))
                .holidayYn(false)
                .useYn(true)
                .build()
        );

        given(ticketRepository.findByParkingLotAndHolidayYnAndUseYn(parkingLot, false, true))
            .willReturn(ticketList);

        // when
        List<TicketUserInfo> ticketUserInfoList = parkingLotServiceImpl.getUsableTickets(1L);

        // then
        assertEquals(1L, ticketUserInfoList.get(0).getId());
        assertEquals(1L, ticketUserInfoList.get(0).getParkingLotId());
        assertEquals("평일 오후이용권", ticketUserInfoList.get(0).getName());
        assertEquals(10000, ticketUserInfoList.get(0).getFee());

        assertEquals(2L, ticketUserInfoList.get(1).getId());
        assertEquals(1L, ticketUserInfoList.get(1).getParkingLotId());
        assertEquals("평일 종일이용권", ticketUserInfoList.get(1).getName());
        assertEquals(20000, ticketUserInfoList.get(1).getFee());
    }

    @Test
    void getUsableTickets_fail_ParkingLotNotFound() {

        // given
        given(parkingLotRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.getUsableTickets(1L));

        // then
        assertEquals(ErrorCode.PARKING_LOT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void getParkingLotWithUseYn_success() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울 어딘가")
            .lat(35)
            .lng(120)
            .spaceCount(100)
            .useYn(true)
            .build();

        given(parkingLotRepository.findByIdAndUseYn(1L, true))
            .willReturn(Optional.of(parkingLot));

        // when
        ParkingLotDto parkingLotDto = parkingLotServiceImpl.getParkingLotWithUseYn(1L);

        // then
        assertEquals(1L, parkingLotDto.getId());
        assertEquals("서울주차장", parkingLotDto.getName());
        assertEquals("서울 어딘가", parkingLotDto.getAddress());
        assertEquals(35, parkingLotDto.getLat());
        assertEquals(120, parkingLotDto.getLng());
        assertEquals(100, parkingLotDto.getSpaceCount());
        assertTrue(parkingLotDto.isUseYn());

    }

    @Test
    void getParkingLotWithUseYn_fail_ParkingLotNotFound() {

        // given
        given(parkingLotRepository.findByIdAndUseYn(anyLong(), anyBoolean()))
            .willReturn(Optional.empty());

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> parkingLotServiceImpl.getParkingLotWithUseYn(1L));

        // then
        assertEquals(ErrorCode.PARKING_LOT_NOT_FOUND, exception.getErrorCode());

    }
}
