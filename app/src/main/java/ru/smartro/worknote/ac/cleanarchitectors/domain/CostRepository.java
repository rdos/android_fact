package ru.smartro.worknote.ac.cleanarchitectors.domain;

import java.util.List;

/**
 * Created bydmilicic
 */
public interface CostRepository {

    void insert(Cost cost);

    void update(Cost cost);

    Cost getCostById(long id);

    List<Cost> getAllCosts();

    List<Cost> getAllUnsyncedCosts();

    void markSynced(List<Cost> costs);

    void delete(Cost cost);
}
