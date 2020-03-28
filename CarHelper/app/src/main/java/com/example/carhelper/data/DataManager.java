package com.example.carhelper.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.carhelper.util.Settings;

import static com.example.carhelper.util.Constants.START_SIMULATION;
import static com.example.carhelper.util.Constants.STOP_SIMULATION;

public class DataManager {

    private DataGenerator mGenerator;
    private Context mContext;
    private DataListener mListener;

    public DataManager(Context context, DataListener listener) {
        this.mContext = context;
        this.mListener = listener;
        mGenerator = SourceFabric.buildGenerator(Settings.getInstance(mContext).getmMaxSpeed(), Settings.getInstance(mContext).getmMaxRPM());
        mGenerator.setDataChangeListener(mListener::onDataUpdated);
    }

    public void registerReciever() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_SIMULATION);
        filter.addAction(STOP_SIMULATION);
        mContext.registerReceiver(commandReceiver, filter);
    }

    public void unregisterReciever() {
        mContext.unregisterReceiver(commandReceiver);
    }

    public void startSimulation(boolean start) {
        mGenerator.start(start);
    }

    BroadcastReceiver commandReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            DataManager.this.mGenerator.start( action.equals(START_SIMULATION) ? true : false);
        }
    };
}