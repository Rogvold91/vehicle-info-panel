package com.example.carhelper.util;

import androidx.annotation.NonNull;

public interface LabelConverter {

    default String getLabelFor(double progress, double maxProgress) {
        return getLabelFor(progress, maxProgress, Unit.SI, Unit.SI);
    }

    default String getLabelFor(double progress, double maxProgress, @NonNull Unit from,@NonNull Unit to) {
        return String.valueOf((int) Math.round(progress));
    }
}
