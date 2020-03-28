package com.example.carhelper.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.carhelper.R;
import com.example.carhelper.data.DataListener;
import com.example.carhelper.data.DataManager;

import java.util.ArrayList;
import java.util.List;

import static com.example.carhelper.util.Remote.tryExec;

public class SpeedService extends Service implements DataListener {

    private static final String TAG = SpeedService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    private final RemoteCallbackList<IDataCallback> mDataCallbacks = new RemoteCallbackList<>();
    private DataManager mDataManager;

    public SpeedService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Service has been created");
        String notificationIdString =
                getResources().getString(R.string.vehicle_helper);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel =
                new NotificationChannel(
                        notificationIdString,
                        "Car Pseudo cluster",
                        NotificationManager.IMPORTANCE_NONE);
        notificationManager.createNotificationChannel(notificationChannel);
        Notification notification =
                new Notification.Builder(
                        this, notificationIdString).setSmallIcon(R.drawable.ic_launcher_background).build();

        startForeground(NOTIFICATION_ID, notification);
        mDataManager = new DataManager(this, this);
        mDataManager.registerReciever();
     }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataManager.unregisterReciever();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final ISpeedService.Stub mBinder = new ISpeedService.Stub() {
        @Override
        public void addCallback(IDataCallback callback) {
            Log.d(TAG, "New Callback has been added");
            mDataCallbacks.register(callback);
        }

        @Override
        public void removeCallback(IDataCallback callback) {
            Log.d(TAG, "Callback has been removed");
            mDataCallbacks.unregister(callback);
        }

        @Override
        public void startStopSimulation(boolean start) {
            Log.d(TAG, "Start simulation: " + start);
            mDataManager.startSimulation(start);
        }
    };


    @Override
    public void onDataUpdated(@NonNull CarData data) {
        // Broadcast to all clients the new value.
        final int N = mDataCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IDataCallback callback = mDataCallbacks.getBroadcastItem(i);
            tryExec(() -> callback.vehicleDataChanged(data));
        }
        mDataCallbacks.finishBroadcast();
    }
}
