package com.app.covidanalysis.model

class TimelineResponse : ArrayList<TimelineResponse.TimelineResponseItem>(){
    data class TimelineResponseItem(
        val last_update: String,
        val total_cases: Int,
        val total_deaths: Int,
        val total_recovered: Int
    )
}