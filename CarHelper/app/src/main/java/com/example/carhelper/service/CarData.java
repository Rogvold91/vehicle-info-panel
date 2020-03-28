package com.example.carhelper.service;

import android.os.Parcel;
import android.os.Parcelable;

public class CarData implements Parcelable {

    private double speed;
    private double prm;

    public CarData(double speed, double prm) {
        this.speed = speed;
        this.prm = prm;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getPrm() {
        return prm;
    }

    public void setPrm(double prm) {
        this.prm = prm;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.speed);
        dest.writeDouble(this.prm);
    }

    protected CarData(Parcel in) {
        this.speed = in.readDouble();
        this.prm = in.readDouble();
    }

    public static final Parcelable.Creator<CarData> CREATOR = new Parcelable.Creator<CarData>() {
        @Override
        public CarData createFromParcel(Parcel source) {
            return new CarData(source);
        }

        @Override
        public CarData[] newArray(int size) {
            return new CarData[size];
        }
    };
}
