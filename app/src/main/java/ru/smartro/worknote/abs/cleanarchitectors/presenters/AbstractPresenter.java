package ru.smartro.worknote.abs.cleanarchitectors.presenters;

import ru.smartro.worknote.abs.cleanarchitectors.uti.executor.Executor;
import ru.smartro.worknote.abs.cleanarchitectors.uti.executor.MainThread;

/**
 * Created bydmilicic
 */
public abstract class AbstractPresenter {
    protected Executor   mExecutor;
    protected MainThread mMainThread;

    public AbstractPresenter(Executor executor, MainThread mainThread) {
        mExecutor = executor;
        mMainThread = mainThread;
    }
}
