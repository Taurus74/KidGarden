package com.tausoft.kidsgarden.workCalendar

import com.google.gson.annotations.SerializedName

data class Transition(
    @SerializedName("from")
    val from: String,
    @SerializedName("to")
    val to: String
)