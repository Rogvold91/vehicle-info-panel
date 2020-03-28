package com.example.carhelper.util;

import androidx.annotation.NonNull;

public class SpeedConverter implements LabelConverter {

    @Override
    public String getLabelFor(double progress, double maxProgress, @NonNull Unit from, @NonNull Unit to) {
        if (from.equals(to))
            return String.valueOf((int) Math.round(progress));
        if (from.equals(Unit.SI) && to.equals(Unit.Empire)) {
            return String.valueOf((int) Math.round(progress / 1.609));
        }
        return  String.valueOf((int) Math.round(progress * 1.609));
    }
}
