package com.example.j2n.report_srv.constant;

public class ReportConstant {
    public final static String ACTIVE_USERS_METRIC_KEY = "active_users";
    public final static String INACTIVE_USERS_METRIC_KEY = "inactive_users";
    public final static String TOTAL_TOURS_METRIC_KEY = "total_products";
    public final static String EMPTY_ROOMS_METRIC_KEY = "empty_rooms";
    public final static String TOTAL_BILLS_METRIC_KEY = "total_bills_this_month";
    public final static String UNPAID_AMOUNT_METRIC_KEY = "remaining_unpaid_amount_this_month";
    public final static String ELECTRIC_AMOUNT_METRIC_KEY = "total_electricity_amount_this_month";
    public final static String WATER_AMOUNT_METRIC_KEY = "total_water_amount_this_month";

    public final static String ROOM_CATEGORY = "ROOM";
    public final static String ACCOUNT_CATEGORY = "ACCOUNT";
    public final static String PRODUCT_CATEGORY = "PRODUCT";
    public final static String FINANCE_CATEGORY = "FINANCE";
    public final static String UTILITY_CATEGORY = "UTILITY";

    public final static String USER_TYPE_CHART = "USER_TYPE";
    public final static String STATUS_AVAILABLE = "AVAILABLE";
    
    private ReportConstant() {
    }
}
