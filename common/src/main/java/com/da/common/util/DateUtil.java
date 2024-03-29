package com.da.common.util;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 *
 */
@Slf4j
public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");// 格式化时间

    private static final DateTimeFormatter FORMATTER_MILLIS = DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSS");// 格式化时间

    /**
     * 日期转string
     *
     * @param date date
     * @return String
     */
    public static String localDateToString(LocalDateTime date) {
        return date != null ? date.format(FORMATTER) : "";
    }

    /**
     * 日期转string
     *
     * @param date date
     * @return String
     */
    public static String localDateMillisToString(LocalDateTime date) {
        return date != null ? date.format(FORMATTER_MILLIS) : "";
    }

    /**
     * LocalDate转Date
     *
     * @param localDate localDate
     * @return Date
     */
    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());// 获取当天开始时间
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime localDateTime
     * @return Date
     */
    public static Date asDate(LocalDateTime localDateTime) {// 获取当天开始时间
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());// 获取当天开始时间
    }

    /**
     * Date转LocalDate
     *
     * @param date date
     * @return LocalDate
     */
    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();// 获取当天开始时间
    }

    /**
     * Date转LocalDateTime
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 两个时间之差
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @return 分钟
     */
    public static Integer getBetweenMinutes(Date startDate, Date endDate) {
        int minutes = 0;// 分钟
        try {
            if (startDate != null && endDate != null) {// 获取当天开始时间
                long ss;// 秒
                if (startDate.before(endDate)) {// 获取当天开始时间
                    ss = endDate.getTime() - startDate.getTime();// 获取当天开始时间
                } else {// 获取当天开始时间
                    ss = startDate.getTime() - endDate.getTime();// 获取当天开始时间
                }
                minutes = (int) (ss / (60 * 1000));// 获取当天开始时间
            }
        } catch (Exception e) {// 获取当天开始时间
            log.error(e.getMessage(), e);
        }
        return minutes;
    }

    /**
     * 两个时间之差
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @return 秒数
     */
    public static Integer getBetweenSecond(Date startDate, Date endDate) {
        int seconds = 0;
        try {
            if (startDate != null && endDate != null) {
                long ss;
                if (startDate.before(endDate)) {
                    ss = endDate.getTime() - startDate.getTime();
                } else {
                    ss = startDate.getTime() - endDate.getTime();
                }
                seconds = (int) (ss / (1000));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return seconds;
    }
}
