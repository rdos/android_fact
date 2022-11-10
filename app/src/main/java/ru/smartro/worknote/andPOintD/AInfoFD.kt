package ru.smartro.worknote.andPOintD

import ru.smartro.worknote.LOG
import ru.smartro.worknote.abs.AInformFD
import ru.smartro.worknote.work.PlatformEntity

//todo: FYI: AbstractInfoDF=BaseInfoDF
abstract class AInfoFD: AInformFD() {

    final override fun onGetEntity(): PlatformEntity? {
        LOG.warn("DON'T_USE") //not use
        return null
    }

   final override fun onLiveData(entity: PlatformEntity) {
        LOG.warn("DON'T_USE")   //not use
    }

    final override fun onNextFragment(entity: PlatformEntity) {
       navigateBack()
    }

    final override fun onStyle(sview: SmartROllc) {
//        TODO("Not yet implemented")
    }
    final override fun onBindLayoutState(): Boolean {
//        TODO("Not yet implemented")
        return false
    }

}