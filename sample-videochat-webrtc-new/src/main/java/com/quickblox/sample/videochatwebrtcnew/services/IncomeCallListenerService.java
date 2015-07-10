package com.quickblox.sample.videochatwebrtcnew.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.sample.videochatwebrtcnew.R;
import com.quickblox.sample.videochatwebrtcnew.SessionManager;
import com.quickblox.sample.videochatwebrtcnew.SharedPreferencesManager;
import com.quickblox.sample.videochatwebrtcnew.activities.CallActivity;
import com.quickblox.sample.videochatwebrtcnew.activities.OpponentsActivity;
import com.quickblox.sample.videochatwebrtcnew.definitions.Consts;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCException;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientConnectionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;

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
        } else {
            initQBRTCClient();
        }

//        initQBRTCClient();

        return super.onStartCommand(intent, flags, startId);

    }

    private void initQBRTCClient() {


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
//        loginPB.setVisibility(View.VISIBLE);
        Log.d(TAG, "createSession()");
        final QBUser user = new QBUser(login, password);
        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {
                Log.d(TAG, "onSuccess create session with params");
                user.setId(session.getUserId());

//                loginPB.setVisibility(View.INVISIBLE);

                if (chatService.isLoggedIn()) {
                    Log.d(TAG, "chatService.isLoggedIn()");
//                    startCallActivity(login);
                    initQBRTCClient();
                    startOpponentsActivity();
                    saveUserDataToPreferences(login, password);
                } else {
                    Log.d(TAG, "!chatService.isLoggedIn()");
                    chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                        @Override
                        public void onSuccess(QBUser result, Bundle params) {
                            Log.d(TAG, "onSuccess login to chat with params");
//                            startCallActivity(login);
                            initQBRTCClient();
                            startOpponentsActivity();
                            saveUserDataToPreferences(login, password);
                        }

                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess login to chat");
//                            startCallActivity(login);
                            initQBRTCClient();
                            startOpponentsActivity();
                            saveUserDataToPreferences(login, password);
                        }

                        @Override
                        public void onError(List errors) {
//                            loginPB.setVisibility(View.INVISIBLE);
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
//                loginPB.setVisibility(View.INVISIBLE);
                Toast.makeText(IncomeCallListenerService.this, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDataToPreferences(String login, String password){
        SharedPreferencesManager sManager = SharedPreferencesManager.getPrefsManager();
        sManager.savePref(Consts.USER_LOGIN, login);
        sManager.savePref(Consts.USER_PASSWORD, password);
    }

    private void startOpponentsActivity(){
        Intent intent = new Intent(IncomeCallListenerService.this, OpponentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        QBRTCClient.getInstance().removeSessionsCallbacksListener(this);

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
