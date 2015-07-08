package com.quickblox.sample.videochatwebrtcnew.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSessionDescription;
import com.quickblox.videochat.webrtc.callbacks.RTCSignallingMessageProcessorCallback;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.List;

/**
 * Created by tereha on 08.07.15.
 */
public class IncomeCallListenerService extends Service implements RTCSignallingMessageProcessorCallback{

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        QBRTCClient.getInstance().getInstance().addRTCSignallingMessageProcessorCallbackListener(this);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
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
    public void onReceiveCallFromUser(Integer integer, QBRTCSessionDescription qbrtcSessionDescription, SessionDescription sessionDescription) {


    }

    @Override
    public void onReceiveAcceptFromUser(Integer integer, QBRTCSessionDescription qbrtcSessionDescription, SessionDescription sessionDescription) {

    }

    @Override
    public void onReceiveRejectFromUser(Integer integer, QBRTCSessionDescription qbrtcSessionDescription) {

    }

    @Override
    public void onReceiveIceCandidatesFromUser(List<IceCandidate> list, Integer integer, QBRTCSessionDescription qbrtcSessionDescription) {

    }

    @Override
    public void onReceiveUserHungUpCall(Integer integer, QBRTCSessionDescription qbrtcSessionDescription) {

    }

    @Override
    public void onAddUserNeed(Integer integer, QBRTCSessionDescription qbrtcSessionDescription) {

    }
}
