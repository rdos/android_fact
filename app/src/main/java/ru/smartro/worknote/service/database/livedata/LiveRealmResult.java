package ru.smartro.worknote.service.database.livedata;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class LiveRealmResult<T extends RealmModel> extends LiveData<List<T>> {
    private final RealmResults<T> results;

    private OrderedRealmCollectionChangeListener<RealmResults<T>> listener = (results, changeSet) -> LiveRealmResult.this.setValue(results);

    @MainThread
    public LiveRealmResult(@NonNull RealmResults<T> results) {
        if (!results.isValid()) {
            throw new IllegalArgumentException("The provided RealmResults is no longer valid, the Realm instance it belongs to is closed. It can no longer be observed for changes.");
        }
        this.results = results;
        if (results.isLoaded()) {
            setValue(results);
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (results.isValid()) { // invalidated results can no longer be observed.
            results.addChangeListener(listener);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (results.isValid()) {
            results.removeChangeListener(listener);
        }
    }
}