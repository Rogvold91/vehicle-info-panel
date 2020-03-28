package com.example.carhelper.util;

import androidx.annotation.NonNull;

public class FabricConverter {

    public static LabelConverter buildConverter(@NonNull Unit from, @NonNull Unit to) {
        return new SpeedConverter();
    }
}
