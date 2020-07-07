package com.app.covidanalysis.ui.home.statistics.state

sealed class StatsStateEvent {

    class GetTimelineEvent : StatsStateEvent()
    class None : StatsStateEvent()
}