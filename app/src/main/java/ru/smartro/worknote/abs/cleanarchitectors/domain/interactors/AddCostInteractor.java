package ru.smartro.worknote.abs.cleanarchitectors.domain.interactors;

import ru.smartro.worknote.abs.cleanarchitectors.domain.Interactor;

/**
 * Created bydmilicic
 */
public interface AddCostInteractor extends Interactor {

    interface Callback {
        void onCostAdded();
    }
}
