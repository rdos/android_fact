package ru.smartro.worknote.ac.cleanarchitectors.domain.interactors;

import ru.smartro.worknote.ac.cleanarchitectors.domain.Interactor;

/**
 * Created bydmilicic
 */
public interface AddCostInteractor extends Interactor {

    interface Callback {
        void onCostAdded();
    }
}
