package com.zerobase.parkinglot.controller;

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
import com.zerobase.parkinglot.auth.WithAuthUser;
import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.member.controller.ApiMemberController;
import com.zerobase.parkinglot.member.entity.Member;
import com.zerobase.parkinglot.member.exception.MemberException;
import com.zerobase.parkinglot.member.model.CarDelete;
import com.zerobase.parkinglot.member.model.CarDto;
import com.zerobase.parkinglot.member.model.CarRegister;
import com.zerobase.parkinglot.member.model.CarUpdate;
import com.zerobase.parkinglot.member.model.MemberDelete;
import com.zerobase.parkinglot.member.model.MemberDto;
import com.zerobase.parkinglot.member.model.MemberLogin;
import com.zerobase.parkinglot.member.model.MemberRegister;
import com.zerobase.parkinglot.member.model.MemberResetPassword;
import com.zerobase.parkinglot.member.model.MemberUpdate;
import com.zerobase.parkinglot.member.service.MemberService;
import com.zerobase.parkinglot.security.TokenProvider;
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

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void memberRegisterTest_success() throws Exception{

        //given
        given(memberService.registerMember(
            anyString(), anyString(), anyString(), anyString(), anyString())
        )
            .willReturn(MemberDto.builder()
                .email("rmsguqsla@gmail.com")
                .name("?????????")
                .phone("010-1212-3434")
                .role("ROLE_USER")
                .regDt(LocalDateTime.now())
                .build());

        //when


        //then
        mockMvc.perform(post("/api/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    new MemberRegister.Request(
                        "rmsguqsla@gmail.com",
                        "?????????",
                        "1234",
                        "010-1212-3434",
                        "ROLE_USER"
                    )
                )
            )
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("rmsguqsla@gmail.com"))
            .andDo(print());

    }

    @Test
    void loginTest_success() throws Exception{
        //given
        Member member = Member.builder()
            .id(1L)
            .email("hgd@gmail.com")
            .password("abcde")
            .phone("010-1234-1234")
            .name("?????????")
            .role("ROLE_USER")
            .build();
        given(memberService.authenticate(anyString(), anyString()))
            .willReturn(member);

        String token = "ababab.cdcdcd.efefef";
        given(tokenProvider.generateToken(anyString(), anyString()))
            .willReturn(token);
        //when
        //then
        mockMvc.perform(post("/api/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MemberLogin.Request(
                            "rmsguqsla@gmail.com",
                            "1234"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("ababab.cdcdcd.efefef"))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void MemberUpdateTest_success() throws Exception {

        //given
        given(memberService.updateMember(anyLong(), anyString(), anyString()))
            .willReturn(MemberDto.builder()
                .email("rmsguqsla@gmail.com")
                .name("?????????")
                .phone("010-1212-3434")
                .role("ROLE_USER")
                .regDt(LocalDateTime.now())
                .build());

        //when


        //then
        mockMvc.perform(put("/api/member/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MemberUpdate.Request(
                            "?????????",
                            "010-1212-3434"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("?????????"))
            .andExpect(jsonPath("$.phone").value("010-1212-3434"))
            .andDo(print());

    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
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
            .andExpect(jsonPath("$.message").value("???????????? ???????????????."))
            .andDo(print());

    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void MemberResetPasswordTest_success() throws Exception {

        //given
        given(memberService.resetPassword(anyLong(), anyString(), anyString()))
            .willReturn(MemberDto.builder()
                .email("rmsguqsla@gmail.com")
                .name("?????????")
                .phone("010-1212-3434")
                .role("ROLE_USER")
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
            .andExpect(jsonPath("$.message").value("??????????????? ?????????????????????."))
            .andDo(print());

    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void CarRegisterTest_success() throws Exception{

        //given
        given(memberService.registerCar(anyLong(), anyString()))
            .willReturn(
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12???3456")
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
                        "12???3456"
                    )
                )
            )
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.carNumber").value("12???3456"))
            .andDo(print());

    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void GetCarsTest_success() throws Exception {

        //given
        List<CarDto> carDtoList =
            Arrays.asList(
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12???1212")
                    .regDt(LocalDateTime.now())
                    .build(),
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12???3434")
                    .regDt(LocalDateTime.now())
                    .build(),
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12???5656")
                    .regDt(LocalDateTime.now())
                    .build()
            );

        given(memberService.getCars(anyLong()))
            .willReturn(carDtoList);

        //when


        //then
        mockMvc.perform(get("/api/member/1/cars"))
            .andDo(print())
            .andExpect(jsonPath("$[0].carNumber").value("12???1212"))
            .andExpect(jsonPath("$[1].carNumber").value("12???3434"))
            .andExpect(jsonPath("$[2].carNumber").value("12???5656"));

    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void GetCarsTest_fail_memberNotFound() throws Exception {

        //given
        given(memberService.getCars(anyLong()))
            .willThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        //when


        //then
        mockMvc.perform(get("/api/member/1/cars"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("????????? ???????????? ????????????."))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void CarUpdateTest_success() throws Exception {

        //given
        given(memberService.updateCar(anyLong(), anyString(), anyString()))
            .willReturn(
                CarDto.builder()
                    .member(new Member())
                    .carNumber("12???5678")
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
                            "12???1234",
                            "12???5678"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.carNumber").value("12???5678"))
            .andDo(print());

    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void CarDeleteTest_success() throws Exception {

        //given
        //when
        //then
        mockMvc.perform(delete("/api/member/1/car")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new CarDelete.Request(
                            "12???1234"
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("????????? ?????????????????????."))
            .andDo(print());

    }
}
