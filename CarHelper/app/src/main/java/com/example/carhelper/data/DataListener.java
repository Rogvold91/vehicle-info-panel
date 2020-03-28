package com.example.carhelper.data;

import androidx.annotation.NonNull;

import com.example.carhelper.service.CarData;

public interface DataListener {
    void onDataUpdated(@NonNull CarData data);
}
