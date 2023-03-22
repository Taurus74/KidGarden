package com.tausoft.kidsgarden.workCalendar

import com.google.gson.annotations.SerializedName

data class Month(
    @SerializedName("days")
    val days: String,
    @SerializedName("month")
    val month: Int
)