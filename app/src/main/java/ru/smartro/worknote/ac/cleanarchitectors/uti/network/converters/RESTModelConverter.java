package ru.smartro.worknote.ac.cleanarchitectors.uti.network.converters;

import ru.smartro.worknote.ac.cleanarchitectors.domain.Cost;
import ru.smartro.worknote.ac.cleanarchitectors.uti.network.model.RESTCost;

import java.util.Date;

/**
 * Createdbydmilicic
 */
public class RESTModelConverter {

    public static RESTCost convertToRestModel(Cost cost) {

        String desc = cost.getDescription();
        double amount = cost.getAmount();
        String category = cost.getCategory();
        Date date = cost.getDate();
        long id = cost.getId();

        return new RESTCost(id, category, desc, date, amount);
    }
}
