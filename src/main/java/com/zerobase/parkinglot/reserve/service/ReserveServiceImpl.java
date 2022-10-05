package com.zerobase.parkinglot.reserve.service;

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
import com.zerobase.parkinglot.reserve.type.StatusType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService{

    private final MemberRepository memberRepository;
    private final CarRepository carRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final TicketRepository ticketRepository;
    private final ReserveRepository reserveRepository;

    // 예약
    @Transactional
    @Override
    public ReserveDto reserveRegister(Long memberId, Long carId, Long parkingLotId,
        Long ticketId, Integer estimatedHour, Integer estimatedMinute) {

        ParkingLot parkingLot = findParkingLotByIdAndUseYn(parkingLotId);

        // Using 상태의 예약 수를 파악
        // if 예약 수 >= 주차장 자리 => 예약 x
        checkReserveFull(parkingLot);

        Member member = findMemberById(memberId);

        Car car = findCarByIdAndMember(carId, member);

        Ticket ticket = findTicketByIdAndParkingLot(ticketId, parkingLot);

        // 최소도착예정시간 조건
        // 조건1. 시간은 0~23시
        // 조건2. 분은 0~59분
        // 조건3. 현재시간보다 과거시간은 x
        // 조건4. 최소 이용권 끝 사용 가능 시간 보다 1시간 전에 예약해야 함
        LocalDateTime minEstimatedDt = makeMinEstimatedDt(estimatedHour,
            estimatedMinute, ticketId, parkingLot);

        // 예약종료시간은 최소도착예정시간 + 이용권 최대 이용 가능시간
        // 최대 오늘을 넘길 수 없음
        LocalDateTime reserveEndDt = getReserveEndDt(minEstimatedDt, ticket);


        return ReserveDto.fromEntity(
            reserveRepository.save(
                Reserve.builder()
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
                    .reserveDt(LocalDateTime.now())
                    .reserveEndDt(reserveEndDt)
                    .status(StatusType.Using)
                    .build()
            ));
    }

    private void checkReserveFull(ParkingLot parkingLot) {
        int reserveCount = reserveRepository.countByParkingLotAndAddressAndStatus(parkingLot.getName(), parkingLot.getAddress(), StatusType.Using);
        int spaceCount = parkingLot.getSpaceCount();
        if (reserveCount >= spaceCount) {
            throw new ReserveException(ErrorCode.RESERVE_FULL);
        }
    }

    private LocalDateTime getReserveEndDt(LocalDateTime minEstimatedDt, Ticket ticket) {
        // 예약종료시간은 예약시간 + 이용권 최대 이용 가능시간
        // 최대 오늘을 넘길 수 없음
        int hour = ticket.getMaxUsableTime().getHour();
        int minute = ticket.getMaxUsableTime().getMinute();
        int second = ticket.getMaxUsableTime().getSecond();

        LocalDateTime reserveEndDt = minEstimatedDt.plusSeconds(second).plusMinutes(minute).plusHours(hour);

        LocalDateTime todayMax = LocalDate.now().atTime(LocalTime.MAX);
        if (reserveEndDt.isAfter(todayMax)) {
            reserveEndDt = todayMax;
        }

        return reserveEndDt;
    }

    // 예약 취소
    @Transactional
    @Override
    public ReserveDto reserveCancel(Long memberId, Long reserveId) {

        Member member = findMemberById(memberId);

        Reserve reserve = findReserveByIdAndEmail(reserveId, member.getEmail());

        // 상태가 Using인지(Cancel, Complete이면 exception)
        checkStatusIsUsing(reserve);

        // 최소도착예정시간 30분전엔 취소 x
        checkBefore30(reserve);

        reserve.setStatus(StatusType.Cancel);
        reserve.setCancelDt(LocalDateTime.now());

        return ReserveDto.fromEntity(reserveRepository.save(reserve));
    }

    @Override
    public List<ReserveInfo> getReserves(Long id) {
        Member member = findMemberById(id);
        List<Reserve> reserveList = findReserveByEmail(member.getEmail());
        return ReserveInfo.fromEntityList(reserveList);
    }

    @Override
    public List<ReserveInfo> getAdminReserves() {
        return ReserveInfo.fromEntityList(reserveRepository.findAll());
    }

    @Override
    public ReserveInfo getReserve(Long memberId, Long reserveId) {
        Member member = findMemberById(memberId);
        return ReserveInfo.fromEntity(findReserveByIdAndEmail(reserveId, member.getEmail()));
    }

    @Override
    public ReserveInfo getAdminReserve(Long id) {
        return ReserveInfo.fromEntity(findReserveById(id));
    }

    private Reserve findReserveById(Long id) {
        return reserveRepository.findById(id)
            .orElseThrow(() -> new ReserveException(ErrorCode.RESERVE_NOT_FOUND));
    }

    private List<Reserve> findReserveByEmail(String email) {
        return reserveRepository.findByEmail(email);
    }

    private void checkBefore30(Reserve reserve) {
        if (reserve.getMinEstimatedDt().minusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new ReserveException(ErrorCode.NOT_CANCEL_RESERVE);
        }
    }


    private void checkStatusIsUsing(Reserve reserve) {
        if (reserve.getStatus().equals(StatusType.Cancel)) {
            throw new ReserveException(ErrorCode.STATUS_CANCEL);
        }
        if (reserve.getStatus().equals(StatusType.Complete)) {
            throw new ReserveException(ErrorCode.STATUS_COMPLETE);
        }
    }

    private Reserve findReserveByIdAndEmail(Long reserveId, String email) {
        return reserveRepository.findByIdAndEmail(reserveId, email)
            .orElseThrow(() -> new ReserveException(ErrorCode.RESERVE_NOT_FOUND));
    }

    private Car findCarByIdAndMember(Long id, Member member) {
        return carRepository.findByIdAndMember(id, member)
            .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_CAR_NUMBER_NOT_MATCH));
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(
            () -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)
        );
    }

    private ParkingLot findParkingLotByIdAndUseYn(Long id) {
        return parkingLotRepository.findByIdAndUseYn(id, true)
            .orElseThrow(() -> new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));
    }

    private Ticket findTicketByIdAndParkingLot(Long ticketId, ParkingLot parkingLot) {
        return ticketRepository.findByIdAndParkingLot(ticketId, parkingLot)
            .orElseThrow(() -> new ParkingLotException(ErrorCode.PARKING_LOT_TICKET_NOT_MATCH));
    }

    private LocalTime getEndUsableTime(Long ticketId, ParkingLot parkingLot) {
        Ticket ticket = findTicketByIdAndParkingLot(ticketId, parkingLot);
        return ticket.getEndUsableTime();
    }

    private LocalDateTime makeMinEstimatedDt(Integer estimatedHour, Integer estimatedMinute, Long ticketId, ParkingLot parkingLot) {

        // 조건1. 시간은 0~23시
        // 조건2. 분은 0~59분
        // 조건3. 현재시간보다 과거시간은 x
        // 조건4. 최소 이용권 끝 사용 가능 시간 보다 1시간 전에 예약해야 함
        log.info(LocalDateTime.now().toString());
        LocalTime estimateTime = LocalTime.of(estimatedHour, estimatedMinute);
        if (estimatedHour < 0 || estimatedHour > 23
            || estimatedMinute < 0 || estimatedMinute > 59) {
            throw new ReserveException(ErrorCode.INVALID_RANGE_HOUR_MINUTE);
        }
        if (LocalTime.now().compareTo(estimateTime) > 0) {
            throw new RuntimeException("현재시간보다 과거입니다.");
        }
        if(getEndUsableTime(ticketId, parkingLot).isBefore(estimateTime.plusHours(1))) {
            throw new RuntimeException("이용권 끝 유효시간보다 최소 1시간 일찍 예약하셔야합니다.");
        }
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(estimatedHour, estimatedMinute));
    }

}
