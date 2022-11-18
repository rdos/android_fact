package ru.smartro.worknote.abs.cleanarchitectors.presenters;

import ru.smartro.worknote.abs.cleanarchitectors.domain.Cost;

import java.util.Date;

/**
 * Createdbydmilicic
 */
public interface EditCostPresenter {

    interface View extends BaseView {

        void onCostRetrieved(Cost cost);

        void onCostUpdated(Cost cost);
    }

    void getCostById(long id);

    void editCost(Cost cost, Date date, double amount, String description, String category);
}
