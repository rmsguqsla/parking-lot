package com.zerobase.parkinglot.member.controller;

import com.zerobase.parkinglot.member.entity.Member;
import com.zerobase.parkinglot.member.model.CarDelete;
import com.zerobase.parkinglot.member.model.CarInfo;
import com.zerobase.parkinglot.member.model.CarRegister;
import com.zerobase.parkinglot.member.model.CarUpdate;
import com.zerobase.parkinglot.member.model.MemberDelete;
import com.zerobase.parkinglot.member.model.MemberLogin;
import com.zerobase.parkinglot.member.model.MemberRegister;
import com.zerobase.parkinglot.member.model.MemberResetPassword;
import com.zerobase.parkinglot.member.model.MemberUpdate;
import com.zerobase.parkinglot.member.service.MemberService;
import com.zerobase.parkinglot.security.TokenProvider;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiMemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    // 회원가입
    @ApiOperation(value = "회원가입 API")
    @PostMapping("/api/signup")
    public MemberRegister.Response memberRegister(
        @RequestBody @Valid MemberRegister.Request request) {

        return MemberRegister.Response.from(
            memberService.registerMember(
                request.getEmail(),
                request.getName(),
                request.getPassword(),
                request.getPhone(),
                request.getRole()
            )
        );

    }

    // 로그인
    @ApiOperation(value = "로그인 API")
    @PostMapping("/api/signin")
    public MemberLogin.Response login(
        @RequestBody @Valid MemberLogin.Request request) {

        Member member = memberService.authenticate(request.getEmail(), request.getPassword());
        String token = tokenProvider.generateToken(member.getEmail(), member.getRole());

        return MemberLogin.Response.token(token);

    }

    // 회원수정
    @ApiOperation(value = "회원수정 API")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/api/member/{id}")
    public MemberUpdate.Response memberUpdate(
        @PathVariable Long id,
        @RequestBody @Valid MemberUpdate.Request request) {

        return MemberUpdate.Response.from(
            memberService.updateMember(
                id, request.getName(), request.getPhone())
        );

    }

    // 비밀번호 변경
    @ApiOperation(value = "비밀번호 변경 API")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/api/member/{id}/password")
    public MemberResetPassword.Response memberResetPassword(
        @PathVariable Long id,
        @RequestBody @Valid MemberResetPassword.Request request) {

        return MemberResetPassword.Response.from(
            memberService.resetPassword(
                id, request.getPassword(), request.getNewPassword()
            )
        );

    }

    // 회원탈퇴
    @ApiOperation(value = "회원탈퇴 API")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/api/member/{id}")
    public MemberDelete.Response memberDelete(
        @PathVariable Long id,
        @RequestBody @Valid MemberDelete.Request request) {

        memberService.deleteMember(id, request.getPassword());

        return MemberDelete.Response.delete();
    }

    // 차번호 등록
    @ApiOperation(value = "차 번호 등록 API")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/api/member/{id}/car")
    public CarRegister.Response carRegister(
        @PathVariable Long id,
        @RequestBody @Valid CarRegister.Request request) {

        return CarRegister.Response.from(
            memberService.registerCar(id, request.getCarNumber())
        );

    }

    // 등록된 차번호 목록
    @ApiOperation(value = "차 번호 목록 API")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/member/{id}/cars")
    public List<CarInfo> getCars(
        @PathVariable Long id) {

        return CarInfo.listFrom(memberService.getCars(id));

    }

    // 차번호 수정
    @ApiOperation(value = "차 번호 수정 API")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/api/member/{id}/car")
    public CarUpdate.Response carUpdate(
        @PathVariable Long id,
        @RequestBody @Valid CarUpdate.Request request) {

        return CarUpdate.Response.from(
            memberService.updateCar(id, request.getCarNumber(),
                request.getNewCarNumber())
        );

    }

    // 차번호 삭제
    @ApiOperation(value = "차 번호 삭제 API")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/api/member/{id}/car")
    public CarDelete.Response carDelete(
        @PathVariable Long id,
        @RequestBody @Valid CarDelete.Request request) {

        memberService.deleteCar(id, request.getCarNumber());

        return CarDelete.Response.delete();

    }

}
