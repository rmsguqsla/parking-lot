package com.zerobase.parkinglot.member.repository;

import com.zerobase.parkinglot.member.entity.Car;
import com.zerobase.parkinglot.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

    int countByCarNumber(String carNumber);

    Optional<Car> findByMemberAndCarNumber(Member member, String carNumber);

    List<Car> findByMember(Member member);
}
