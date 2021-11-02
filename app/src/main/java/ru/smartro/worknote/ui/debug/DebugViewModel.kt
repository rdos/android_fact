package ru.smartro.worknote.ui.debug

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel

class DebugViewModel(application: Application) : BaseViewModel(application) {

  fun findContainerProgress () : List<Int>{
      return db.findCountContainerIsServed()
  }

  fun findPlatformProgress () : List<Int>{
      return db.findCountPlatformIsServed()
  }

}