package ru.smartro.worknote.ac.cleanarchitectors.domain.interactors;

import ru.smartro.worknote.ac.cleanarchitectors.domain.Interactor;
import ru.smartro.worknote.ac.cleanarchitectors.domain.Cost;

/**
 * Created bydmilicic
 */
public interface EditCostInteractor extends Interactor {

    interface Callback {

        void onCostUpdated(Cost cost);
    }
}
