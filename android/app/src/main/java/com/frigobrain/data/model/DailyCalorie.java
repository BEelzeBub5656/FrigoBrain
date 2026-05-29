package com.frigobrain.data.model;

import androidx.room.ColumnInfo;

/** 每日热量趋势查询结果 */
public class DailyCalorie {
    @ColumnInfo(name = "log_date")
    public long logDate;

    @ColumnInfo(name = "dailyCal")
    public int dailyCal;
}
