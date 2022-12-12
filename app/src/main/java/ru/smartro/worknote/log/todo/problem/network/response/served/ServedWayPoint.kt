package ru.smartro.worknote.log.todo.problem.network.response.served


import com.google.gson.annotations.SerializedName


data class ServedWayPoint(
    @SerializedName("id")
    val id: Int,
    @SerializedName("p_id")
    val pId: Int
)