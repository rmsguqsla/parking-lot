package com.zerobase.parkinglot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.member.entity.Car;
import com.zerobase.parkinglot.member.entity.Member;
import com.zerobase.parkinglot.member.exception.MemberException;
import com.zerobase.parkinglot.member.repository.CarRepository;
import com.zerobase.parkinglot.member.repository.MemberRepository;
import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import com.zerobase.parkinglot.parkinglot.entity.Ticket;
import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import com.zerobase.parkinglot.parkinglot.repository.ParkingLotRepository;
import com.zerobase.parkinglot.parkinglot.repository.TicketRepository;
import com.zerobase.parkinglot.reserve.entity.Reserve;
import com.zerobase.parkinglot.reserve.exception.ReserveException;
import com.zerobase.parkinglot.reserve.model.ReserveDto;
import com.zerobase.parkinglot.reserve.model.ReserveInfo;
import com.zerobase.parkinglot.reserve.repository.ReserveRepository;
import com.zerobase.parkinglot.reserve.service.ReserveServiceImpl;
import com.zerobase.parkinglot.reserve.type.StatusType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class ReserveServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private ParkingLotRepository parkingLotRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ReserveRepository reserveRepository;

    @InjectMocks
    private ReserveServiceImpl reserveServiceImpl;

    @Test
    void reserveRegisterTest_success() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울시 어딘가")
            .spaceCount(10)
            .build();

        given(parkingLotRepository.findByIdAndUseYn(anyLong(), anyBoolean()))
            .willReturn(Optional.of(parkingLot));

        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Car car = Car.builder()
            .id(1L)
            .carNumber("12가3456")
            .build();
        given(carRepository.findByIdAndMember(anyLong(), any())).willReturn(Optional.of(car));

        Ticket ticket = Ticket.builder()
            .id(1L)
            .name("1시간이용권")
            .fee(1000)
            .startUsableTime(LocalTime.of(0,0,0))
            .endUsableTime(LocalTime.of(23,59,59))
            .maxUsableTime(LocalTime.of(1,0,0))
            .build();
        given(ticketRepository.findByIdAndParkingLot(anyLong(), any())).willReturn(Optional.of(ticket));

        LocalDateTime minEstimatedDt = LocalDateTime.of(LocalDate.now(), LocalTime.of(22,30,0));

        int second = ticket.getMaxUsableTime().getSecond();
        int minute = ticket.getMaxUsableTime().getMinute();
        int hour = ticket.getMaxUsableTime().getHour();
        LocalDateTime reserveEndDt = minEstimatedDt.plusSeconds(second).plusMinutes(minute).plusHours(hour);

        parkingLot.plusReserveCount();

        given(reserveRepository.save(any()))
            .willReturn(
                Reserve.builder()
                    .id(1L)
                    .email(member.getEmail())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .carNumber(car.getCarNumber())
                    .parkingLot(parkingLot.getName())
                    .address(parkingLot.getAddress())
                    .ticket(ticket.getName())
                    .fee(ticket.getFee())
                    .minEstimatedDt(minEstimatedDt)
                    .maxEstimatedDt(minEstimatedDt.plusMinutes(30))
                    .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0,0)))
                    .reserveEndDt(reserveEndDt)
                    .status(StatusType.Using)
                    .build()
            );

        // when
        ReserveDto reserveDto = reserveServiceImpl.reserveRegister(1L, 1L, 1L, 1L, 22, 0);
        ArgumentCaptor<Reserve> captor = ArgumentCaptor.forClass(Reserve.class);

        // then
        verify(reserveRepository, times(1)).save(captor.capture());
        assertEquals(reserveDto.getId(), 1L);
        assertEquals(reserveDto.getName(), "홍길동");
        assertEquals(reserveDto.getPhone(), "010-1234-1234");
        assertEquals(reserveDto.getCarNumber(), "12가3456");
        assertEquals(reserveDto.getParkingLot(), "서울주차장");
        assertEquals(reserveDto.getAddress(), "서울시 어딘가");
        assertEquals(reserveDto.getTicket(), "1시간이용권");
        assertEquals(reserveDto.getFee(), 1000);
        assertEquals(reserveDto.getMinEstimatedDt(), minEstimatedDt);
        assertEquals(reserveDto.getMaxEstimatedDt(), minEstimatedDt.plusMinutes(30));
        assertEquals(reserveDto.getReserveDt(), LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0,0)));
        assertEquals(reserveDto.getStatus(), StatusType.Using);

    }

    @Test
    void reserveRegisterTest_fail_parkingLotNotFound() {

        // given
        given(parkingLotRepository.findByIdAndUseYn(anyLong(), anyBoolean()))
            .willThrow(new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> reserveServiceImpl.reserveRegister(1L, 1L, 1L, 1L, 22, 0));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.PARKING_LOT_NOT_FOUND);
        assertEquals(exception.getErrorMessage(), "주차장이 존재하지 않습니다.");
    }

    @Test
    void reserveRegisterTest_fail_memberNotFound() {

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울시 어딘가")
            .spaceCount(10)
            .build();

        given(parkingLotRepository.findByIdAndUseYn(anyLong(), anyBoolean()))
            .willReturn(Optional.of(parkingLot));

        given(memberRepository.findById(anyLong()))
            .willThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // when
        MemberException exception = assertThrows(MemberException.class,
            () -> reserveServiceImpl.reserveRegister(1L, 1L, 1L, 1L, 22, 0));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
        assertEquals(exception.getErrorMessage(), "회원이 존재하지 않습니다.");

    }

    @Test
    void reserveRegisterTest_fail_memberCarNumberNotMatch() {
        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울시 어딘가")
            .spaceCount(10)
            .build();

        given(parkingLotRepository.findByIdAndUseYn(anyLong(), anyBoolean()))
            .willReturn(Optional.of(parkingLot));

        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        given(carRepository.findByIdAndMember(anyLong(), any()))
            .willThrow(new MemberException(ErrorCode.MEMBER_CAR_NUMBER_NOT_MATCH));

        // when
        MemberException exception = assertThrows(MemberException.class,
            () -> reserveServiceImpl.reserveRegister(1L, 1L, 1L, 1L, 22, 0));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_CAR_NUMBER_NOT_MATCH);
        assertEquals(exception.getErrorMessage(), "회원님에게 등록되지 않은 차입니다.");
    }

    @Test
    void reserveRegisterTest_fail_ParkingLotTicketNotMatch() {
        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울시 어딘가")
            .spaceCount(10)
            .build();

        given(parkingLotRepository.findByIdAndUseYn(anyLong(), anyBoolean()))
            .willReturn(Optional.of(parkingLot));

        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Car car = Car.builder()
            .id(1L)
            .carNumber("12가3456")
            .build();
        given(carRepository.findByIdAndMember(anyLong(), any())).willReturn(Optional.of(car));

        given(ticketRepository.findByIdAndParkingLot(anyLong(), any()))
            .willThrow(new ParkingLotException(ErrorCode.PARKING_LOT_TICKET_NOT_MATCH));

        // when
        ParkingLotException exception = assertThrows(ParkingLotException.class,
            () -> reserveServiceImpl.reserveRegister(1L, 1L, 1L, 1L, 22, 0));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.PARKING_LOT_TICKET_NOT_MATCH);
        assertEquals(exception.getErrorMessage(), "주차장에 존재하지 않는 이용권입니다.");
    }

    @Test
    void reserveRegisterTest_fail_InvalidRangeHourMinute() {
        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울시 어딘가")
            .spaceCount(10)
            .build();

        given(parkingLotRepository.findByIdAndUseYn(anyLong(), anyBoolean()))
            .willReturn(Optional.of(parkingLot));

        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Car car = Car.builder()
            .id(1L)
            .carNumber("12가3456")
            .build();
        given(carRepository.findByIdAndMember(anyLong(), any())).willReturn(Optional.of(car));

        Ticket ticket = Ticket.builder()
            .id(1L)
            .name("1시간이용권")
            .fee(1000)
            .startUsableTime(LocalTime.of(0,0,0))
            .endUsableTime(LocalTime.of(23,59,59))
            .maxUsableTime(LocalTime.of(1,0,0))
            .build();
        given(ticketRepository.findByIdAndParkingLot(anyLong(), any())).willReturn(Optional.of(ticket));

        // when
        ReserveException exception = assertThrows(ReserveException.class,
            () -> reserveServiceImpl.reserveRegister(1L, 1L, 1L, 1L, 24, 60));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_RANGE_HOUR_MINUTE);
        assertEquals(exception.getErrorMessage(), "시간은 0~23시, 분은 0~59분 입니다.");

    }

    @Test
    void reserveRegisterTest_fail_TimeInThePast() {
        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울시 어딘가")
            .spaceCount(10)
            .build();

        given(parkingLotRepository.findByIdAndUseYn(anyLong(), anyBoolean()))
            .willReturn(Optional.of(parkingLot));

        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Car car = Car.builder()
            .id(1L)
            .carNumber("12가3456")
            .build();
        given(carRepository.findByIdAndMember(anyLong(), any())).willReturn(Optional.of(car));

        Ticket ticket = Ticket.builder()
            .id(1L)
            .name("1시간이용권")
            .fee(1000)
            .startUsableTime(LocalTime.of(0,0,0))
            .endUsableTime(LocalTime.of(23,59,59))
            .maxUsableTime(LocalTime.of(1,0,0))
            .build();
        given(ticketRepository.findByIdAndParkingLot(anyLong(), any())).willReturn(Optional.of(ticket));

        // when
        LocalTime time = LocalTime.now().minusMinutes(30);
        ReserveException exception = assertThrows(ReserveException.class,
            () -> reserveServiceImpl.reserveRegister(1L, 1L, 1L, 1L, time.getHour(), time.getMinute()));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.TIME_IN_THE_PAST);
        assertEquals(exception.getErrorMessage(), "현재시간보다 과거시간입니다.");

    }

    @Test
    void reserveCancelTest_success() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        LocalDateTime minEstimatedDt = LocalDateTime.of(LocalDate.now(), LocalTime.now().plusMinutes(50));

        Reserve reserve = Reserve.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .carNumber("12가3456")
            .parkingLot("서울주차장")
            .address("서울시 어딘가")
            .ticket("1시간이용권")
            .fee(1000)
            .minEstimatedDt(minEstimatedDt)
            .maxEstimatedDt(minEstimatedDt.plusMinutes(30))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0,0)))
            .status(StatusType.Using)
            .cancelDt(null)
            .build();
        given(reserveRepository.findByIdAndEmail(anyLong(), anyString()))
            .willReturn(Optional.of(reserve));

        given(reserveRepository.save(any()))
            .willReturn(reserve);

        //when
        ReserveDto reserveDto = reserveServiceImpl.reserveCancel(1L, 1L);
        ArgumentCaptor<Reserve> captor = ArgumentCaptor.forClass(Reserve.class);

        //then
        verify(reserveRepository, times(1)).save(captor.capture());
        assertEquals(reserveDto.getId(), 1L);
        assertEquals(reserveDto.getName(), "홍길동");
        assertEquals(reserveDto.getPhone(), "010-1234-1234");
        assertEquals(reserveDto.getCarNumber(), "12가3456");
        assertEquals(reserveDto.getParkingLot(), "서울주차장");
        assertEquals(reserveDto.getAddress(), "서울시 어딘가");
        assertEquals(reserveDto.getTicket(), "1시간이용권");
        assertEquals(reserveDto.getFee(), 1000);
        assertEquals(reserveDto.getMinEstimatedDt(), minEstimatedDt);
        assertEquals(reserveDto.getMaxEstimatedDt(), minEstimatedDt.plusMinutes(30));
        assertEquals(reserveDto.getReserveDt(), LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0,0)));
        assertEquals(reserveDto.getStatus(), StatusType.Cancel);
    }

    @Test
    void reserveCancelTest_fail_memberNotFound() {
        //given
        given(memberRepository.findById(anyLong()))
            .willThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> reserveServiceImpl.reserveCancel(1L, 1L));
        //then
        assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
        assertEquals(exception.getErrorMessage(), "회원이 존재하지 않습니다.");
    }

    @Test
    void reserveCancelTest_fail_reserveNotFound() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(reserveRepository.findByIdAndEmail(anyLong(), anyString()))
            .willThrow(new ReserveException(ErrorCode.RESERVE_NOT_FOUND));

        //when
        ReserveException exception = assertThrows(ReserveException.class,
            () -> reserveServiceImpl.reserveCancel(1L, 1L));
        //then
        assertEquals(exception.getErrorCode(), ErrorCode.RESERVE_NOT_FOUND);
        assertEquals(exception.getErrorMessage(), "주차장 예약 내역이 존재하지 않습니다.");
    }

    @Test
    void reserveCancelTest_fail_statusCancel() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        LocalDateTime minEstimatedDt = LocalDateTime.of(LocalDate.now(), LocalTime.now().plusMinutes(50));

        Reserve reserve = Reserve.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .carNumber("12가3456")
            .parkingLot("서울주차장")
            .address("서울시 어딘가")
            .ticket("1시간이용권")
            .fee(1000)
            .minEstimatedDt(minEstimatedDt)
            .maxEstimatedDt(minEstimatedDt.plusMinutes(30))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0,0)))
            .status(StatusType.Cancel)
            .cancelDt(null)
            .build();

        given(reserveRepository.findByIdAndEmail(anyLong(), anyString()))
            .willReturn(Optional.of(reserve));
        //when
        ReserveException exception = assertThrows(ReserveException.class,
            () -> reserveServiceImpl.reserveCancel(1L, 1L));
        //then
        assertEquals(exception.getErrorCode(), ErrorCode.STATUS_CANCEL);
        assertEquals(exception.getErrorMessage(), "이미 취소되었습니다.");
    }

    @Test
    void reserveCancelTest_fail_statusComplete() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        LocalDateTime minEstimatedDt = LocalDateTime.of(LocalDate.now(), LocalTime.now().plusMinutes(50));

        Reserve reserve = Reserve.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .carNumber("12가3456")
            .parkingLot("서울주차장")
            .address("서울시 어딘가")
            .ticket("1시간이용권")
            .fee(1000)
            .minEstimatedDt(minEstimatedDt)
            .maxEstimatedDt(minEstimatedDt.plusMinutes(30))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0,0)))
            .status(StatusType.Complete)
            .cancelDt(null)
            .build();

        given(reserveRepository.findByIdAndEmail(anyLong(), anyString()))
            .willReturn(Optional.of(reserve));
        //when
        ReserveException exception = assertThrows(ReserveException.class,
            () -> reserveServiceImpl.reserveCancel(1L, 1L));
        //then
        assertEquals(exception.getErrorCode(), ErrorCode.STATUS_COMPLETE);
        assertEquals(exception.getErrorMessage(), "이미 사용하셨습니다.");
    }

    @Test
    void reserveCancelTest_fail_notCancelReserve() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        LocalDateTime minEstimatedDt = LocalDateTime.of(LocalDate.now(), LocalTime.now().plusMinutes(20));

        Reserve reserve = Reserve.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("홍길동")
            .phone("010-1234-1234")
            .carNumber("12가3456")
            .parkingLot("서울주차장")
            .address("서울시 어딘가")
            .ticket("1시간이용권")
            .fee(1000)
            .minEstimatedDt(minEstimatedDt)
            .maxEstimatedDt(minEstimatedDt.plusMinutes(30))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0,0)))
            .status(StatusType.Using)
            .cancelDt(null)
            .build();

        given(reserveRepository.findByIdAndEmail(anyLong(), anyString()))
            .willReturn(Optional.of(reserve));
        //when
        ReserveException exception = assertThrows(ReserveException.class,
            () -> reserveServiceImpl.reserveCancel(1L, 1L));
        //then
        assertEquals(exception.getErrorCode(), ErrorCode.NOT_CANCEL_RESERVE);
        assertEquals(exception.getErrorMessage(), "예약 취소할 수 없습니다.");
    }

    @Test
    void getReserves_success() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        List<Reserve> list = Arrays.asList(
            Reserve.builder()
                .id(1L)
                .name("홍길동")
                .phone("010-1234-1234")
                .carNumber("12가3456")
                .parkingLot("서울주차장")
                .address("서울특별시 어딘가")
                .ticket("평일 이용권")
                .fee(10000)
                .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
                .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
                .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
                .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
                .status(StatusType.Using)
                .build(),
            Reserve.builder()
                .id(2L)
                .name("홍길동")
                .phone("010-1234-1234")
                .carNumber("12가3456")
                .parkingLot("인천주차장")
                .address("인천광역시 어딘가")
                .ticket("평일 이용권")
                .fee(10000)
                .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
                .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
                .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
                .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
                .status(StatusType.Using)
                .build()
        );
        given(reserveRepository.findByEmail(any()))
            .willReturn(list);
        //when
        List<ReserveInfo> reserveInfoList = reserveServiceImpl.getReserves(1L);
        //then
        assertEquals(reserveInfoList.get(0).getParkingLot(), "서울주차장");
        assertEquals(reserveInfoList.get(1).getParkingLot(), "인천주차장");
    }

    @Test
    void getReserves_fail_memberNotFound() {
        //given
        given(memberRepository.findById(anyLong()))
            .willThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> reserveServiceImpl.getReserves(1L));
        //then
        assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
        assertEquals(exception.getErrorMessage(), "회원이 존재하지 않습니다.");
    }

    @Test
    void getReserve_success() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        Reserve reserve = Reserve.builder()
            .id(1L)
            .name("홍길동")
            .phone("010-1234-1234")
            .carNumber("12가3456")
            .parkingLot("서울주차장")
            .address("서울특별시 어딘가")
            .ticket("평일 이용권")
            .fee(10000)
            .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
            .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
            .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
            .status(StatusType.Using)
            .build();
        given(reserveRepository.findByIdAndEmail(anyLong(), anyString()))
            .willReturn(Optional.of(reserve));

        //when
        ReserveInfo reserveInfo = reserveServiceImpl.getReserve(1L, 1L);

        //then
        assertEquals(reserveInfo.getId(), 1L);
        assertEquals(reserveInfo.getName(), "홍길동");
        assertEquals(reserveInfo.getParkingLot(), "서울주차장");
    }

    @Test
    void getReserve_fail_memberNotFound() {
        //given
        given(memberRepository.findById(anyLong()))
            .willThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> reserveServiceImpl.getReserve(1L, 1L));
        //then
        assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
        assertEquals(exception.getErrorMessage(), "회원이 존재하지 않습니다.");
    }

    @Test
    void getReserve_fail_reserveNotFound() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("abc@gmail.com")
            .build();
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(reserveRepository.findByIdAndEmail(anyLong(), anyString()))
            .willThrow(new ReserveException(ErrorCode.RESERVE_NOT_FOUND));

        //when
        ReserveException exception = assertThrows(ReserveException.class,
            () -> reserveServiceImpl.getReserve(1L, 1L));
        //then
        assertEquals(exception.getErrorCode(), ErrorCode.RESERVE_NOT_FOUND);
        assertEquals(exception.getErrorMessage(), "주차장 예약 내역이 존재하지 않습니다.");
    }

    @Test
    void getAdminReserves_success() {
        //given
        List<Reserve> list = Arrays.asList(
            Reserve.builder()
                .id(1L)
                .name("홍길동")
                .phone("010-1234-1234")
                .carNumber("12가3456")
                .parkingLot("서울주차장")
                .address("서울특별시 어딘가")
                .ticket("평일 이용권")
                .fee(10000)
                .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
                .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
                .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
                .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
                .status(StatusType.Using)
                .build(),
            Reserve.builder()
                .id(2L)
                .name("홍길동")
                .phone("010-1234-1234")
                .carNumber("12가3456")
                .parkingLot("인천주차장")
                .address("인천광역시 어딘가")
                .ticket("평일 이용권")
                .fee(10000)
                .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
                .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
                .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
                .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
                .status(StatusType.Using)
                .build()
        );
        given(reserveRepository.findAll())
            .willReturn(list);
        //when
        List<ReserveInfo> reserveInfoList = reserveServiceImpl.getAdminReserves();
        //then
        assertEquals(reserveInfoList.get(0).getParkingLot(), "서울주차장");
        assertEquals(reserveInfoList.get(1).getParkingLot(), "인천주차장");
    }

    @Test
    void getAdminReserve_success() {
        //given
        Reserve reserve = Reserve.builder()
            .id(1L)
            .name("홍길동")
            .phone("010-1234-1234")
            .carNumber("12가3456")
            .parkingLot("서울주차장")
            .address("서울특별시 어딘가")
            .ticket("평일 이용권")
            .fee(10000)
            .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
            .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
            .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
            .status(StatusType.Using)
            .build();
        given(reserveRepository.findById(anyLong()))
            .willReturn(Optional.of(reserve));

        //when
        ReserveInfo reserveInfo = reserveServiceImpl.getAdminReserve(1L);

        //then
        assertEquals(reserveInfo.getId(), 1L);
        assertEquals(reserveInfo.getName(), "홍길동");
        assertEquals(reserveInfo.getParkingLot(), "서울주차장");
    }

    @Test
    void getAdminReserve_reserveNotFound() {
        //given
        given(reserveRepository.findById(anyLong()))
            .willThrow(new ReserveException(ErrorCode.RESERVE_NOT_FOUND));

        //when
        ReserveException exception = assertThrows(ReserveException.class,
            () -> reserveServiceImpl.getAdminReserve(1L));

        //then
        assertEquals(exception.getErrorCode(), ErrorCode.RESERVE_NOT_FOUND);
        assertEquals(exception.getErrorMessage(), "주차장 예약 내역이 존재하지 않습니다.");
    }
}
