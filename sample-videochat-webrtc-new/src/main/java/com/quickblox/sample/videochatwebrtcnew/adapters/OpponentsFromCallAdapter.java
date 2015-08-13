package com.quickblox.sample.videochatwebrtcnew.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.videochat.webrtc.view.QBGLVideoView;

import java.util.List;

/**
 * Created by tereha on 24.02.15.
 */
public class OpponentsFromCallAdapter extends BaseAdapter {

    private static final int NUM_IN_ROW = 3;
    private static final String TAG = OpponentsFromCallAdapter.class.getSimpleName();
    private final int itemHeight;
    private final int itemWidth;

    private Context context;
    private List<Integer> opponents;
    private LayoutInflater inflater;


    public OpponentsFromCallAdapter(Context context, List<Integer> users, GridView gridView) {
        this.context = context;
        this.opponents = users;
        this.inflater = LayoutInflater.from(context);
        itemWidth = gridView.getMeasuredWidth() / NUM_IN_ROW;
        itemHeight = gridView.getMeasuredHeight() / 2;
        Log.d(TAG, "item width=" + itemWidth + ", item height=" + itemHeight);
    }


    @Override
    public int getCount() {
        return opponents.size();
    }

    @Override
    public Integer getItem(int position) {
        return opponents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView=" + position);
        QBGLVideoView videoView = new QBGLVideoView(context);
        videoView.setLayoutParams(new AbsListView.LayoutParams(itemWidth, itemHeight));
        videoView.setTag(opponents.get(position));
        return videoView;
    }


    public static class ViewHolder {
        TextView opponentsNumber;
        TextView connectionStatus;
        QBGLVideoView opponentLittleCamera;
        ImageView opponentAvatar;


    }
}
