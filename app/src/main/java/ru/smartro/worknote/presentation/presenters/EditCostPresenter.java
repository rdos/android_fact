package ru.smartro.worknote.presentation.presenters;

import ru.smartro.worknote.domain.model.Cost;

import java.util.Date;

/**
 * Created by dmilicic on 12/27/15.
 */
public interface EditCostPresenter {

    interface View extends BaseView {

        void onCostRetrieved(Cost cost);

        void onCostUpdated(Cost cost);
    }

    void getCostById(long id);

    void editCost(Cost cost, Date date, double amount, String description, String category);
}
