package ru.smartro.worknote.abs.cleanarchitectors.domain.interactors;

import ru.smartro.worknote.abs.cleanarchitectors.domain.Interactor;
import ru.smartro.worknote.abs.cleanarchitectors.domain.Cost;

/**
 * Created bydmilicic
 */
public interface EditCostInteractor extends Interactor {

    interface Callback {

        void onCostUpdated(Cost cost);
    }
}
