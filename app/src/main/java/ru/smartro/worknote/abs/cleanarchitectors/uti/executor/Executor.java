package ru.smartro.worknote.abs.cleanarchitectors.uti.executor;

import ru.smartro.worknote.abs.cleanarchitectors.domain.AbstractInteractor;

/**
 * This executor is responsible for running interactors on background threads.
 * <p/>
 * Createdbydmilicic
 */
public interface Executor {

    /**
     * This method should call the interactor's run method and thus start the interactor. This should be called
     * on a background thread as interactors might do lengthy operations.
     *
     * @param interactor The interactor to run.
     */
    void execute(final AbstractInteractor interactor);
}
