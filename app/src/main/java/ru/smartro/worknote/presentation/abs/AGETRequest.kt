package ru.smartro.worknote.presentation.abs

import ru.smartro.worknote.presentation.ANoBodyGET
import ru.smartro.worknote.presentation.ac.AbsRequest
import ru.smartro.worknote.presentation.ac.NetObject

abstract class AGETRequest<TZ: NetObject> : AbsRequest<ANoBodyGET, TZ>(){
    override fun onGetRequestBodyIn(): ANoBodyGET {
        return ANoBodyGET()
    }
}