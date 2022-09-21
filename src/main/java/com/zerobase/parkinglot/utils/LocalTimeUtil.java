package com.zerobase.parkinglot.utils;

import java.time.LocalTime;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

@Component
public class LocalTimeUtil {

    public LocalTime convertLocalTime(int hour, int minute, int second) {
        return LocalTime.of(hour, minute, second, 0);
    }
}
