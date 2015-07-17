package com.quickblox.sample.videochatwebrtcnew.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.quickblox.sample.videochatwebrtcnew.definitions.Consts;

/**
 * Created by tereha on 13.07.15.
 */
public class AutoStartServiceBroadcastReceiver extends BroadcastReceiver {

    final String TAG = AutoStartServiceBroadcastReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String login = sharedPreferences.getString(Consts.USER_LOGIN, null);
        String password = sharedPreferences.getString(Consts.USER_PASSWORD, null);

        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Consts.IS_SERVICE_AUTOSTARTED, true);
            editor.commit();

            Intent serviceIntent = new Intent(context, IncomeCallListenerService.class);
            serviceIntent.putExtra(Consts.USER_LOGIN, login);
            serviceIntent.putExtra(Consts.USER_PASSWORD, password);
            serviceIntent.putExtra(Consts.IS_SERVICE_AUTOSTARTED, true);
            context.startService(serviceIntent);
        }

        Log.d(TAG, "onReceive " + intent.getAction());
        Log.d(TAG, "login = " + login+ " password = " + password + " isAutoStarted = " + sharedPreferences.getBoolean(Consts.IS_SERVICE_AUTOSTARTED, false));
    }
}

