package com.zerobase.parkinglot.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.parkinglot.auth.WithAuthUser;
import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.member.service.MemberService;
import com.zerobase.parkinglot.reserve.controller.ApiAdminReserveController;
import com.zerobase.parkinglot.reserve.exception.ReserveException;
import com.zerobase.parkinglot.reserve.model.ReserveInfo;
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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApiAdminReserveController.class)
public class ApiAdminReserveControllerTest {

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
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_ADMIN")
    void getAdminReservesTest() throws Exception{
        //given
        List<ReserveInfo> list = Arrays.asList(
            ReserveInfo.builder()
                .id(1L)
                .name("?????????")
                .phone("010-1234-1234")
                .carNumber("12???3456")
                .parkingLot("???????????????")
                .address("??????????????? ?????????")
                .ticket("?????? ?????????")
                .fee(10000)
                .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
                .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
                .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
                .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
                .status(StatusType.Using)
                .build(),
            ReserveInfo.builder()
                .id(2L)
                .name("?????????")
                .phone("010-1234-1234")
                .carNumber("12???3456")
                .parkingLot("???????????????")
                .address("??????????????? ?????????")
                .ticket("?????? ?????????")
                .fee(10000)
                .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
                .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
                .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
                .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
                .status(StatusType.Using)
                .build()
        );

        given(reserveService.getAdminReserves())
            .willReturn(list);

        //when
        //then
        mockMvc.perform(get("/api/admin/reserves"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].parkingLot").value("???????????????"))
            .andExpect(jsonPath("$[1].parkingLot").value("???????????????"))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_ADMIN")
    void getAdminReserveTest_success() throws Exception{
        //given
        ReserveInfo reserveInfo = ReserveInfo.builder()
            .id(1L)
            .name("?????????")
            .phone("010-1234-1234")
            .carNumber("12???3456")
            .parkingLot("???????????????")
            .address("??????????????? ?????????")
            .ticket("?????? ?????????")
            .fee(10000)
            .minEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)))
            .maxEstimatedDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,30)))
            .reserveDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)))
            .reserveEndDt(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)))
            .status(StatusType.Using)
            .build();
        given(reserveService.getAdminReserve(anyLong()))
            .willReturn(reserveInfo);
        //when
        //then
        mockMvc.perform(get("/api/admin/reserve/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("?????????"))
            .andExpect(jsonPath("$.phone").value("010-1234-1234"))
            .andExpect(jsonPath("$.carNumber").value("12???3456"))
            .andExpect(jsonPath("$.parkingLot").value("???????????????"))
            .andExpect(jsonPath("$.address").value("??????????????? ?????????"))
            .andExpect(jsonPath("$.ticket").value("?????? ?????????"))
            .andExpect(jsonPath("$.fee").value(10000))
            .andExpect(jsonPath("$.minEstimatedDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.maxEstimatedDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 30)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.reserveDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,30,0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.reserveEndDt").value(LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.status").value(StatusType.Using.toString()))
            .andDo(print());
    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_ADMIN")
    void getAdminReserveTest_fail_reserveNotFound() throws Exception{
        //given
        given(reserveService.getAdminReserve(anyLong()))
            .willThrow(new ReserveException(ErrorCode.RESERVE_NOT_FOUND));
        //when
        //then
        mockMvc.perform(get("/api/admin/reserve/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("RESERVE_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("????????? ?????? ????????? ???????????? ????????????."))
            .andDo(print());
    }
}
