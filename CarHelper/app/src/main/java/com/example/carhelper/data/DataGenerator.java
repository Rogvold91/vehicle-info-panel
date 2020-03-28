package com.example.carhelper.data;

import androidx.annotation.NonNull;

import com.example.carhelper.service.CarData;

public interface DataGenerator {

    default double getSpeed() {
        return 0;
    }

    default double getPRM() {
        return 0;
    }

    default CarData dataGenerate() { return new CarData(0,0);}

    void start(boolean start);

    void setDataChangeListener(@NonNull DataListener listener);

    void generate();
}
