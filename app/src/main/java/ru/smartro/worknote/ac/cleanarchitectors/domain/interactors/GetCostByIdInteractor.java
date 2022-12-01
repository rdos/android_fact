package ru.smartro.worknote.ac.cleanarchitectors.domain.interactors;

import ru.smartro.worknote.ac.cleanarchitectors.domain.Interactor;
import ru.smartro.worknote.ac.cleanarchitectors.domain.Cost;

/**
 * Created bydmilicic
 */
public interface GetCostByIdInteractor extends Interactor {

    interface Callback {
        void onCostRetrieved(Cost cost);

        void noCostFound();
    }
}
