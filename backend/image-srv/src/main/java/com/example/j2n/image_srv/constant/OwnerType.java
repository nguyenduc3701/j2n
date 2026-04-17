package com.example.j2n.image_srv.constant;

public class OwnerType {
    public static final String USER = "USER";
    public static final String PRODUCT = "PRODUCT";
    public static final String ROOM = "ROOM";
    public static final String TRAVEL = "TRAVEL";
    public static final String DEFAULT = "DEFAULT";

    public static boolean isValid(String ownerType) {
        if (ownerType == null) {
            return false;
        }
        String upperCaseOwnerType = ownerType.toUpperCase();
        return USER.equals(upperCaseOwnerType) || PRODUCT.equals(upperCaseOwnerType) || ROOM.equals(upperCaseOwnerType)
                || TRAVEL.equals(upperCaseOwnerType) || DEFAULT.equals(upperCaseOwnerType);
    }
}
