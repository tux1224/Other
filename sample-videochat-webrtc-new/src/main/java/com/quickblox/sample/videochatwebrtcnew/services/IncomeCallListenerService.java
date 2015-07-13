package com.quickblox.sample.videochatwebrtcnew.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.sample.videochatwebrtcnew.R;
import com.quickblox.sample.videochatwebrtcnew.SessionManager;
import com.quickblox.sample.videochatwebrtcnew.SharedPreferencesManager;
import com.quickblox.sample.videochatwebrtcnew.activities.CallActivity;
import com.quickblox.sample.videochatwebrtcnew.activities.OpponentsActivity;
import com.quickblox.sample.videochatwebrtcnew.definitions.Consts;
import com.quickblox.sample.videochatwebrtcnew.holder.DataHolder;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

import org.jivesoftware.smack.SmackException;

import java.util.List;
import java.util.Map;

/**
 * Created by tereha on 08.07.15.
 */
public class IncomeCallListenerService extends Service implements QBRTCClientSessionCallbacks {

    private static final String TAG = IncomeCallListenerService.class.getSimpleName();
    private QBChatService chatService;
    private String login;
    private String password;

    @Override
    public void onCreate() {
        super.onCreate();
        QBSettings.getInstance().fastConfigInit(Consts.APP_ID, Consts.AUTH_KEY, Consts.AUTH_SECRET);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        if (!QBChatService.isInitialized()) {
            Log.d(TAG, "!QBChatService.isInitialized()");
            QBChatService.init(this);
            chatService = QBChatService.getInstance();
        }

        if (intent.getExtras()!= null) {
            parseIntentExtras(intent);
        }

        if(!QBChatService.getInstance().isLoggedIn()){
            createSession(login, password);
        }
//        else {
//            initQBRTCClient();
//        }

        startForeground(1, createNotification());

        return super.onStartCommand(intent, flags, startId);
    }

    private Notification createNotification() {
        Notification.Builder notificationBuilder = new Notification.Builder(IncomeCallListenerService.this);
        notificationBuilder.setSmallIcon(R.drawable.logo_qb)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_qb))
                .setTicker(getResources().getString(R.string.service_launched))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.logged_in_as) + " " +
                        DataHolder.getUserNameByLogin(login));

        Notification notification = notificationBuilder.build();

        return notification;
    }

    private void initQBRTCClient() {

        try {
            QBChatService.getInstance().startAutoSendPresence(60);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        }

        // Add signalling manager
        QBChatService.getInstance().getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    QBRTCClient.getInstance().addSignaling((QBWebRTCSignaling) qbSignaling);
                }
            }
        });

        // Add activity as callback to RTCClient
        QBRTCClient.getInstance().addSessionCallbacksListener(this);

        // Start mange QBRTCSessions according to VideoCall parser's callbacks
        QBRTCClient.getInstance().prepareToProcessCalls(this);
    }

    private void parseIntentExtras(Intent intent) {
        Log.d(TAG, "parseIntentExtras()");
        login = intent.getStringExtra(Consts.USER_LOGIN);
        password = intent.getStringExtra(Consts.USER_PASSWORD);
        Log.d(TAG, "login = " + login + " password = " + password);
    }

    private void createSession(final String login, final String password) {

        Log.d(TAG, "createSession()");
        final QBUser user = new QBUser(login, password);
        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {
                Log.d(TAG, "onSuccess create session with params");
                user.setId(session.getUserId());

                if (chatService.isLoggedIn()) {
                    Log.d(TAG, "chatService.isLoggedIn()");
                    initQBRTCClient();
                    startOpponentsActivity();
                    saveUserDataToPreferences(login, password);
                } else {
                    Log.d(TAG, "!chatService.isLoggedIn()");
                    chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                        @Override
                        public void onSuccess(QBUser result, Bundle params) {
                            Log.d(TAG, "onSuccess login to chat with params");
                            initQBRTCClient();
                            startOpponentsActivity();
                            saveUserDataToPreferences(login, password);
                        }

                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess login to chat");
                            initQBRTCClient();
                            startOpponentsActivity();
                            saveUserDataToPreferences(login, password);
                        }

                        @Override
                        public void onError(List errors) {
                            Toast.makeText(IncomeCallListenerService.this, "Error when login", Toast.LENGTH_SHORT).show();
                            for (Object error : errors) {
                                Log.d(TAG, error.toString());
                            }
                        }
                    });
                }

            }

            @Override
            public void onSuccess() {
                super.onSuccess();
                Log.d(TAG, "onSuccess create session");
            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(IncomeCallListenerService.this, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDataToPreferences(String login, String password){
//        SharedPreferencesManager sManager = new SharedPreferencesManager(this);
//        sManager.savePref(Consts.USER_LOGIN, login);
//        sManager.savePref(Consts.USER_PASSWORD, password);

        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(Consts.USER_LOGIN, login);
        ed.putString(Consts.USER_PASSWORD, password);
        ed.commit();

        Log.d(TAG, "login = " + sharedPreferences.getString(Consts.USER_LOGIN, null) + " password = " + sharedPreferences.getString(Consts.USER_PASSWORD, null));
    }

    private void startOpponentsActivity(){
        boolean isServiceAutostarted = getSharedPreferences(Consts.SHARED_PREFERENCES, MODE_PRIVATE)
                .getBoolean(Consts.IS_SERVICE_AUTOSTARTED, false);
        if (!isServiceAutostarted) {
            Intent intent = new Intent(IncomeCallListenerService.this, OpponentsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        QBRTCClient.getInstance().removeSessionsCallbacksListener(this);
        QBChatService.getInstance().destroy();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }



    //========== Implement methods ==========//

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        if (SessionManager.getCurrentSession() != null){
            qbrtcSession.rejectCall(qbrtcSession.getUserInfo());
        } else {
            SessionManager.setCurrentSession(qbrtcSession);
            CallActivity.start(this, qbrtcSession.getConferenceType(), qbrtcSession.getOpponents(),
                    qbrtcSession.getUserInfo(), Consts.CALL_DIRECTION_TYPE.INCOMING);
        }
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {

    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {

    }
}
