package ru.smartro.worknote.andPOintD

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults

// TODO: ?!! RealmResults<T>?  if (results != null) {
class LiveRealmData<T : RealmModel?>(private val results: RealmResults<T>) : LiveData<List<T>?>(), RealmChangeListener<RealmResults<T>>  {

    override fun onActive() {
        results.addChangeListener(this)
    }

    override fun onInactive() {
        results.removeChangeListener(this)
    }

    override fun onChange(t: RealmResults<T>) {
        val res = results.realm.copyFromRealm(results)
        Handler(Looper.getMainLooper()).postDelayed({
                value=res
            },201)
        }
}

