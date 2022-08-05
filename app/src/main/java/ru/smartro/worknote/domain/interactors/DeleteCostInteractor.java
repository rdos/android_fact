package ru.smartro.worknote.domain.interactors;

import ru.smartro.worknote.domain.interactors.base.Interactor;
import ru.smartro.worknote.domain.model.Cost;

/**
 * Created by dmilicic on 12/26/15.
 */
public interface DeleteCostInteractor extends Interactor {

    interface Callback {
        void onCostDeleted(Cost cost);
    }
}
