package com.zerobase.parkinglot.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.parkinglot.auth.WithAuthUser;
import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.member.exception.MemberException;
import com.zerobase.parkinglot.member.service.MemberService;
import com.zerobase.parkinglot.reserve.controller.ApiReserveController;
import com.zerobase.parkinglot.reserve.exception.ReserveException;
import com.zerobase.parkinglot.reserve.model.ReserveCancel;
import com.zerobase.parkinglot.reserve.model.ReserveDto;
import com.zerobase.parkinglot.reserve.model.ReserveInfo;
import com.zerobase.parkinglot.reserve.model.ReserveRegister;
import com.zerobase.parkinglot.reserve.service.ReserveService;
import com.zerobase.parkinglot.reserve.type.StatusType;
import com.zerobase.parkinglot.security.TokenProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApiReserveController.class)
public class ApiReserveControllerTest {

    @MockBean
    private ReserveService reserveService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void reserveRegisterTest() throws Exception{

        // given
        ReserveDto reserveDto = ReserveDto.builder()
            .id(1L)
            .name("홍길동")
            .phone("010-1234-1234")
            .carNumber("12가3456")
            .parkingLot("서울주차장")
            .address("서울특별시 어딘가")
            .ticket("평일 이용권")
            .fee(10000)
            .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
            .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
            .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
            .status(StatusType.Using)
            .build();

        given(reserveService.reserveRegister(anyLong(), anyLong(), anyLong(), anyLong(), anyInt(), anyInt()))
            .willReturn(reserveDto);

        // when

        // then

        mockMvc.perform(post("/api/member/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new ReserveRegister.Request(
                            1L, 1L, 1L,
                            18, 0
                        )
                    )
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("홍길동"))
            .andExpect(jsonPath("$.parkingLot").value("서울주차장"))
            .andExpect(jsonPath("$.address").value("서울특별시 어딘가"))
            .andExpect(jsonPath("$.ticket").value("평일 이용권"))
            .andExpect(jsonPath("$.fee").value(10000))
            .andExpect(jsonPath("$.minEstimatedDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.maxEstimatedDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 30)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.reserveDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.reserveEndDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void reserveCancelTest() throws Exception {
        //given
        ReserveDto reserveDto = ReserveDto.builder()
            .id(1L)
            .name("홍길동")
            .phone("010-1234-1234")
            .carNumber("12가3456")
            .parkingLot("서울주차장")
            .address("서울특별시 어딘가")
            .ticket("평일 이용권")
            .fee(10000)
            .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
            .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
            .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
            .status(StatusType.Cancel)
            .cancelDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,31,0)))
            .build();

        given(reserveService.reserveCancel(anyLong(), anyLong()))
            .willReturn(reserveDto);
        //when
        //then
        mockMvc.perform(patch("/api/member/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new ReserveCancel.Request(
                            1L, 1L
                        )
                    )
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.parkingLot").value("서울주차장"))
            .andExpect(jsonPath("$.address").value("서울특별시 어딘가"))
            .andExpect(jsonPath("$.ticket").value("평일 이용권"))
            .andExpect(jsonPath("$.status").value(StatusType.Cancel.toString()))
            .andExpect(jsonPath("$.cancelDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,31,0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void getReservesTest() throws Exception{
        //given
        List<ReserveInfo> list = Arrays.asList(
            ReserveInfo.builder()
                .id(1L)
                .name("홍길동")
                .phone("010-1234-1234")
                .carNumber("12가3456")
                .parkingLot("서울주차장")
                .address("서울특별시 어딘가")
                .ticket("평일 이용권")
                .fee(10000)
                .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
                .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
                .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
                .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
                .status(StatusType.Cancel)
                .cancelDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,31,0)))
                .build(),
            ReserveInfo.builder()
                .id(2L)
                .name("홍길동")
                .phone("010-1234-1234")
                .carNumber("12가3456")
                .parkingLot("인천주차장")
                .address("인천광역시 어딘가")
                .ticket("평일 이용권")
                .fee(10000)
                .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
                .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
                .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
                .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
                .status(StatusType.Cancel)
                .cancelDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,31,0)))
                .build()
        );

        given(reserveService.getReserves(anyLong()))
            .willReturn(list);
        //when
        //then
        mockMvc.perform(get("/api/member/1/reserves"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].parkingLot").value("서울주차장"))
            .andExpect(jsonPath("$[1].parkingLot").value("인천주차장"))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void getReservesTest_fail_memberNotFound() throws Exception{
        //given
        given(reserveService.getReserves(anyLong()))
            .willThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        //when
        //then
        mockMvc.perform(get("/api/member/1/reserves"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("회원이 존재하지 않습니다."))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void getReserveTest_success() throws Exception{
        //given
        ReserveInfo reserveInfo = ReserveInfo.builder()
            .id(1L)
            .name("홍길동")
            .phone("010-1234-1234")
            .carNumber("12가3456")
            .parkingLot("서울주차장")
            .address("서울특별시 어딘가")
            .ticket("평일 이용권")
            .fee(10000)
            .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
            .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
            .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
            .status(StatusType.Using)
            .build();
        given(reserveService.getReserve(anyLong(), anyLong()))
            .willReturn(reserveInfo);
        //when
        //then
        mockMvc.perform(get("/api/member/1/reserve/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("홍길동"))
            .andExpect(jsonPath("$.phone").value("010-1234-1234"))
            .andExpect(jsonPath("$.carNumber").value("12가3456"))
            .andExpect(jsonPath("$.parkingLot").value("서울주차장"))
            .andExpect(jsonPath("$.address").value("서울특별시 어딘가"))
            .andExpect(jsonPath("$.ticket").value("평일 이용권"))
            .andExpect(jsonPath("$.fee").value(10000))
            .andExpect(jsonPath("$.minEstimatedDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.maxEstimatedDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 30)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.reserveDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.reserveEndDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.status").value(StatusType.Using.toString()))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void getReserveTest_fail_memberNotFound() throws Exception{
        //given
        given(reserveService.getReserve(anyLong(), anyLong()))
            .willThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        //when
        //then
        mockMvc.perform(get("/api/member/1/reserve/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("회원이 존재하지 않습니다."))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void getReserveTest_fail_reserveNotFound() throws Exception{
        //given
        given(reserveService.getReserve(anyLong(), anyLong()))
            .willThrow(new ReserveException(ErrorCode.RESERVE_NOT_FOUND));
        //when
        //then
        mockMvc.perform(get("/api/member/1/reserve/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("RESERVE_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("주차장 예약 내역이 존재하지 않습니다."))
            .andDo(print());
    }
}
