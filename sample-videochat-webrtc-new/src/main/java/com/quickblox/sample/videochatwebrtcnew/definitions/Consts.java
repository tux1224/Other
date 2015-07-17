package com.quickblox.sample.videochatwebrtcnew.definitions;

public class Consts {
//    public static final String APP_ID = "92";
//    public static final String AUTH_KEY = "wJHdOcQSxXQGWx5";
//    public static final String AUTH_SECRET = "BTFsj7Rtt27DAmT";
//    public static final String EMPTY_STRING = "";
    public static final String APP_ID = "18846";
    public static final String AUTH_KEY = "64JzC2cuLkSMUq7";
    public static final String AUTH_SECRET = "s4VCJZq4uWNer7H";
    public static final String EMPTY_STRING = "";

    public final static String OPPONENTS = "opponents";
    public static final String CONFERENCE_TYPE = "conference_type";
    public static final String INCOME_CALL_FRAGMENT = "income_call_fragment";
    public static final String CONVERSATION_CALL_FRAGMENT = "conversation_call_fragment";

    public static final int CALL_ACTIVITY_CLOSE = 1000;
    public final static int LOGIN_TASK_CODE = 1002;
    public final static int LOGIN_RESULT_CODE = 1003;
    public final static int RESULT_CODE_1 = 1;
    public final static int RESULT_CODE_2 = 2;
    public final static int RESULT_CODE_3 = 3;
    ;

    public final static String PARAM_PINTENT = "pendingIntent";
    public final static String LOGIN_RESULT = "result";

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
