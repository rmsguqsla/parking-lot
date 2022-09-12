package com.zerobase.parkinglot.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.zerobase.parkinglot.member.entity.Car;
import com.zerobase.parkinglot.member.entity.Member;
import com.zerobase.parkinglot.member.exception.MemberException;
import com.zerobase.parkinglot.member.model.CarDelete;
import com.zerobase.parkinglot.member.model.CarDto;
import com.zerobase.parkinglot.member.model.CarRegister;
import com.zerobase.parkinglot.member.model.CarUpdate;
import com.zerobase.parkinglot.member.model.MemberDelete;
import com.zerobase.parkinglot.member.model.MemberDto;
import com.zerobase.parkinglot.member.model.MemberLogin;
import com.zerobase.parkinglot.member.model.MemberRegister.Request;
import com.zerobase.parkinglot.member.model.MemberResetPassword;
import com.zerobase.parkinglot.member.model.MemberUpdate;
import com.zerobase.parkinglot.member.repository.CarRepository;
import com.zerobase.parkinglot.member.repository.MemberRepository;
import com.zerobase.parkinglot.member.type.ErrorCode;
import com.zerobase.parkinglot.member.type.Role;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final CarRepository carRepository;

    @Override
    public MemberDto registerMember(Request request) {

        validateRegisterMember(request.getEmail());

        return MemberDto.fromEntity(
            memberRepository.save(Member.builder()
            .email(request.getEmail())
            .name(request.getName())
            .password(encPassword(request.getPassword()))
            .phone(request.getPhone())
            .role(Role.USER.getDescription())
            .regDt(LocalDateTime.now())
            .build())
        );
    }

    @Override
    public MemberDto updateMember(Long id, MemberUpdate.Request request) {

        Member member = findMemberById(id);
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setUpdateDt(LocalDateTime.now());

        return MemberDto.fromEntity(
            memberRepository.save(member)
        );
    }

    @Override
    public MemberDto resetPassword(Long id, MemberResetPassword.Request request) {

        Member member = findMemberById(id);

        checkPasswordEquals(request.getPassword(), member.getPassword());

        member.setPassword(encPassword(request.getNewPassword()));
        member.setUpdateDt(LocalDateTime.now());

        return MemberDto.fromEntity(
            memberRepository.save(member)
        );
    }

    @Override
    public void deleteMember(Long id, MemberDelete.Request request) {

        Member member = findMemberById(id);

        List<Car> carList = findCarByMember(member);

        checkPasswordEquals(request.getPassword(), member.getPassword());

        carRepository.deleteAll(carList);
        memberRepository.delete(member);
    }

    @Override
    public CarDto registerCar(Long id, CarRegister.Request request) {

        Member member = findMemberById(id);

        validateRegisterCar(request.getCarNumber());

        return CarDto.fromEntity(
            carRepository.save(Car.builder()
                .member(member)
                .carNumber(request.getCarNumber())
                .regDt(LocalDateTime.now())
                .build())
        );
    }

    @Override
    public List<CarDto> getCars(Long id) {

        Member member = findMemberById(id);

        List<Car> carList = findCarByMember(member);

        return CarDto.fromEntityList(carList);
    }

    @Override
    public CarDto updateCar(Long id, CarUpdate.Request request) {

        Member member = findMemberById(id);

        validateRegisterCar(request.getNewCarNumber());

        Car car = findCarByMemberAndCarNumber(member, request.getCarNumber());
        car.setCarNumber(request.getNewCarNumber());
        car.setUpdateDt(LocalDateTime.now());

        return CarDto.fromEntity(
            carRepository.save(car)
        );
    }

    @Override
    public void deleteCar(Long id, CarDelete.Request request) {

        Member member = findMemberById(id);

        Car car = findCarByMemberAndCarNumber(member, request.getCarNumber());

        carRepository.delete(car);
    }

    @Override
    public String login(MemberLogin.Request request) {

        Member member = findMemberByEmail(request.getEmail());

        checkPasswordEquals(request.getPassword(), member.getPassword());

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
