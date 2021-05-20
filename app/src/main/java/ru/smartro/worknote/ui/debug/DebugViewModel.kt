package ru.smartro.worknote.ui.debug

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel

class DebugViewModel(application: Application) : BaseViewModel(application) {

  fun findContainerProgress () : List<Int>{
      return db.findContainerProgress()
  }

  fun findPlatformProgress () : List<Int>{
      return db.findPlatformProgress()
  }

}