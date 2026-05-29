package com.frigobrain.data.db.converter;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    public static String formatDate(long timestamp) {
        return fmt.format(new Date(timestamp));
    }

    public static long parseDate(String dateStr) {
        try {
            return fmt.parse(dateStr).getTime();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }
}
