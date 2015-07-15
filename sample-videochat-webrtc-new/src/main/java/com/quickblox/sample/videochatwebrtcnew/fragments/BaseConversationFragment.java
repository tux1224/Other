package com.quickblox.sample.videochatwebrtcnew.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.quickblox.sample.videochatwebrtcnew.R;
import com.quickblox.sample.videochatwebrtcnew.SessionManager;
import com.quickblox.sample.videochatwebrtcnew.activities.BaseLogginedUserActivity;
import com.quickblox.sample.videochatwebrtcnew.activities.CallActivity;
import com.quickblox.sample.videochatwebrtcnew.definitions.Consts;
import com.quickblox.sample.videochatwebrtcnew.holder.DataHolder;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tereha on 15.07.15.
 */
public abstract class BaseConversationFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = BaseConversationFragment.class.getSimpleName();

    protected ArrayList<Integer> opponents;
    protected int qbConferenceType;
    protected int startReason;
    protected String sessionID;
    protected String callerName;

    private TextView opponentNumber;
    private TextView connectionStatus;
    private ImageView opponentAvatar;
    private ToggleButton dynamicToggleVideoCall;
    private ToggleButton micToggleVideoCall;
    private ImageButton handUpVideoCall;
    private TextView callerNameView;
//    protected View view;
    private Map<String, String> userInfo;
    private View opponentItemView;
    private HorizontalScrollView camerasOpponentsList;
    //    public static LinearLayout opponentsFromCall;
    private LayoutInflater inflater;
    private ViewGroup container;
    private Bundle savedInstanceState;
    private boolean isVideoEnabled = true;
    private boolean isAudioEnabled = true;
    private List<QBUser> allUsers = new ArrayList<>();
    private LinearLayout actionVideoButtonsLayout;
    private View actionBar;

    private LinearLayout noVideoImageContainer;
    private boolean isMessageProcessed;
    private MediaPlayer ringtone;
    private View localVideoView;
    private View remoteVideoView;
    private IntentFilter intentFilter;
    private AudioStreamReceiver audioStreamReceiver;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(getContentView(), container, false);
        this.inflater = inflater;
        this.container = container;
        Log.d(TAG, "Fragment. Thread id: " + Thread.currentThread().getId());

        ((CallActivity) getActivity()).initActionBarWithTimer();

        if (getArguments() != null) {
            opponents = getArguments().getIntegerArrayList(Consts.OPPONENTS);
            qbConferenceType = getArguments().getInt(Consts.CONFERENCE_TYPE);
            startReason = getArguments().getInt(CallActivity.START_CONVERSATION_REASON);
            sessionID = getArguments().getString(CallActivity.SESSION_ID);
            callerName = getArguments().getString(CallActivity.CALLER_NAME);

            Log.d(TAG, "CALLER_NAME: " + callerName);

        }

        initViews(view);
//        createOpponentsList(opponents);
        return view;
    }

    protected abstract int getContentView();

    protected void actionButtonsEnabled(boolean enability) {

        micToggleVideoCall.setEnabled(enability);
        dynamicToggleVideoCall.setEnabled(enability);

        // inactivate toggle buttons
        micToggleVideoCall.setActivated(enability);
        dynamicToggleVideoCall.setActivated(enability);
    }


    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");

        getActivity().registerReceiver(audioStreamReceiver, intentFilter);

        super.onStart();
        QBRTCSession session = SessionManager.getCurrentSession();
        if (!isMessageProcessed) {
            if (startReason == StartConversetionReason.INCOME_CALL_FOR_ACCEPTION.ordinal()) {
                Log.d(TAG, "acceptCall() from " + TAG);
                session.acceptCall(session.getUserInfo());
            } else {
                Log.d(TAG, "startCall() from " + TAG);
                session.startCall(session.getUserInfo());
                startOutBeep();
            }
            isMessageProcessed = true;
        }
    }

    private void startOutBeep() {
        ringtone = MediaPlayer.create(getActivity(), R.raw.beep);
        ringtone.setLooping(true);
        ringtone.start();

    }

    public void stopOutBeep() {

        if (ringtone != null) {
            try {
                ringtone.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ringtone.release();
            ringtone = null;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() from " + TAG);
        super.onCreate(savedInstanceState);

        intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);

        audioStreamReceiver = new AudioStreamReceiver();
    }

    protected void initViews(View view) {
//        opponentsFromCall = (LinearLayout) view.findViewById(R.id.opponentsFromCall);

        dynamicToggleVideoCall = (ToggleButton) view.findViewById(R.id.dynamicToggleVideoCall);
        dynamicToggleVideoCall.setOnClickListener(this);
        micToggleVideoCall = (ToggleButton) view.findViewById(R.id.micToggleVideoCall);
        micToggleVideoCall.setOnClickListener(this);

        callerNameView = (TextView) view.findViewById(R.id.incUserName);
        callerNameView.setText(DataHolder.getUserNameByID(opponents.get(0)));
        callerNameView.setBackgroundResource(BaseLogginedUserActivity.selectBackgrounForOpponent((
                DataHolder.getUserIndexByID(opponents.get(0))) + 1));

        handUpVideoCall = (ImageButton) view.findViewById(R.id.handUpVideoCall);
        handUpVideoCall.setOnClickListener(this);

        noVideoImageContainer = (LinearLayout) view.findViewById(R.id.noVideoImageContainer);

//        actionButtonsEnabled(false);
        Log.d(TAG, "initViews() from " + TAG);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
        stopOutBeep();
        getActivity().unregisterReceiver(audioStreamReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dynamicToggleVideoCall:
                if (SessionManager.getCurrentSession() != null) {
                    Log.d(TAG, "Dynamic switched!");
                    SessionManager.getCurrentSession().switchAudioOutput();
                }
                break;
            case R.id.micToggleVideoCall:
                if (SessionManager.getCurrentSession() != null) {
                    if (isAudioEnabled) {
                        Log.d(TAG, "Mic is off!");
                        SessionManager.getCurrentSession().setAudioEnabled(false);
                        isAudioEnabled = false;
                    } else {
                        Log.d(TAG, "Mic is on!");
                        SessionManager.getCurrentSession().setAudioEnabled(true);
                        isAudioEnabled = true;
                    }
                }
                break;
            case R.id.handUpVideoCall:
                stopOutBeep();
                actionButtonsEnabled(false);
                handUpVideoCall.setEnabled(false);
                Log.d(TAG, "Call is stopped");

                ((CallActivity) getActivity()).hangUpCurrentSession();
                handUpVideoCall.setEnabled(false);
                handUpVideoCall.setActivated(false);
                break;
            default:
                break;
        }
    }

    public static enum StartConversetionReason {
        INCOME_CALL_FOR_ACCEPTION,
        OUTCOME_CALL_MADE;
    }

//    private List<QBUser> getOpponentsFromCall(ArrayList<Integer> opponents) {
//        ArrayList<QBUser> opponentsList = new ArrayList<>();
//
//        for (Integer opponentId : opponents) {
//            try {
//                opponentsList.add(QBUsers.getUser(opponentId));
//            } catch (QBResponseException e) {
//                e.printStackTrace();
//            }
//        }
//        return opponentsList;
//    }

//    private void createOpponentsList(List<Integer> opponents) {
//        if (opponents.size() != 0) {
//            for (Integer i : opponents) {
//                addOpponentPreviewToList(i, opponentsFromCall);
//            }
//        }
//    }
//
//    private void addOpponentPreviewToList(Integer userID, LinearLayout opponentsFromCall) {
//
//        if (opponentsFromCall.findViewById(userID) == null) {
//
//            View opponentItemView = inflater.inflate(R.layout.list_item_opponent_from_call, opponentsFromCall, false);
//            opponentItemView.setId(userID);
//
//            opponentItemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Main opponent Selected");
//                }
//            });
//
//            TextView opponentNumber = (TextView) opponentItemView.findViewById(R.id.opponentNumber);
//            opponentNumber.setText(String.valueOf(ListUsersActivity.getUserIndex(userID)));
//            opponentNumber.setBackgroundResource(BaseLogginedUserActivity.resourceSelector
//                    (ListUsersActivity.getUserIndex(userID)));
//
//            ImageView opponentAvatar = (ImageView) opponentItemView.findViewById(R.id.opponentAvatar);
//            opponentAvatar.setImageResource(R.drawable.ic_noavatar);
//
//            opponentsFromCall.addView(opponentItemView);
//        } else {
//            opponentsFromCall.addView(opponentsFromCall.findViewById(userID));
//        }
//    }

//    private String getCallerName(QBRTCSession session) {
//        String s = new String();
//        int i = session.getCallerID();
//
//        allUsers.addAll(DataHolder.usersList);
//
//        for (QBUser usr : allUsers) {
//            if (usr.getId().equals(i)) {
//                s = usr.getFullName();
//            }
//        }
//        return s;
//    }

    private class AudioStreamReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(AudioManager.ACTION_HEADSET_PLUG)){
                Log.d(TAG, "ACTION_HEADSET_PLUG " + intent.getIntExtra("state", -1));
            } else if (intent.getAction().equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)){
                Log.d(TAG, "ACTION_SCO_AUDIO_STATE_UPDATED " + intent.getIntExtra("EXTRA_SCO_AUDIO_STATE", -2));
            }

            if (intent.getIntExtra("state", -1) == 0 /*|| intent.getIntExtra("EXTRA_SCO_AUDIO_STATE", -1) == 0*/){
                dynamicToggleVideoCall.setChecked(false);
            } else if (intent.getIntExtra("state", -1) == 1) {
                dynamicToggleVideoCall.setChecked(true);
            } else {
//                Toast.makeText(context, "Output audio stream is incorrect", Toast.LENGTH_LONG).show();
            }
            dynamicToggleVideoCall.invalidate();
        }
    }
}
