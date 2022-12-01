package ru.smartro.worknote.ac.cleanarchitectors.presenters;


import java.util.Date;

/**
 * Created bydmilicic
 */
public interface AddCostPresenter extends BasePresenter {


    interface View extends BaseView {

        void onCostAdded();
    }

    void addNewCost(Date date, double amount, String description, String category);

}
