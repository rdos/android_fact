package ru.smartro.worknote.presentation.presenters.impl;

import ru.smartro.worknote.domain.executor.Executor;
import ru.smartro.worknote.domain.executor.MainThread;
import ru.smartro.worknote.domain.interactors.DeleteCostInteractor;
import ru.smartro.worknote.domain.interactors.GetAllCostsInteractor;
import ru.smartro.worknote.domain.interactors.impl.DeleteCostInteractorImpl;
import ru.smartro.worknote.domain.interactors.impl.GetAllCostsInteractorImpl;
import ru.smartro.worknote.domain.model.Cost;
import ru.smartro.worknote.domain.repository.CostRepository;
import ru.smartro.worknote.presentation.converter.DailyTotalCostConverter;
import ru.smartro.worknote.presentation.model.DailyTotalCost;
import ru.smartro.worknote.presentation.presenters.AbstractPresenter;
import ru.smartro.worknote.presentation.presenters.MainPresenter;

import java.util.List;

/**
 * Created by dmilicic on 12/13/15.
 */
public class MainPresenterImpl extends AbstractPresenter implements MainPresenter,
        GetAllCostsInteractor.Callback,
        DeleteCostInteractor.Callback {

    private MainPresenter.View mView;
    private CostRepository     mCostRepository;

    public MainPresenterImpl(Executor executor, MainThread mainThread,
                             View view, CostRepository costRepository) {
        super(executor, mainThread);
        mView = view;
        mCostRepository = costRepository;
    }

    @Override
    public void resume() {
        getAllCosts();
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void getAllCosts() {
        // get all costs
        GetAllCostsInteractor getCostsInteractor = new GetAllCostsInteractorImpl(
                mExecutor,
                mMainThread,
                mCostRepository,
                this
        );
        getCostsInteractor.execute();
    }

    @Override
    public void onCostsRetrieved(List<Cost> costList) {
        List<DailyTotalCost> dailyTotalCosts = DailyTotalCostConverter.convertCostsToDailyCosts(costList);
        mView.showCosts(dailyTotalCosts);
    }

    @Override
    public void deleteCost(long costId) {

        // delete this cost item in a background thread
        DeleteCostInteractor deleteCostInteractor = new DeleteCostInteractorImpl(
                mExecutor,
                mMainThread,
                costId,
                this,
                mCostRepository
        );
        deleteCostInteractor.execute();
    }


    @Override
    public void onCostDeleted(Cost cost) {
        mView.onCostDeleted(cost);
    }
}
