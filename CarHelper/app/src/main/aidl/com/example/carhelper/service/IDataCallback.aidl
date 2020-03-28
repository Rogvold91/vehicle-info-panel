// IDataCallback.aidl
package com.example.carhelper.service;

parcelable CarData;

interface IDataCallback {
    oneway void vehicleDataChanged(in CarData data);
}
