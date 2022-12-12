package ru.smartro.worknote.ac.cleanarchitectors.presenters.impl;

import ru.smartro.worknote.ac.cleanarchitectors.uti.executor.Executor;
import ru.smartro.worknote.ac.cleanarchitectors.uti.executor.MainThread;
import ru.smartro.worknote.ac.cleanarchitectors.domain.interactors.AddCostInteractor;
import ru.smartro.worknote.ac.cleanarchitectors.domain.interactors.impl.AddCostInteractorImpl;
import ru.smartro.worknote.ac.cleanarchitectors.domain.CostRepository;
import ru.smartro.worknote.ac.cleanarchitectors.presenters.AbstractPresenter;
import ru.smartro.worknote.ac.cleanarchitectors.presenters.AddCostPresenter;

import java.util.Date;

/**
 * Createdbydmilicic
 */
public class AddCostPresenterImpl extends AbstractPresenter implements AddCostPresenter,
        AddCostInteractor.Callback {

    private AddCostPresenter.View mView;
    private CostRepository mCostRepository;

    public AddCostPresenterImpl(Executor executor, MainThread mainThread,
                                View view, CostRepository costRepository) {
        super(executor, mainThread);
        mView = view;
        mCostRepository = costRepository;
    }

    @Override
    public void addNewCost(Date date, double amount, String description, String category) {
        AddCostInteractor addCostInteractor = new AddCostInteractorImpl(mExecutor,
                mMainThread,
                this,
                mCostRepository,
                category,
                description,
                date,
                amount);
        addCostInteractor.execute();
    }

    @Override
    public void onCostAdded() {
        mView.onCostAdded();
    }


    @Override
    public void resume() {

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
}
