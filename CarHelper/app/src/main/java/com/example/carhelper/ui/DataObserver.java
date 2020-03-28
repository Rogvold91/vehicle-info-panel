package com.example.carhelper.ui;

import androidx.annotation.NonNull;

import com.example.carhelper.service.CarData;

public interface DataObserver {
    void onCarDataUpdated(@NonNull CarData data);
}
