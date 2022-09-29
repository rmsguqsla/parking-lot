package com.zerobase.parkinglot.reserve.repository;

import com.zerobase.parkinglot.reserve.entity.Reserve;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    Optional<Reserve> findByIdAndEmail(Long reserveId, String email);
}
