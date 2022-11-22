package ru.smartro.worknote.awORKOLDs

import ru.smartro.worknote.awORKOLDs.service.NetObject

abstract class GETRequestA<TZ:NetObject> : AbsRequest<NoBody, TZ>(){
    override fun onGetRequestBodyIn(): NoBody {
        return NoBody()
    }
}