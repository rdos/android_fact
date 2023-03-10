package ru.smartro.worknote.ac.cleanarchitectors.presenters;

import ru.smartro.worknote.ac.cleanarchitectors.domain.Cost;
import ru.smartro.worknote.ac.cleanarchitectors.presenters.model.DailyTotalCost;

import java.util.List;

/**
 * Created bydmilicic
 */
public interface MainPresenter extends BasePresenter {

    interface View extends BaseView {

        void showCosts(List<DailyTotalCost> costs);

        void onClickDeleteCost(long costId);

        void onClickEditCost(long costId, int position);

        void onCostDeleted(Cost cost);
    }

    void getAllCosts();

    //void deleteCost(long costId);
}
