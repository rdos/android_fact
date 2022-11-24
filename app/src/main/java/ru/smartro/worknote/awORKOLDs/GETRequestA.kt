package ru.smartro.worknote.awORKOLDs

import ru.smartro.worknote.awORKOLDs.service.NetObject

abstract class GETRequestA<TZ:NetObject> : AbsRequest<NoBodyGET, TZ>(){
    override fun onGetRequestBodyIn(): NoBodyGET {
        return NoBodyGET()
    }
}