package ru.smartro.worknote.domain.interactors;

import ru.smartro.worknote.domain.interactors.base.Interactor;

/**
 * Created by dmilicic on 12/23/15.
 */
public interface AddCostInteractor extends Interactor {

    interface Callback {
        void onCostAdded();
    }
}
