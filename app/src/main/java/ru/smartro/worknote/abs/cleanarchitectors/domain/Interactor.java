package ru.smartro.worknote.abs.cleanarchitectors.domain;

/**
 * Created bydmilicic
 */
public interface Interactor {

    /**
     * This is the main method that starts an interactor. It will make sure that the interactor operation is done on a
     * background thread.
     */
    void execute();
}

// * Created ru.smartro.worknote.work.swipebtn.SwipeButton on 12/13/15.