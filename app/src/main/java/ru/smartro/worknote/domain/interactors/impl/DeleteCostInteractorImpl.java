package ru.smartro.worknote.domain.interactors.impl;

import ru.smartro.worknote.domain.executor.Executor;
import ru.smartro.worknote.domain.executor.MainThread;
import ru.smartro.worknote.domain.interactors.DeleteCostInteractor;
import ru.smartro.worknote.domain.interactors.base.AbstractInteractor;
import ru.smartro.worknote.domain.model.Cost;
import ru.smartro.worknote.domain.repository.CostRepository;

/**
 * Interactor responsible for deleting a cost from the database.
 * <p/>
 * Created by dmilicic on 12/26/15.
 */
public class DeleteCostInteractorImpl extends AbstractInteractor implements DeleteCostInteractor {

    private long                          mCostId;
    private DeleteCostInteractor.Callback mCallback;
    private CostRepository                mCostRepository;

    public DeleteCostInteractorImpl(Executor threadExecutor,
                                    MainThread mainThread, long costId,
                                    Callback callback, CostRepository costRepository) {
        super(threadExecutor, mainThread);
        mCostId = costId;
        mCallback = callback;
        mCostRepository = costRepository;
    }

    @Override
    public void run() {

        // check if this object even exists
        final Cost cost = mCostRepository.getCostById(mCostId);

        // delete this cost item
        if (cost != null) mCostRepository.delete(cost);

        // notify on the main thread
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onCostDeleted(cost);
            }
        });
    }
}
