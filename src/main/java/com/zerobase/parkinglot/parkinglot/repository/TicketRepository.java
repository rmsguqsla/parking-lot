package com.zerobase.parkinglot.parkinglot.repository;

import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import com.zerobase.parkinglot.parkinglot.entity.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByName(String name);

    Optional<Ticket> findByIdAndParkingLot(Long ticketId, ParkingLot parkingLot);

    List<Ticket> findByParkingLot(ParkingLot parkingLot);

    List<Ticket> findByParkingLotAndHolidayYnAndUseYn(ParkingLot parkingLot, boolean isHoliday, boolean isUseYn);
}
