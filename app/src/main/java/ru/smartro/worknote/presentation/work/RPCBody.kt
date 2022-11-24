package ru.smartro.worknote.presentation.work

import com.google.gson.annotations.SerializedName
import ru.smartro.worknote.Inull
import ru.smartro.worknote.Snull
import ru.smartro.worknote.awORKOLDs.service.NetObject

open class RPCBody<T> (
    var type: String = Snull,
    var payload: T? = null
) : NetObject()
