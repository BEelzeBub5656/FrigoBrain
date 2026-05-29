package com.frigobrain.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Constants {

    private Constants() {}

    // Database
    public static final String DB_NAME = "frigobrain.db";

    // SharedPreferences keys
    public static final String PREFS_NAME = "frigobrain_prefs";
    public static final String KEY_CURRENT_USER_ID = "current_user_id";
    public static final String KEY_IOT_ENABLED = "iot_enabled";

    // Intent actions
    public static final String ACTION_SYNC_DATA = "com.frigobrain.SYNC_DATA";
    public static final String ACTION_NEW_FOOD = "com.frigobrain.NEW_FOOD";
    public static final String ACTION_COMMAND_RECEIVED = "com.frigobrain.COMMAND_RECEIVED";

    // Meal types
    public static final String MEAL_BREAKFAST = "BREAKFAST";
    public static final String MEAL_LUNCH = "LUNCH";
    public static final String MEAL_DINNER = "DINNER";
    public static final String MEAL_SNACK = "SNACK";

    // Diet types
    public static final String DIET_NORMAL = "NORMAL";
    public static final String DIET_VEGAN = "VEGAN";
    public static final String DIET_VEGETARIAN = "VEGETARIAN";
    public static final String DIET_LOW_CAL = "LOW_CAL";
    public static final String DIET_HIGH_PROTEIN = "HIGH_PROTEIN";

    // Storage types
    public static final String STORAGE_FRIDGE = "FRIDGE";
    public static final String STORAGE_FREEZER = "FREEZER";
    public static final String STORAGE_PANTRY = "PANTRY";

    // User roles
    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_MEMBER = "MEMBER";

    // Waste reasons
    public static final String WASTE_EXPIRED = "EXPIRED";
    public static final String WASTE_SPOILED = "SPOILED";
    public static final String WASTE_OTHER = "OTHER";

    // Notification
    public static final String CHANNEL_EXPIRY = "frigobrain_expiry";
    public static final int NOTIFY_EXPIRY_ID = 1001;

    // Huawei IoT (已确认真实端点)
    public static final String IOT_MQTT_HOST = "21158429fd.st1.iotda-device.cn-north-4.myhuaweicloud.com";
    public static final int IOT_MQTT_PORT = 443; // WebSocket
    public static final long SYNC_INTERVAL_MS = 30 * 60 * 1000; // 30 minutes
    // 已注册设备凭证（内置）
    public static final String IOT_DEVICE_ID = "FrigoBrain5656_BEEL";
    public static final String IOT_DEVICE_SECRET = "565627pp";

    // Date format
    public static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final SimpleDateFormat DATE_FMT_CN = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);

    // Daily recommended intake (Chinese Dietary Guidelines)
    public static final int RDA_CALORIES = 2000;
    public static final double RDA_PROTEIN = 60.0;
    public static final double RDA_FAT = 60.0;
    public static final double RDA_CARBS = 300.0;

    // Common allergens / ingredients to exclude
    public static final String[] COMMON_ALLERGENS = {
        "花生", "花生油", "海鲜", "虾", "螃蟹", "鱼", "贝类",
        "辣椒", "花椒", "大蒜", "洋葱", "生姜",
        "牛奶", "奶酪", "黄油", "酸奶",
        "鸡蛋", "豆腐", "豆制品",
        "猪肉", "牛肉", "羊肉", "鸡肉",
        "小麦", "面粉", "麸质",
        "芝麻", "香油", "芥末"
    };
}
