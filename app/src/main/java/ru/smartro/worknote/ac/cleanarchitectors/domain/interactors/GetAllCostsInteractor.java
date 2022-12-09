package ru.smartro.worknote.ac.cleanarchitectors.domain.interactors;



import java.util.List;

import ru.smartro.worknote.ac.cleanarchitectors.domain.Cost;
import ru.smartro.worknote.ac.cleanarchitectors.domain.Interactor;

/**
 * Created bydmilicic
 * <p/>
 * This interactor is responsible for retrieving a list of costs from the database.
 */
public interface GetAllCostsInteractor extends Interactor {

    interface Callback {
        void onCostsRetrieved(List<Cost> costList);
    }
}
