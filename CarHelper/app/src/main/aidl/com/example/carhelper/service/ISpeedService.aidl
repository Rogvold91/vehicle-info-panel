// ISpeedService.aidl
package com.example.carhelper.service;

/**
* Registration callbacks and the simplest commands
*/

import com.example.carhelper.service.IDataCallback;

interface ISpeedService {

    void addCallback(in IDataCallback callback);
    void removeCallback(in IDataCallback callback);
    void startStopSimulation(boolean start);
}
