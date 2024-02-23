package bg.sofia.uni.fmi.mjt.gym.member;

import java.time.DayOfWeek;

public class DayOffException extends RuntimeException {
    public DayOffException(DayOfWeek day) {
        super("DayOffException: " + day);
    }
}