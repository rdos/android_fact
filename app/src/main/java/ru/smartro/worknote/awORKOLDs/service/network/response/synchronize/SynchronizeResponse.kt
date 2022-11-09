package ru.smartro.worknote.awORKOLDs.service.network.response.synchronize

import com.google.gson.annotations.Expose

class SynchronizeResponse(
    @Expose
    val success: Boolean,
    @Expose
    val alert: String,
    @Expose
    val message: String
    )