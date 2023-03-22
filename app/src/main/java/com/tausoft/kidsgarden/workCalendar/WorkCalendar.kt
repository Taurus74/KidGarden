package com.tausoft.kidsgarden.workCalendar

import com.google.gson.annotations.SerializedName

data class WorkCalendar(
    @SerializedName("months")
    val months: List<Month>,
    @SerializedName("transitions")
    val transitions: List<Transition>,
    @SerializedName("year")
    val year: Int
)