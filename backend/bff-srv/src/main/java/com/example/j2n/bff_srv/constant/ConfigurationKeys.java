package com.example.j2n.bff_srv.constant;

import lombok.Getter;
import java.util.List;

@Getter
public class ConfigurationKeys {
        public static final String PORTFOLIO_NAME = "portfolio.name";
        public static final String PORTFOLIO_PRE_INTRODUCTION = "portfolio.pre-introduction";
        public static final String PORTFOLIO_MAIN_INTRODUCTION = "portfolio.main-introduction";
        public static final String PORTFOLIO_SKILLS = "portfolio.skills";
        public static final String PORTFOLIO_TIME_LINE = "portfolio.time-line";
        public static final String STORE_PORTAL_BASE_URL = "store-portal.base-url";
        public static final String ROOM_PORTAL_BASE_URL = "room-portal.base-url";
        public static final String MANAGEMENT_PORTAL_BASE_URL = "management-portal.base-url";
        public static final String SYSTEM_PORTAL_BASE_URL = "system-portal.base-url";
        public static final String ABOUT_PORTAL_BASE_URL = "about-portal.base-url";

        public static final List<String> PORTFOLIO_KEYS = List.of(PORTFOLIO_NAME, PORTFOLIO_PRE_INTRODUCTION,
                        PORTFOLIO_MAIN_INTRODUCTION, PORTFOLIO_SKILLS, PORTFOLIO_TIME_LINE);
        public static final List<String> GLOBAL_KEYS = List.of(STORE_PORTAL_BASE_URL, ROOM_PORTAL_BASE_URL,
                        MANAGEMENT_PORTAL_BASE_URL, SYSTEM_PORTAL_BASE_URL, ABOUT_PORTAL_BASE_URL);
}
