package com.example.carhelper.data;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.carhelper.service.CarData;

import java.util.Random;

import static com.example.carhelper.util.Constants.DEFAULT_MAX_PRM;
import static com.example.carhelper.util.Constants.DEFAULT_MAX_SPEED;
import static com.example.carhelper.util.Constants.UPDATE_PERIOD;

public class SinusGenerator implements DataGenerator {


    private final int AMOUNT_FUNCTIONS = 10;
    private final int MULTIPLICATOR =  10;
    private double maxVal = DEFAULT_MAX_SPEED;
    private int maxPrm = DEFAULT_MAX_PRM;
    private double speed = 0;
    private double prm = 0;
    private DataListener mDataListener;
    private Handler mHandler;
    private final long PERIOD_UPDATE = UPDATE_PERIOD;
    private final Random rnd = new Random();

    public SinusGenerator(int maxSpeed, int maxPrm) {
        mHandler = new Handler();
        this.maxVal = maxSpeed;
        this.maxPrm = maxPrm;
    }
    @Override
    public void start(boolean start) {
        if (start) {
            mHandler.post(task);
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void setDataChangeListener(@NonNull DataListener listener) {
        mDataListener = listener;
    }

    @Override
    public double getPRM() {
        return prm;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public void generate() {
        long currentTime = System.currentTimeMillis();
        double val = 0;
        for (int i = 0; i < AMOUNT_FUNCTIONS; ++i) {
            val += Math.sin(currentTime + Math.PI / (i + 1));
        }
        speed += val * MULTIPLICATOR;
        speed = speed > maxVal ? maxVal : speed;
        speed = speed > 0 ? speed : 0;
        prm = speed / maxVal * maxPrm;
    }

    Runnable task = new Runnable() {
        @Override
        public void run() {
            generate();
            mDataListener.onDataUpdated(new CarData(getSpeed(), getPRM()));
            mHandler.postDelayed(task, PERIOD_UPDATE);
        }
    };
}
