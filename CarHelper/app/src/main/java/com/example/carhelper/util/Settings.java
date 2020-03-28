package com.example.carhelper.util;

import android.content.Context;
import android.graphics.Color;

import com.example.carhelper.R;

public class Settings {

    private final int mMaxSpeed;

    public int getmMaxRPM() {
        return mMaxRPM;
    }

    public int getmMaxSpeed() {
        return mMaxSpeed;
    }

    private final int mMaxRPM;
    private Context mContext;
    private static Settings instance;

    public static Settings getInstance(Context context) {
        if (instance == null)
            instance = new Settings(context);
        return  instance;
    }
    private Settings(Context context) {
        mMaxSpeed = context.getResources().getInteger(R.integer.max_speed);
        mMaxRPM = context.getResources().getInteger(R.integer.max_rpm);
    }

}
