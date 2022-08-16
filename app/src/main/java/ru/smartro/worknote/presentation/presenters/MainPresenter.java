package ru.smartro.worknote.presentation.presenters;

import ru.smartro.worknote.domain.model.Cost;
import ru.smartro.worknote.presentation.model.DailyTotalCost;

import java.util.List;

/**
 * Created by dmilicic on 12/10/15.
 */
public interface MainPresenter extends BasePresenter {

    interface View extends BaseView {

        void showCosts(List<DailyTotalCost> costs);

        void onClickDeleteCost(long costId);

        void onClickEditCost(long costId, int position);

        void onCostDeleted(Cost cost);
    }

    void getAllCosts();

    void deleteCost(long costId);
}
