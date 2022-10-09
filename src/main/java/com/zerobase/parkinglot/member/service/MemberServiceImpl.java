package com.zerobase.parkinglot.member.service;

import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.member.entity.Car;
import com.zerobase.parkinglot.member.entity.Member;
import com.zerobase.parkinglot.member.exception.MemberException;
import com.zerobase.parkinglot.member.model.CarDto;
import com.zerobase.parkinglot.member.model.MemberDto;
import com.zerobase.parkinglot.member.repository.CarRepository;
import com.zerobase.parkinglot.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final CarRepository carRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return findMemberByEmail(email);
    }

    @Override
    @Transactional
    public MemberDto registerMember(String email, String name, String password, String phone, String role) {

        validateRegisterMember(email);

        return MemberDto.fromEntity(
            memberRepository.save(Member.builder()
            .email(email)
            .name(name)
            .password(passwordEncoder.encode(password))
            .phone(phone)
            .role(role)
            .regDt(LocalDateTime.now())
            .build())
        );
    }

    @Override
    @Transactional
    public Member authenticate(String email, String password) {
        Member member = findMemberByEmail(email);

        checkPasswordEquals(password, member.getPassword());

        return member;
    }

    @Override
    @Transactional
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
    @Transactional
    public MemberDto resetPassword(Long id, String password, String newPassword) {

        Member member = findMemberById(id);

        checkPasswordEquals(password, member.getPassword());

        member.setPassword(passwordEncoder.encode(newPassword));
        member.setUpdateDt(LocalDateTime.now());

        return MemberDto.fromEntity(
            memberRepository.save(member)
        );
    }

    @Override
    @Transactional
    public void deleteMember(Long id, String password) {

        Member member = findMemberById(id);

        List<Car> carList = findCarByMember(member);

        checkPasswordEquals(password, member.getPassword());

        carRepository.deleteAll(carList);
        memberRepository.delete(member);
    }

    @Override
    @Transactional
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
    @Transactional
    public List<CarDto> getCars(Long id) {

        return CarDto.fromEntityList(findCarByMember(findMemberById(id)));

    }

    @Override
    @Transactional
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
    @Transactional
    public void deleteCar(Long id, String carNumber) {

        Member member = findMemberById(id);

        Car car = findCarByMemberAndCarNumber(member, carNumber);

        carRepository.delete(car);
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

    private void checkPasswordEquals(String password, String encPassword) {
        if (!passwordEncoder.matches(password, encPassword)) {
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
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(ErrorCode.MEMBER_ALREADY_EXIST);
        }
    }

}
