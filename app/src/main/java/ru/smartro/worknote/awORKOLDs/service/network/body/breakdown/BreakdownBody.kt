package ru.smartro.worknote.awORKOLDs.service.network.body.breakdown

import com.google.gson.annotations.SerializedName

data class BreakdownBody(
    @SerializedName("items")
    val items: List<BreakDownItem>
)