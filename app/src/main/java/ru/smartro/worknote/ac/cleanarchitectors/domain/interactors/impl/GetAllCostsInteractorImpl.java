package ru.smartro.worknote.ac.cleanarchitectors.domain.interactors.impl;

import ru.smartro.worknote.ac.cleanarchitectors.domain.interactors.GetAllCostsInteractor;
import ru.smartro.worknote.ac.cleanarchitectors.uti.executor.Executor;
import ru.smartro.worknote.ac.cleanarchitectors.uti.executor.MainThread;
import ru.smartro.worknote.ac.cleanarchitectors.domain.AbstractInteractor;
import ru.smartro.worknote.ac.cleanarchitectors.domain.Cost;
import ru.smartro.worknote.ac.cleanarchitectors.domain.CostRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This interactor handles getting all costs from the database in a sorted manner. Costs should be sorted by date with
 * the most recent one coming first and the oldest one coming last.
 * <p/>
 * Created bydmilicic
 */
public class GetAllCostsInteractorImpl extends AbstractInteractor implements GetAllCostsInteractor {

    private Callback       mCallback;
    private CostRepository mCostRepository;

    private Comparator<Cost> mCostComparator = new Comparator<Cost>() {
        @Override
        public int compare(Cost lhs, Cost rhs) {

            if (lhs.getDate().before(rhs.getDate()))
                return 1;

            if (rhs.getDate().before(lhs.getDate()))
                return -1;

            return 0;
        }
    };

    public GetAllCostsInteractorImpl(Executor threadExecutor, MainThread mainThread, CostRepository costRepository,
                                     Callback callback) {
        super(threadExecutor, mainThread);

        if (costRepository == null || callback == null) {
            throw new IllegalArgumentException("Arguments can not be null!");
        }

        mCostRepository = costRepository;
        mCallback = callback;
    }

    @Override
    public void run() {
        // retrieve the costs from the database
        final List<Cost> costs = mCostRepository.getAllCosts();

        // sort them so the most recent cost items come first, and oldest comes last
        Collections.sort(costs, mCostComparator);

        // Show costs on the main thread
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onCostsRetrieved(costs);
            }
        });
    }
}
