package com.example.carhelper.util;

import android.graphics.Color;

public class Constants {

    // Drawing constants
    public static final double DEFAULT_MAX_SPEED = 180.0;
    public static final double DEFAULT_MAJOR_TICK_STEP = 20.0;
    public static final int DEFAULT_MINOR_TICKS = 1;
    public static final int DEFAULT_LABEL_TEXT_SIZE_DP = 12;
    public static final int DEFAULT_UNITS_TEXT_SIZE_DP = 24;
    public static final int DEFAULT_COLOR = Color.rgb(180, 180, 180);
    public static final int DEFAULT_MAX_PRM = 8;
    //

    public static final long UPDATE_PERIOD = 200;
    public static final long ANIMATION_DURATION = 30;
    public static final long ANIMATION_DELAY = 0;

    public static final String KMH = "Km/H";
    public static final String MPH = "MPH";

    public static final String START_SIMULATION = "com.example.carhelper.START_SIMULATION";
    public static final String STOP_SIMULATION = "com.example.carhelper.STOP_SIMULATION";


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    public static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    public static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    public static final int UI_ANIMATION_DELAY = 300;

}
