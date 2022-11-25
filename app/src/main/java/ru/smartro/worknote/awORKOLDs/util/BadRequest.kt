package ru.smartro.worknote.awORKOLDs.util

import okhttp3.Response

class BadRequest(response: Response) : THR(response.code){
    init {
        sentToSentry(response)
    }
}