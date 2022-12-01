package ru.smartro.worknote.ac.cleanarchitectors.presenters.impl;

import ru.smartro.worknote.ac.cleanarchitectors.domain.interactors.GetAllCostsInteractor;
import ru.smartro.worknote.ac.cleanarchitectors.uti.executor.Executor;
import ru.smartro.worknote.ac.cleanarchitectors.uti.executor.MainThread;
import ru.smartro.worknote.ac.cleanarchitectors.domain.interactors.impl.GetAllCostsInteractorImpl;
import ru.smartro.worknote.ac.cleanarchitectors.domain.Cost;
import ru.smartro.worknote.ac.cleanarchitectors.domain.CostRepository;
import ru.smartro.worknote.ac.cleanarchitectors.uti.converter.DailyTotalCostConverter;
import ru.smartro.worknote.ac.cleanarchitectors.presenters.model.DailyTotalCost;
import ru.smartro.worknote.ac.cleanarchitectors.presenters.AbstractPresenter;
import ru.smartro.worknote.ac.cleanarchitectors.presenters.MainPresenter;

import java.util.List;

/**
 * Created bydmilicic
 */
public class MainPresenterImpl extends AbstractPresenter implements MainPresenter,
        GetAllCostsInteractor.Callback {

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
}
