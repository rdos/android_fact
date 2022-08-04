package ru.smartro.worknote.domain.interactors;

import ru.smartro.worknote.domain.interactors.base.Interactor;
import ru.smartro.worknote.domain.model.Cost;

/**
 * Created by dmilicic on 12/27/15.
 */
public interface GetCostByIdInteractor extends Interactor {

    interface Callback {
        void onCostRetrieved(Cost cost);

        void noCostFound();
    }
}
