package ru.smartro.worknote.andPOintD

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults

class LiveRealmData<T : RealmModel?>(private val results: RealmResults<T>) : LiveData<List<T>?>() {
    private val listener: RealmChangeListener<RealmResults<T>> = RealmChangeListener { results -> value = results.realm.copyFromRealm(results)  }
    override fun onActive() {
        results.addChangeListener(listener)
    }

    override fun onInactive() {
        results.removeChangeListener(listener)
    }
}

fun <T:RealmModel> RealmResults<T>.asLiveData() = LiveRealmData<T>(this)