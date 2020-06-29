package com.app.covidanalysis.ui.home.news.state

sealed class NewsStateEvent {

    class GetNewsEvent(val date:String):NewsStateEvent()
    class None:NewsStateEvent()
}