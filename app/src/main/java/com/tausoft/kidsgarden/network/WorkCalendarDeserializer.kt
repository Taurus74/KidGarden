package com.tausoft.kidsgarden.network

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tausoft.kidsgarden.workCalendar.WorkCalendar
import java.lang.reflect.Type

class WorkCalendarDeserializer: JsonDeserializer<WorkCalendar> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WorkCalendar {
        val jsonObject = json?.asJsonObject
        return Gson().fromJson(jsonObject, WorkCalendar::class.java)
    }
}