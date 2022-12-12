package ru.smartro.worknote.presentation.ac

import okhttp3.Response

class BadRequest(response: Response) : THR(response.code){
    init {
        sentToSentry(response)
    }
}