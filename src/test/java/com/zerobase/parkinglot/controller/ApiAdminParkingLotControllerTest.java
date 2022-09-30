package com.zerobase.parkinglot.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.parkinglot.controller.ApiAdminParkingLotController;
import com.zerobase.parkinglot.parkinglot.entity.ParkingLot;
import com.zerobase.parkinglot.parkinglot.entity.Ticket;
import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotDto;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotRegister;
import com.zerobase.parkinglot.parkinglot.model.ParkingLotUpdate;
import com.zerobase.parkinglot.parkinglot.model.TicketDto;
import com.zerobase.parkinglot.parkinglot.model.TicketRegister;
import com.zerobase.parkinglot.parkinglot.model.TicketUpdate;
import com.zerobase.parkinglot.parkinglot.service.ParkingLotService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApiAdminParkingLotController.class)
public class ApiAdminParkingLotControllerTest {

    @MockBean
    private ParkingLotService parkingLotService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void parkingLotRegisterTest_success() throws Exception{

        // given
        given(parkingLotService.parkingLotRegister(
            anyString(), anyString(), anyInt()))
            .willReturn(ParkingLotDto.builder()
                .name("서울주차장")
                .address("서울특별시 어딘가")
                .lat(36)
                .lng(120)
                .spaceCount(100)
                .useYn(true)
                .build());

        // when


        // then
        mockMvc.perform(post("/api/admin/parking-lot")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    new ParkingLotRegister.Request(
                        "서울주차장",
                        "서울특별시 어딘가",
                        100
                    )
                )
            )
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("서울주차장"))
            .andExpect(jsonPath("$.address").value("서울특별시 어딘가"))
            .andExpect(jsonPath("$.lat").value(36))
            .andExpect(jsonPath("$.lng").value(120))
            .andExpect(jsonPath("$.spaceCount").value(100))
            .andExpect(jsonPath("$.useYn").value(true))
            .andDo(print());

    }

    @Test
    void getParkingLotsTest_success() throws Exception {

        // given
        List<ParkingLotDto> parkingLotDtoList = Arrays.asList(
            ParkingLotDto.builder()
                .name("서울주차장")
                .address("서울특별시 어딘가")
                .lat(36)
                .lng(120)
                .spaceCount(100)
                .useYn(true)
                .build(),
            ParkingLotDto.builder()
                .name("인천주차장")
                .address("인천광역시 어딘가")
                .lat(35)
                .lng(115)
                .spaceCount(50)
                .useYn(true)
                .build(),
            ParkingLotDto.builder()
                .name("경기주차장")
                .address("경기도 어딘가")
                .lat(34)
                .lng(114)
                .spaceCount(30)
                .useYn(true)
                .build()
        );

        given(parkingLotService.getParkingLots())
            .willReturn(parkingLotDtoList);

        // when

        // then
        mockMvc.perform(get("/api/admin/parking-lots"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("서울주차장"))
            .andExpect(jsonPath("$[1].name").value("인천주차장"))
            .andExpect(jsonPath("$[2].name").value("경기주차장"))
            .andDo(print());

    }

    @Test
    void getParkingLotTest_success() throws Exception{

        // given
        given(parkingLotService.getParkingLot(anyLong()))
            .willReturn(
                ParkingLotDto.builder()
                    .id(1L)
                    .name("서울주차장")
                    .address("서울특별시 어딘가")
                    .lat(36)
                    .lng(120)
                    .spaceCount(100)
                    .useYn(true)
                    .build()
            );
        // when

        // then
        mockMvc.perform(get("/api/admin/parking-lot/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("서울주차장"))
            .andExpect(jsonPath("$.address").value("서울특별시 어딘가"))
            .andExpect(jsonPath("$.lat").value(36))
            .andExpect(jsonPath("$.lng").value(120))
            .andExpect(jsonPath("$.spaceCount").value(100))
            .andExpect(jsonPath("$.useYn").value(true))
            .andDo(print());

    }

    @Test
    void getParkingLotTest_fail_ParkingLotNotFound() throws Exception{

        // given
        given(parkingLotService.getParkingLot(anyLong()))
            .willThrow(new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));
        // when

        // then
        mockMvc.perform(get("/api/admin/parking-lot/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("PARKING_LOT_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("주차장이 존재하지 않습니다."))
            .andDo(print());

    }

    @Test
    void parkingLotUpdateTest_success() throws Exception {

        // given
        given(parkingLotService.parkingLotUpdate(anyLong(), anyString(), anyString(), anyInt(), anyBoolean()))
            .willReturn(
                ParkingLotDto.builder()
                    .name("서울주차장")
                    .address("서울특별시 어딘가")
                    .lat(36)
                    .lng(120)
                    .spaceCount(100)
                    .useYn(true)
                    .build()
            );
        // when

        // then
        mockMvc.perform(put("/api/admin/parking-lot/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new ParkingLotUpdate.Request(
                            "서울주차장",
                            "서울특별시 어딘가",
                            100,
                            true
                        )
                    )
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("서울주차장"))
            .andExpect(jsonPath("$.address").value("서울특별시 어딘가"))
            .andExpect(jsonPath("$.lat").value(36))
            .andExpect(jsonPath("$.lng").value(120))
            .andExpect(jsonPath("$.spaceCount").value(100))
            .andExpect(jsonPath("$.useYn").value(true))
            .andDo(print());

    }

    @Test
    void ticketRegister_success() throws Exception{

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .build();

        given(parkingLotService.ticketRegister(anyLong(), anyString(), anyInt(), any(), any(), any(), anyBoolean()))
            .willReturn(
                TicketDto.builder()
                    .id(1L)
                    .parkingLot(parkingLot)
                    .name("평일 이용권")
                    .fee(10000)
                    .holidayYn(false)
                    .useYn(true)
                    .build()
            );

        // when

        // then
        mockMvc.perform(post("/api/admin/parking-lot/1/ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new TicketRegister.Request(
                            "평일 이용권",
                            10000,
                            0, 0, 0,
                            23, 59, 59,
                            23, 59, 59,
                            false
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.parkingLotName").value("서울주차장"))
            .andExpect(jsonPath("$.name").value("평일 이용권"))
            .andExpect(jsonPath("$.fee").value(10000))
            .andDo(print());

    }

    @Test
    void getTickets_success() throws Exception{

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .build();

        List<Ticket> ticketList = Arrays.asList(
            Ticket.builder()
                .id(1L)
                .parkingLot(parkingLot)
                .name("평일 이용권")
                .fee(10000)
                .holidayYn(false)
                .useYn(true)
                .build(),
            Ticket.builder()
                .id(2L)
                .parkingLot(parkingLot)
                .name("주말 이용권")
                .fee(10000)
                .holidayYn(true)
                .useYn(true)
                .build()
        );

        given(parkingLotService.getTickets(anyLong()))
            .willReturn(TicketDto.fromEntityList(ticketList));

        // when


        // then
        mockMvc.perform(get("/api/admin/parking-lot/1/tickets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("평일 이용권"))
            .andExpect(jsonPath("$[1].name").value("주말 이용권"))
            .andDo(print());

    }

    @Test
    void getTickets_fail_ParkingLotNotFound() throws Exception{

        // given
        given(parkingLotService.getTickets(anyLong()))
            .willThrow(new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));

        // when


        // then
        mockMvc.perform(get("/api/admin/parking-lot/1/tickets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("PARKING_LOT_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("주차장이 존재하지 않습니다."))
            .andDo(print());

    }

    @Test
    void getTicket_success() throws Exception{

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .build();

        Ticket ticket = Ticket.builder()
            .id(1L)
            .parkingLot(parkingLot)
            .name("평일 이용권")
            .fee(10000)
            .holidayYn(false)
            .useYn(true)
            .build();

        given(parkingLotService.getTicket(anyLong(), anyLong()))
            .willReturn(TicketDto.fromEntity(ticket));

        // when


        // then
        mockMvc.perform(get("/api/admin/parking-lot/1/ticket/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("평일 이용권"))
            .andExpect(jsonPath("$.parkingLotId").value(1L))
            .andExpect(jsonPath("$.fee").value(10000))
            .andExpect(jsonPath(".holidayYn").value(false))
            .andExpect(jsonPath(".useYn").value(true))
            .andDo(print());

    }

    @Test
    void getTicket_fail_ParkingLotNotFound() throws Exception{

        // given
        given(parkingLotService.getTicket(anyLong(), anyLong()))
            .willThrow(new ParkingLotException(ErrorCode.PARKING_LOT_NOT_FOUND));

        // when


        // then
        mockMvc.perform(get("/api/admin/parking-lot/1/ticket/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("PARKING_LOT_NOT_FOUND"))
            .andExpect(jsonPath("$.errorMessage").value("주차장이 존재하지 않습니다."))
            .andDo(print());

    }

    @Test
    void getTicket_fail_ParkingLotTicketNotMatch() throws Exception{

        // given
        given(parkingLotService.getTicket(anyLong(), anyLong()))
            .willThrow(new ParkingLotException(ErrorCode.PARKING_LOT_TICKET_NOT_MATCH));

        // when


        // then
        mockMvc.perform(get("/api/admin/parking-lot/1/ticket/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errorCode").value("PARKING_LOT_TICKET_NOT_MATCH"))
            .andExpect(jsonPath("$.errorMessage").value("주차장에 존재하지 않는 이용권입니다."))
            .andDo(print());

    }

    @Test
    void ticketUpdate_success() throws Exception{

        // given
        ParkingLot parkingLot = ParkingLot.builder()
            .id(1L)
            .name("서울주차장")
            .build();

        given(parkingLotService.ticketUpdate(anyLong(), anyLong(), anyString(), anyInt(), any(), any(), any(), anyBoolean(), anyBoolean()))
            .willReturn(
                TicketDto.builder()
                    .id(1L)
                    .parkingLot(parkingLot)
                    .name("평일 이용권")
                    .fee(10000)
                    .holidayYn(false)
                    .useYn(true)
                    .build()
            );

        // when

        // then
        mockMvc.perform(put("/api/admin/parking-lot/1/ticket/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new TicketUpdate.Request(
                            "평일 이용권",
                            10000,
                            0, 0, 0,
                            23, 59, 59,
                            23, 59, 59,
                            false,
                            true
                        )
                    )
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.parkingLotName").value("서울주차장"))
            .andExpect(jsonPath("$.name").value("평일 이용권"))
            .andExpect(jsonPath("$.fee").value(10000))
            .andExpect(jsonPath(".holidayYn").value(false))
            .andExpect(jsonPath(".useYn").value(true))
            .andDo(print());

    }

}
