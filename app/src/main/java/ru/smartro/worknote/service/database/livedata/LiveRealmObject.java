package ru.smartro.worknote.service.database.livedata;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmObjectChangeListener;

public class LiveRealmObject<T extends RealmModel> extends LiveData<T> {

    private final RealmObjectChangeListener<T> listener = (object, objectChangeSet) -> {
        if (!objectChangeSet.isDeleted()) {
            setValue(object);
        } else {
            setValue(null);
        }
    };

    @MainThread
    public LiveRealmObject(@NonNull T object) {
        //noinspection ConstantConditions
        if (object == null) {
            throw new IllegalArgumentException("The object cannot be null!");
        }
        if (!RealmObject.isManaged(object)) {
            throw new IllegalArgumentException("LiveRealmObject only supports managed RealmModel instances!");
        }
        if (!RealmObject.isValid(object)) {
            throw new IllegalArgumentException("The provided RealmObject is no longer valid, and therefore cannot be observed for changes.");
        }
        setValue(object);
    }

    @Override
    protected void onActive() {
        super.onActive();
        T object = getValue();
        if (object != null && RealmObject.isValid(object)) {
            RealmObject.addChangeListener(object, listener);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        T object = getValue();
        if (object != null && RealmObject.isValid(object)) {
            RealmObject.removeChangeListener(object, listener);
        }
    }
}