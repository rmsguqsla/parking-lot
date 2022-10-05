package com.zerobase.parkinglot.reserve.repository;

import com.zerobase.parkinglot.reserve.entity.Reserve;
import com.zerobase.parkinglot.reserve.type.StatusType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    Optional<Reserve> findByIdAndEmail(Long reserveId, String email);

    int countByParkingLotAndAddressAndStatus(String name, String address, StatusType using);

    List<Reserve> findByStatus(StatusType using);

    List<Reserve> findByEmail(String email);
}
