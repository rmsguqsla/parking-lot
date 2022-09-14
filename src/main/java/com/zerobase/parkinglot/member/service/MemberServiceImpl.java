package com.zerobase.parkinglot.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.zerobase.parkinglot.member.entity.Car;
import com.zerobase.parkinglot.member.entity.Member;
import com.zerobase.parkinglot.member.exception.MemberException;
import com.zerobase.parkinglot.member.model.CarDto;
import com.zerobase.parkinglot.member.model.MemberDto;
import com.zerobase.parkinglot.member.repository.CarRepository;
import com.zerobase.parkinglot.member.repository.MemberRepository;
import com.zerobase.parkinglot.member.type.ErrorCode;
import com.zerobase.parkinglot.member.type.Role;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final CarRepository carRepository;

    @Override
    @Transactional
    public MemberDto registerMember(String email, String name, String password, String phone) {

        validateRegisterMember(email);

        return MemberDto.fromEntity(
            memberRepository.save(Member.builder()
            .email(email)
            .name(name)
            .password(encPassword(password))
            .phone(phone)
            .role(Role.USER.getDescription())
            .regDt(LocalDateTime.now())
            .build())
        );
    }

    @Override
    public MemberDto updateMember(Long id, String name, String phone) {

        Member member = findMemberById(id);
        member.setName(name);
        member.setPhone(phone);
        member.setUpdateDt(LocalDateTime.now());

        return MemberDto.fromEntity(
            memberRepository.save(member)
        );
    }

    @Override
    public MemberDto resetPassword(Long id, String password, String newPassword) {

        Member member = findMemberById(id);

        checkPasswordEquals(password, member.getPassword());

        member.setPassword(encPassword(newPassword));
        member.setUpdateDt(LocalDateTime.now());

        return MemberDto.fromEntity(
            memberRepository.save(member)
        );
    }

    @Override
    public void deleteMember(Long id, String password) {

        Member member = findMemberById(id);

        List<Car> carList = findCarByMember(member);

        checkPasswordEquals(password, member.getPassword());

        carRepository.deleteAll(carList);
        memberRepository.delete(member);
    }

    @Override
    public CarDto registerCar(Long id, String carNumber) {

        Member member = findMemberById(id);

        validateRegisterCar(carNumber);

        return CarDto.fromEntity(
            carRepository.save(Car.builder()
                .member(member)
                .carNumber(carNumber)
                .regDt(LocalDateTime.now())
                .build())
        );
    }

    @Override
    public List<CarDto> getCarList(Long id) {

        return CarDto.fromEntityList(findCarByMember(findMemberById(id)));

    }

    @Override
    public CarDto updateCar(Long id, String carNumber, String newCarNumber) {

        Member member = findMemberById(id);

        validateRegisterCar(newCarNumber);

        Car car = findCarByMemberAndCarNumber(member, carNumber);
        car.setCarNumber(newCarNumber);
        car.setUpdateDt(LocalDateTime.now());

        return CarDto.fromEntity(
            carRepository.save(car)
        );
    }

    @Override
    public void deleteCar(Long id, String carNumber) {

        Member member = findMemberById(id);

        Car car = findCarByMemberAndCarNumber(member, carNumber);

        carRepository.delete(car);
    }

    @Override
    public String login(String email, String password) {

        Member member = findMemberByEmail(email);

        checkPasswordEquals(password, member.getPassword());

        return createToken(member);
    }

    private String createToken(Member member) {

        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = Timestamp.valueOf(expiredDateTime);

        // 토큰발행시점
        String token = JWT.create()
            .withExpiresAt(expiredDate)
            .withClaim("user_id", member.getId())
            .withSubject(member.getName())
            .withIssuer(member.getEmail())
            .sign(Algorithm.HMAC512("parkinglot".getBytes()));

        return token;
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private List<Car> findCarByMember(Member member) {

        return carRepository.findByMember(member);

    }

    private Car findCarByMemberAndCarNumber(Member member, String carNumber) {

        return carRepository.findByMemberAndCarNumber(member, carNumber).orElseThrow(
            () -> new MemberException(ErrorCode.MEMBER_CAR_NUMBER_NOT_MATCH)
        );

    }

    private void checkPasswordEquals(String plaintext, String hashed) {

        if (!passwordEquals(plaintext, hashed)) {
            throw new MemberException(ErrorCode.PASSWORD_NOT_MATCH);

        }
    }

    private boolean passwordEquals(String plaintext, String hashed) {

        if (plaintext == null || plaintext.length() < 1) {
            return false;
        }

        if (hashed == null || hashed.length() < 1) {
            return false;
        }

        return BCrypt.checkpw(plaintext, hashed);
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(
            () -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)
        );
    }

    private void validateRegisterCar(String carNumber) {
        if (carRepository.countByCarNumber(carNumber) > 0) {
            throw new MemberException(ErrorCode.CAR_ALREADY_EXIST);
        }
    }

    private void validateRegisterMember(String email) {
        if (memberRepository.countByEmail(email) > 0) {
            throw new MemberException(ErrorCode.MEMBER_ALREADY_EXIST);
        }
    }

    private String encPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
