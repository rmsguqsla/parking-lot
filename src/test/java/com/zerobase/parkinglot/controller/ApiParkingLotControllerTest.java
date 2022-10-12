package com.zerobase.parkinglot.controller;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.parkinglot.auth.WithAuthUser;
import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.member.service.MemberService;
import com.zerobase.parkinglot.parkinglot.controller.ApiParkingLotController;
import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUserInfo;
import com.zerobase.parkinglot.parkinglot.model.TicketUserInfo;
import com.zerobase.parkinglot.parkinglot.service.ParkingLotService;
import com.zerobase.parkinglot.security.TokenProvider;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApiParkingLotController.class)
public class ApiParkingLotControllerTest {

    @MockBean
    private ParkingLotService parkingLotService;

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
    void getParkingLotsMyAroundTest_success() throws Exception{

        // given
        List<ParkingLotUserInfo> parkingLotUserInfoList = Arrays.asList(
            ParkingLotUserInfo.builder()
                .id(1L)
                .name("a주차장")
                .address("서울 어딘가")
                .spaceCount(100)
                .build(),
            ParkingLotUserInfo.builder()
                .id(2L)
                .name("b주차장")
                .address("서울 어딘가")
                .spaceCount(50)
                .build()
        );

        given(parkingLotService.getParkingLotsMyAround(anyDouble(), anyDouble()))
            .willReturn(parkingLotUserInfoList);

        // when


        // then
        mockMvc.perform(get("/api/parking-lots/around?myLat=" + anyDouble() + "&myLng=" + anyDouble()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("a주차장"))
            .andExpect(jsonPath("$[0].address").value("서울 어딘가"))
            .andExpect(jsonPath("$[0].spaceCount").value(100))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].name").value("b주차장"))
            .andExpect(jsonPath("$[1].address").value("서울 어딘가"))
            .andExpect(jsonPath("$[1].spaceCount").value(50))
            .andDo(print());

    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void getParkingLotsSearchTest_success() throws Exception{

        // given
        List<ParkingLotUserInfo> parkingLotUserInfoList = Arrays.asList(
            ParkingLotUserInfo.builder()
                .id(1L)
                .name("a주차장")
                .address("서울 어딘가")
                .spaceCount(100)
                .build(),
            ParkingLotUserInfo.builder()
                .id(2L)
                .name("b주차장")
                .address("서울 어딘가")
                .spaceCount(50)
                .build()
        );

        given(parkingLotService.getParkingLotsSearch(anyDouble(), anyDouble(), anyString(), anyString()))
            .willReturn(parkingLotUserInfoList);

        // when


        // then
        mockMvc.perform(get("/api/parking-lots/search?myLat=" + anyDouble() + "&myLng=" + anyDouble()
                + "&searchType=" + anyString() + "&searchValue=" + anyString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("a주차장"))
            .andExpect(jsonPath("$[0].address").value("서울 어딘가"))
            .andExpect(jsonPath("$[0].spaceCount").value(100))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].name").value("b주차장"))
            .andExpect(jsonPath("$[1].address").value("서울 어딘가"))
            .andExpect(jsonPath("$[1].spaceCount").value(50))
            .andDo(print());

    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void getParkingLotDetailTest_success() throws Exception{

        // given
        ParkingLotDto parkingLotDto = ParkingLotDto.builder()
            .id(1L)
            .name("서울주차장")
            .address("서울특별시 어딘가")
            .lat(36)
            .lng(120)
            .spaceCount(100)
            .useYn(true)
            .build();

        given(parkingLotService.getParkingLotWithUseYn(anyLong()))
            .willReturn(parkingLotDto);

        List<TicketUserInfo> ticketUserInfoList = Arrays.asList(
            TicketUserInfo.builder()
                .id(1L)
                .parkingLotId(parkingLotDto.getId())
                .name("평일 이용권")
                .fee(10000)
                .build()
        );

        given(parkingLotService.getUsableTickets(anyLong()))
            .willReturn(ticketUserInfoList);

        // when


        // then
        mockMvc.perform(get("/api/parking-lot/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("서울주차장"))
            .andExpect(jsonPath("$.address").value("서울특별시 어딘가"))
            .andExpect(jsonPath("$.spaceCount").value(100))
            .andExpect(jsonPath("$.remainCount").value(100))
            .andExpect(jsonPath("$.ticketInfoList[0].id").value(1L))
            .andExpect(jsonPath("$.ticketInfoList[0].parkingLotId").value(1L))
            .andExpect(jsonPath("$.ticketInfoList[0].name").value("평일 이용권"))
            .andExpect(jsonPath("$.ticketInfoList[0].fee").value(10000))
            .andDo(print());

    }

    @Test
    @WithAuthUser(email = "hgd@gmail.com", role = "ROLE_USER")
    void getParkingLotUserTest_fail_ParKingLotNotFound() throws Exception{

        // given
        given(parkingLotService.getParkingLotWithUseYn(anyLong()))
            .willThrow(new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));

        // when


        // then
        mockMvc.perform(get("/api/parking-lot/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("PARKING_LOT_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("주차장이 존재하지 않습니다."))
            .andDo(print());

    }

}
