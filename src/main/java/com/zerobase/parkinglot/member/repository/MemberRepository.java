package com.zerobase.parkinglot.member.repository;

import com.zerobase.parkinglot.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByIdAndPassword(Long id, String password);

    Optional<Member> findByEmail(String email);
}
