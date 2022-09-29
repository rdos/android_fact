package ru.smartro.worknote.abs.cleanarchitectors.domain.interactors;

import ru.smartro.worknote.abs.cleanarchitectors.domain.Interactor;
import ru.smartro.worknote.abs.cleanarchitectors.domain.Cost;

/**
 * Created bydmilicic
 */
public interface GetCostByIdInteractor extends Interactor {

    interface Callback {
        void onCostRetrieved(Cost cost);

        void noCostFound();
    }
}
