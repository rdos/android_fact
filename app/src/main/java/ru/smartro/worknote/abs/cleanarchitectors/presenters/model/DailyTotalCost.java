package ru.smartro.worknote.abs.cleanarchitectors.presenters.model;

import ru.smartro.worknote.abs.cleanarchitectors.domain.Cost;

import java.util.Date;
import java.util.List;

/**
 * Created bydmilicic
 */
public class DailyTotalCost {

    private List<Cost> mCostList;

    private Date   mDate;
    private double mTotalCost;

    public DailyTotalCost(List<Cost> costList, Date date) {
        mCostList = costList;
        mDate = date;

        // eagerly calculate the total cost
        mTotalCost = 0.0;
        for (int idx = 0; idx < costList.size(); idx++) {
            mTotalCost += costList.get(idx).getAmount();
        }
    }

    public List<Cost> getCostList() {
        return mCostList;
    }

    public Date getDate() {
        return mDate;
    }

    public double getTotalCost() {
        return mTotalCost;
    }

    @Override
    public String toString() {
        return "DailyTotalCost{" +
                "mCostList=" + mCostList +
                ", mDate=" + mDate +
                ", mTotalCost=" + mTotalCost +
                '}';
    }
}
