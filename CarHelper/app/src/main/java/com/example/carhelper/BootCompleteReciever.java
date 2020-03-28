package com.example.carhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

import com.example.carhelper.service.SpeedService;

public class BootCompleteReciever extends BroadcastReceiver {

    private final String TAG = BootCompleteReciever.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.d(TAG, "Boot completed");
            context.startForegroundService(new Intent(context, SpeedService.class));
        }
    }
}
