package com.quickblox.sample.videochatwebrtcnew.definitions;

public class Consts {
    public static final String APP_ID = "92";
    public static final String AUTH_KEY = "wJHdOcQSxXQGWx5";
    public static final String AUTH_SECRET = "BTFsj7Rtt27DAmT";
    public static final String EMPTY_STRING = "";

    public static final int CALL_ACTIVITY_CLOSE = 1000;

    //Shared Preferences constants
    public static final String USER_LOGIN = "user_login";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_IS_LOGINED = "is_logined";

    //CALL ACTIVITY CLOSE REASONS
    public static final int CALL_ACTIVITY_CLOSE_WIFI_DISABLED = 1001;
    public static final String WIFI_DISABLED = "wifi_disabled";
    public static final String OPPONENTS_LIST_EXTRAS = "opponents_list";
    public static final String CALL_DIRECTION_TYPE_EXTRAS = "call_direction_type";
    public static final String CALL_TYPE_EXTRAS = "call_type";
    public static final String QBRTCSESSION_EXTRAS = "qbrtcsession";
    public static final String USER_INFO_EXTRAS = "user_info";
    public static final String IS_SERVICE_AUTOSTARTED = "autostart";
    public static final String SHARED_PREFERENCES = "preferences";


    public enum CALL_DIRECTION_TYPE {
        INCOMING, OUTGOING
    }
}
