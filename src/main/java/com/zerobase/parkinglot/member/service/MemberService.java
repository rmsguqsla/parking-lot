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
    MemberDto registerMember(MemberRegister.Request request);

    // 차번호 등록
    CarDto registerCar(Long id, Request request);

    // 회원수정
    MemberDto updateMember(Long id, MemberUpdate.Request request);

    // 비밀번호 변경
    MemberDto resetPassword(Long id, MemberResetPassword.Request request);

    // 회원탈퇴
    void deleteMember(Long id, MemberDelete.Request request);

    // 차번호 삭제
    void deleteCar(Long id, CarDelete.Request request);

    // 차번호 수정
    CarDto updateCar(Long id, CarUpdate.Request request);

    // 로그인 토큰 생성
    String login(MemberLogin.Request request);

    List<CarDto> getCars(Long id);
}
