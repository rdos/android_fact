package ru.smartro.worknote.utils;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class ListableSparseArray<E> extends SparseArray<E> {

    public List<E> asList() {
        List<E> arrayList = new ArrayList<>(this.size());
        for (int i = 0; i < this.size(); i++)
            arrayList.add(this.valueAt(i));
        return arrayList;
    }
}
