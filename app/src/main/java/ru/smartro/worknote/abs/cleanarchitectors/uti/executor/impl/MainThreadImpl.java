package ru.smartro.worknote.abs.cleanarchitectors.uti.executor.impl;

import android.os.Handler;
import android.os.Looper;

import ru.smartro.worknote.abs.cleanarchitectors.uti.executor.MainThread;

/**
 * This class makes sure that the runnable we provide will be run on the main UI thread.
 * <p/>
 * Createdbydmilicic
 */
public class MainThreadImpl implements MainThread {

    private static MainThread sMainThread;

    private Handler mHandler;

    private MainThreadImpl() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static MainThread getInstance() {
        if (sMainThread == null) {
            sMainThread = new MainThreadImpl();
        }

        return sMainThread;
    }
}
