package ru.smartro.worknote.presentation.work

import okhttp3.ResponseBody
import ru.smartro.worknote.App

class AuthRequest: AbsRequest() {


    override fun onAfter(body: String?) {
        App.getAppParaMS().token =body
    }

    override fun onBefore() {

    }


}