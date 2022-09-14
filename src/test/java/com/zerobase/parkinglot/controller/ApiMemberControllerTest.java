package com.zerobase.parkinglot.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.parkinglot.member.controller.ApiMemberController;
import com.zerobase.parkinglot.member.entity.Member;
import com.zerobase.parkinglot.member.exception.MemberException;
import com.zerobase.parkinglot.member.model.CarDelete;
import com.zerobase.parkinglot.member.model.CarDto;
import com.zerobase.parkinglot.member.model.CarRegister;
import com.zerobase.parkinglot.member.model.CarUpdate;
import com.zerobase.parkinglot.member.model.MemberDelete;
import com.zerobase.parkinglot.member.model.MemberDto;
import com.zerobase.parkinglot.member.model.MemberRegister;
import com.zerobase.parkinglot.member.model.MemberResetPassword;
import com.zerobase.parkinglot.member.model.MemberUpdate;
import com.zerobase.parkinglot.member.service.MemberService;
import com.zerobase.parkinglot.member.type.ErrorCode;
import com.zerobase.parkinglot.member.type.Role;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApiMemberController.class)
public class ApiMemberControllerTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void MemberRegisterTest_success() throws Exception{

        //given
        given(memberService.registerMember(
            anyString(), anyString(), anyString(), anyString())
        )
            .willReturn(MemberDto.builder()
                .email("rmsguqsla@gmail.com")
                .name("홍길동")
                .phone("010-1212-3434")
                .role(Role.USER.getDescription())
                .regDt(LocalDateTime.now())
                .build());

        //when


        //then
        mockMvc.perform(post("/api/member")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    new MemberRegister.Request(
                        "rmsguqsla@gmail.com",
                        "오근협",
                        "1234",
                        "010-1212-3434"
                    )
                )
            )
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("rmsguqsla@gmail.com"))
            .andDo(print());

    }

    @Test
    void MemberUpdateTest_success() throws Exception {

        //given
        given(memberService.updateMember(anyLong(), anyString(), anyString()))
            .willReturn(MemberDto.builder()
                .email("rmsguqsla@gmail.com")
                .name("홍길동")
                .phone("010-1212-3434")
                .role(Role.USER.getDescription())
                .regDt(LocalDateTime.now())
                .build());

        //when


        //then
        mockMvc.perform(put("/api/member/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MemberUpdate.Request(
                            "홍길동",
                            "010-1212-3434"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("홍길동"))
            .andExpect(jsonPath("$.phone").value("010-1212-3434"))
            .andDo(print());

    }

    @Test
    void MemberDeleteTest_success() throws Exception {

        //given

        //when


        //then
        mockMvc.perform(delete("/api/member/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MemberDelete.Request(
                            "1234"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("회원삭제 되었습니다."))
            .andDo(print());

    }

    @Test
    void MemberResetPasswordTest_success() throws Exception {

        //given
        given(memberService.resetPassword(anyLong(), anyString(), anyString()))
            .willReturn(MemberDto.builder()
                .email("rmsguqsla@gmail.com")
                .name("홍길동")
                .phone("010-1212-3434")
                .role(Role.USER.getDescription())
                .updateDt(LocalDateTime.now())
                .build());

        //when


        //then
        mockMvc.perform(put("/api/member/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MemberResetPassword.Request(
                            "1212",
                            "1234"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("비밀번호가 변경되었습니다."))
            .andDo(print());

    }

    @Test
    void CarRegisterTest_success() throws Exception{

        //given
        given(memberService.registerCar(anyLong(), anyString()))
            .willReturn(
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12가3456")
                    .regDt(LocalDateTime.now())
                    .build()
            );

        //when


        //then
        mockMvc.perform(post("/api/member/1/car")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    new CarRegister.Request(
                        "12가3456"
                    )
                )
            )
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.carNumber").value("12가3456"))
            .andDo(print());

    }

    @Test
    void GetCarsTest_success() throws Exception {

        //given
        List<CarDto> carDtoList =
            Arrays.asList(
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12가1212")
                    .regDt(LocalDateTime.now())
                    .build(),
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12가3434")
                    .regDt(LocalDateTime.now())
                    .build(),
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12가5656")
                    .regDt(LocalDateTime.now())
                    .build()
            );

        given(memberService.getCarList(anyLong()))
            .willReturn(carDtoList);

        //when


        //then
        mockMvc.perform(get("/api/member/1/car/list"))
            .andDo(print())
            .andExpect(jsonPath("$[0].carNumber").value("12가1212"))
            .andExpect(jsonPath("$[1].carNumber").value("12가3434"))
            .andExpect(jsonPath("$[2].carNumber").value("12가5656"));

    }

    @Test
    void GetCarsTest_fail_memberNotFound() throws Exception {

        //given
        given(memberService.getCarList(anyLong()))
            .willThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        //when


        //then
        mockMvc.perform(get("/api/member/1/car/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("회원이 존재하지 않습니다."))
            .andDo(print());
    }

    @Test
    void CarUpdateTest_success() throws Exception {

        //given
        given(memberService.updateCar(anyLong(), anyString(), anyString()))
            .willReturn(
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12가5678")
                    .regDt(LocalDateTime.now())
                    .build()
            );

        //when
        //then
        mockMvc.perform(put("/api/member/1/car")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new CarUpdate.Request(
                            "12가1234",
                            "12가5678"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.carNumber").value("12가5678"))
            .andDo(print());

    }

    @Test
    void CarDeleteTest_success() throws Exception {

        //given
        //when
        //then
        mockMvc.perform(delete("/api/member/1/car")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new CarDelete.Request(
                            "12가1234"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("차번호 삭제되었습니다."))
            .andDo(print());

    }
}
