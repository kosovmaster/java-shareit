package ru.practicum.shareit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constant {
    public static final String HEADER_USER = "X-Sharer-User-Id";
    public static final String STATE_DEFAULT = "ALL";
    public static final String PAGE_FROM_DEFAULT = "0";
    public static final String PAGE_SIZE_DEFAULT = "10";
    public static final String NEXT = "next";
    public static final String LAST = "last";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTime FIXED_TIME = LocalDateTime.parse("2023-05-19T21:09:45", DATE_FORMAT);
}
