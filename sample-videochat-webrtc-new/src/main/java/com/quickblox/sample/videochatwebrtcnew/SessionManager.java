package com.quickblox.sample.videochatwebrtcnew;

import android.util.Log;

import com.quickblox.videochat.webrtc.QBRTCSession;

/**
 * Created by tereha on 10.07.15.
 */
public class SessionManager {

    private static volatile QBRTCSession currentSession;
    private static final String TAG = SessionManager.class.getSimpleName();

    public synchronized static QBRTCSession getCurrentSession() {
        if (currentSession != null) {
            Log.d(TAG, "getCurrentSession() from " + TAG + " " + currentSession.getSessionID());
        } else {
            Log.d(TAG, "getCurrentSession() from " + TAG + " null");
        }
        return currentSession;
    }

    public synchronized static void setCurrentSession(QBRTCSession qbCurrentSession) {
        if (qbCurrentSession != null) {
            Log.d(TAG, "setCurrentSession() from " + TAG + " " + qbCurrentSession.getSessionID());
        } else {
            Log.d(TAG, "setCurrentSession() from " + TAG + " null");
        }
        currentSession = qbCurrentSession;
    }
}
