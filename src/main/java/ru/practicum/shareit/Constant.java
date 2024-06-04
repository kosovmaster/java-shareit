package ru.practicum.shareit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constant {
    public static final String NEXT = "next";
    public static final String LAST = "last";
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTime FIXED_TIME = LocalDateTime.parse("2023-05-19T21:09:45", DATE_TIME_FORMAT);
    public static final String USER_HEADER = "X-Sharer-User-Id";
}
