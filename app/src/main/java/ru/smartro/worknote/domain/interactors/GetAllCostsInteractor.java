package ru.smartro.worknote.domain.interactors;

import ru.smartro.worknote.domain.interactors.base.Interactor;
import ru.smartro.worknote.domain.model.Cost;

import java.util.List;

/**
 * Created by dmilicic on 12/10/15.
 * <p/>
 * This interactor is responsible for retrieving a list of costs from the database.
 */
public interface GetAllCostsInteractor extends Interactor {

    interface Callback {
        void onCostsRetrieved(List<Cost> costList);
    }
}
