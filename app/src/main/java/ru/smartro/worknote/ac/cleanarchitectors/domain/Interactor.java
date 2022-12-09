package ru.smartro.worknote.ac.cleanarchitectors.domain;

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

// * Created ru.smartro.worknote.andPOintD.swipebtn.SwipeButton on 12/13/15.