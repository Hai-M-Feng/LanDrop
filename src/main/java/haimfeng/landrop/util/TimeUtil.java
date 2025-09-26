package haimfeng.landrop.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeUtil {
    /**
     * 私有构造方法，防止被实例化
     */
    private TimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取当前时间
     * @return 当前时间
     */
    public static LocalDateTime getCurrentTime() {
        return java.time.LocalDateTime.now();
    }

    /**
     * 计算两个时间之间的秒数
     * @param start 开始时间
     * @param end 结束时间
     * @return 秒数
     */
    public static long betweenSeconds(LocalDateTime start, LocalDateTime end) {
        return java.time.Duration.between(start, end).getSeconds();
    }

    /**
     * 获取当前时间字符串
     * @return 当前时间字符串
     */
    public static String getCurrentTimeString() {
        return java.time.LocalDateTime.now().toString();
    }

    /**
     * 解析时间字符串
     * @param time 时间字符串
     * @return 时间
     */
    public static LocalDateTime parseTime(String time) {
        // 时间字符串为空
        if (time == null || time.isEmpty())
            return null;

        // 解析时间字符串
        try {
            return LocalDateTime.parse(time);
        } catch (Exception e) {
            return null;
        }
    }
}
