package com.zerobase.parkinglot.parkinglot.repository;

import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    Optional<ParkingLot> findByIdAndUseYn(Long id, boolean useYn);

    List<ParkingLot> findAllByUseYn(boolean useYn);

    Optional<ParkingLot> findByNameAndAddress(String name, String address);
}
