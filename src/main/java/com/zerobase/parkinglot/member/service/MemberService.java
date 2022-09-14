package com.zerobase.parkinglot.member.service;

import com.zerobase.parkinglot.member.model.CarDelete;
import com.zerobase.parkinglot.member.model.CarDto;
import com.zerobase.parkinglot.member.model.CarRegister.Request;
import com.zerobase.parkinglot.member.model.CarUpdate;
import com.zerobase.parkinglot.member.model.MemberDelete;
import com.zerobase.parkinglot.member.model.MemberDto;
import com.zerobase.parkinglot.member.model.MemberLogin;
import com.zerobase.parkinglot.member.model.MemberRegister;
import com.zerobase.parkinglot.member.model.MemberResetPassword;
import com.zerobase.parkinglot.member.model.MemberUpdate;
import java.util.List;

public interface MemberService {

    // 회원가입
    MemberDto registerMember(String email, String name, String password, String phone);

    // 차번호 등록
    CarDto registerCar(Long id, String carNumber);

    // 회원수정
    MemberDto updateMember(Long id, String name, String phone);

    // 비밀번호 변경
    MemberDto resetPassword(Long id, String password, String newPassword);

    // 회원탈퇴
    void deleteMember(Long id, String password);

    // 차번호 삭제
    void deleteCar(Long id, String carNumber);

    // 차번호 수정
    CarDto updateCar(Long id, String carNumber, String newCarNumber);

    // 로그인 토큰 생성
    String login(String email, String password);

    List<CarDto> getCarList(Long id);
}
