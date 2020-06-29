package com.app.covidanalysis.ui.home.summary.state

sealed class MainStateEvent {

    class GetSummaryEvent: MainStateEvent()

    class None: MainStateEvent()


}