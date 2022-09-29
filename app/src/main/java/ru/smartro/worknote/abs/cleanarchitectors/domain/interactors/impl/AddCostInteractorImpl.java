package ru.smartro.worknote.abs.cleanarchitectors.domain.interactors.impl;

import ru.smartro.worknote.abs.cleanarchitectors.uti.executor.Executor;
import ru.smartro.worknote.abs.cleanarchitectors.uti.executor.MainThread;
import ru.smartro.worknote.abs.cleanarchitectors.domain.interactors.AddCostInteractor;
import ru.smartro.worknote.abs.cleanarchitectors.domain.AbstractInteractor;
import ru.smartro.worknote.abs.cleanarchitectors.domain.Cost;
import ru.smartro.worknote.abs.cleanarchitectors.domain.CostRepository;

import java.util.Date;

/**
 * This interactor is responsible for creating and adding a new cost item into the database. It should get all the data needed to create
 * a new cost object and it should insert it in our repository.
 * <p/>
 * Created bydmilicic
 */
public class AddCostInteractorImpl extends AbstractInteractor implements AddCostInteractor {

    private AddCostInteractor.Callback mCallback;
    private CostRepository             mCostRepository;

    private String mCategory;
    private String mDescription;
    private Date   mDate;
    private double mAmount;

    public AddCostInteractorImpl(Executor threadExecutor, MainThread mainThread,
                                 Callback callback, CostRepository costRepository, String category,
                                 String description, Date date, double amount) {
        super(threadExecutor, mainThread);
        mCallback = callback;
        mCostRepository = costRepository;
        mCategory = category;
        mDescription = description;
        mDate = date;
        mAmount = amount;
    }

    @Override
    public void run() {
        // create a new cost object and insert it
        Cost cost = new Cost(mCategory, mDescription, mDate, mAmount);
        mCostRepository.insert(cost);

        // notify on the main thread that we have inserted this item
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onCostAdded();
            }
        });

    }
}
