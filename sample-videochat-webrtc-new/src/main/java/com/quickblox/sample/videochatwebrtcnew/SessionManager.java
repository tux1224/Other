package com.quickblox.sample.videochatwebrtcnew;

import com.quickblox.videochat.webrtc.QBRTCSession;

/**
 * Created by tereha on 10.07.15.
 */
public class SessionManager {

    public static QBRTCSession currentSession;

    public static QBRTCSession getCurrentSession() {
        return currentSession;
    }

    public static void setCurrentSession(QBRTCSession currentSession) {
        SessionManager.currentSession = currentSession;
    }
}
