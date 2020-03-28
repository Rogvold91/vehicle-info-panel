package com.example.carhelper.util;

import android.os.RemoteException;
import android.util.Log;


public abstract class Remote {

    private static final String TAG = Remote.class.getSimpleName();

    /**
     * Throwing void function.
     */
    public interface RemoteVoidFunction {
        /**
         * The actual throwing function.
         * */
        void call() throws RemoteException;
    }


    /**
     * Wraps remote void function and logs in case of {@link RemoteException}.
     */
    public static void tryExec(RemoteVoidFunction func) {
        try {
            func.call();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to execute remote call", e);
        }
    }
}
