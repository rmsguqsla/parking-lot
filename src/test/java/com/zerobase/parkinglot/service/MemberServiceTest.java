package com.zerobase.parkinglot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.parkinglot.member.entity.Car;
import com.zerobase.parkinglot.member.entity.Member;
import com.zerobase.parkinglot.member.exception.MemberException;
import com.zerobase.parkinglot.member.model.CarDto;
import com.zerobase.parkinglot.member.model.MemberDto;
import com.zerobase.parkinglot.member.repository.CarRepository;
import com.zerobase.parkinglot.member.repository.MemberRepository;
import com.zerobase.parkinglot.member.service.MemberServiceImpl;
import com.zerobase.parkinglot.error.ErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private MemberServiceImpl memberServiceImpl;

    @Test
    void registerMemberTest_success() {

        //given

        given(memberRepository.existsByEmail(anyString()))
            .willReturn(false);

        when(passwordEncoder.encode("1234")).thenReturn("abcde");

        given(memberRepository.save(any()))
            .willReturn(
                Member.builder()
                    .email("test@gmail.com")
                    .name("홍길동")
                    .password("abcde")
                    .phone("010-1234-5678")
                    .role("ROLE_USER")
                    .build()
            );

        //when
        MemberDto memberDto = memberServiceImpl.registerMember ("test@gmail.com",
                "홍길동", "1234", "010-1234-5678", "ROLE_USER");

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        //then
        verify(memberRepository, times(1)).save(captor.capture());
        assertEquals("test@gmail.com", memberDto.getEmail());
        assertEquals("홍길동", memberDto.getName());
        assertEquals("010-1234-5678", memberDto.getPhone());
        assertEquals("ROLE_USER", memberDto.getRole());
    }

    @Test
    void registerMemberTest_fail_MemberAlreadyExist() {

        //given
        given(memberRepository.existsByEmail(anyString()))
            .willReturn(true);

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberServiceImpl.registerMember ("test@gmail.com",
                "홍길동", "1234", "010-1234-5678", "ROLE_USER"));

        //then
        assertEquals(ErrorCode.MEMBER_ALREADY_EXIST, exception.getErrorCode());

    }

    @Test
    void updateMemberTest_success() {

        //given
        Member member = Member.builder()
            .id(1L)
            .build();

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(memberRepository.save(any()))
            .willReturn(
                Member.builder()
                    .id(1L)
                    .name("홍길동2")
                    .phone("010-1111-2222")
                    .build());

        //when
        MemberDto memberDto = memberServiceImpl.updateMember(1L, "홍길동2", "010-1111-2222");

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        //then
        verify(memberRepository, times(1)).save(captor.capture());
        assertEquals("홍길동2", memberDto.getName());
        assertEquals("010-1111-2222", memberDto.getPhone());

    }



    @Test
    void updateMemberTest_fail_MemberNotFound() {

        //given
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class, () -> memberServiceImpl.updateMember(
            1L, "홍길동2", "010-1111-2222"));

        //then
        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void deleteMember_fail_MemberNotFound() {

        //given
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberServiceImpl.deleteMember(1L,"1234"));

        //then
        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void registerCar_success() {

        //given
        Member member = Member.builder()
            .id(1L)
            .build();

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(carRepository.countByCarNumber(anyString()))
            .willReturn(0);

        given(carRepository.save(any()))
            .willReturn(
                Car.builder()
                    .member(member)
                    .carNumber("12가1234")
                    .build()
            );

        //when
        CarDto carDto = memberServiceImpl.registerCar(1L, "12가1234");

        ArgumentCaptor<Car> captor = ArgumentCaptor.forClass(Car.class);

        //then
        verify(carRepository, times(1)).save(captor.capture());
        assertEquals(1L, carDto.getMember().getId());
        assertEquals("12가1234", carDto.getCarNumber());

    }

    @Test
    void registerCar_fail_MemberNotFound() {

        //given
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class, () -> memberServiceImpl.registerCar(
            1L, "12가1234"));

        //then
        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void registerCar_fail_CarAlready_Exist() {

        //given
        Member member = Member.builder()
            .id(1L)
            .build();

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(carRepository.countByCarNumber(anyString()))
            .willReturn(1);

        //when
        MemberException exception = assertThrows(MemberException.class, () -> memberServiceImpl.registerCar(
            1L, "12가1234"));

        //then
        assertEquals(ErrorCode.CAR_ALREADY_EXIST, exception.getErrorCode());

    }

    @Test
    void getCars_success() {

        //given
        Member member = Member.builder()
            .id(1L)
            .build();

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        List<Car> carList = Arrays.asList(
            Car.builder()
                .member(member)
                .carNumber("12가1234")
                .build(),
            Car.builder()
                .member(member)
                .carNumber("12나1234")
                .build(),
            Car.builder()
                .member(member)
                .carNumber("12다1234")
                .build()

        );

        given(carRepository.findByMember(member))
            .willReturn(carList);

        //when
        List<CarDto> carDtoList = memberServiceImpl.getCars(1L);

        //then
        assertEquals(3, carDtoList.size());

        assertEquals(1L, carDtoList.get(0).getMember().getId());
        assertEquals("12가1234", carDtoList.get(0).getCarNumber());

        assertEquals(1L, carDtoList.get(1).getMember().getId());
        assertEquals("12나1234", carDtoList.get(1).getCarNumber());

        assertEquals(1L, carDtoList.get(2).getMember().getId());
        assertEquals("12다1234", carDtoList.get(2).getCarNumber());

    }

    @Test
    void getCarList_fail_MemberNotFound() {

        //given
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberServiceImpl.getCars(anyLong()));

        //then
        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void updateCar_success() {

        //given
        Member member = Member.builder()
            .id(1L)
            .build();

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(carRepository.countByCarNumber(anyString()))
            .willReturn(0);


        Car car = Car.builder()
            .member(member)
            .carNumber("12가1234")
            .build();

        given(carRepository.findByMemberAndCarNumber(any(), anyString()))
            .willReturn(Optional.of(car));

        given(carRepository.save(car))
            .willReturn(
                Car.builder()
                    .member(member)
                    .carNumber("12가5678")
                    .build()
            );

        //when
        CarDto carDto = memberServiceImpl.updateCar(1L,
            "12가1234", "12가5678");

        ArgumentCaptor<Car> captor = ArgumentCaptor.forClass(Car.class);

        //then
        verify(carRepository, times(1)).save(captor.capture());
        assertEquals(1L, carDto.getMember().getId());
        assertEquals("12가5678", carDto.getCarNumber());

    }

    @Test
    void updateCar_fail_MemberNotFound() {

        //given
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberServiceImpl.updateCar(1L,
                "12가1234", "12가5678"));

        //then
        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void updateCar_fail_CarAlreadyExist() {

        //given
        Member member = Member.builder()
            .id(1L)
            .build();

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(carRepository.countByCarNumber(anyString()))
            .willReturn(1);

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberServiceImpl.updateCar(1L,
                "12가1234", "12가5678"));

        //then
        assertEquals(ErrorCode.CAR_ALREADY_EXIST, exception.getErrorCode());

    }

    @Test
    void updateCar_fail_MemberCarNumberNotMatch() {

        //given
        Member member = Member.builder()
            .id(1L)
            .build();

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(carRepository.countByCarNumber(anyString()))
            .willReturn(0);

        given(carRepository.findByMemberAndCarNumber(any(), anyString()))
            .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberServiceImpl.updateCar(1L,
                "12가1234", "12가5678"));

        //then
        assertEquals(ErrorCode.MEMBER_CAR_NUMBER_NOT_MATCH, exception.getErrorCode());

    }

    @Test
    void deleteCar_fail_MemberNotFound() {

        //given
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberServiceImpl.deleteCar(1L, "12가1234"));

        //then
        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    void deleteCar_fail_MemberCarNumberNotMatch() {

        //given
        Member member = Member.builder()
            .id(1L)
            .build();

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        given(carRepository.findByMemberAndCarNumber(any(), anyString()))
            .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberServiceImpl.deleteCar(1L, "12가1234"));

        //then
        assertEquals(ErrorCode.MEMBER_CAR_NUMBER_NOT_MATCH, exception.getErrorCode());

    }
}
