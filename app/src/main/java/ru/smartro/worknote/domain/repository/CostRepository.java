package ru.smartro.worknote.domain.repository;

import ru.smartro.worknote.domain.model.Cost;

import java.util.List;

/**
 * Created by dmilicic on 12/13/15.
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
