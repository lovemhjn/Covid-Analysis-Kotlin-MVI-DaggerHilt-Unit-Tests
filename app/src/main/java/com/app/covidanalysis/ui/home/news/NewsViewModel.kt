package com.app.covidanalysis.ui.home.news

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.app.covidanalysis.model.NewsResponse
import com.app.covidanalysis.repository.NewsRepository
import com.app.covidanalysis.ui.home.news.state.NewsStateEvent
import com.app.covidanalysis.ui.home.news.state.NewsViewState
import com.app.covidanalysis.util.AbsentLiveData
import com.app.covidanalysis.util.DataState

class NewsViewModel @ViewModelInject constructor(val repository: NewsRepository):ViewModel() {
    private val _stateEvent: MutableLiveData<NewsStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<NewsViewState> = MutableLiveData()

    val viewState: LiveData<NewsViewState>
        get() = _viewState


    val dataState: LiveData<DataState<NewsViewState>> = Transformations
            .switchMap(_stateEvent) { stateEvent ->
                stateEvent?.let {
                    handleStateEvent(stateEvent)
                }
            }

    fun handleStateEvent(stateEvent: NewsStateEvent): LiveData<DataState<NewsViewState>> {
        println("DEBUG: New StateEvent detected: $stateEvent")
        return when (stateEvent) {

            is NewsStateEvent.GetNewsEvent -> {
                repository.getNews(stateEvent.date)
            }

            is NewsStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    fun setSummaryData(news: NewsResponse) {
        val update = getCurrentViewStateOrNew()
        update.newsResponse = news
        _viewState.value = update
    }

    fun getCurrentViewStateOrNew(): NewsViewState {
        return viewState.value?.let {
            it
        } ?: NewsViewState()
    }

    fun setStateEvent(event: NewsStateEvent) {
        val state: NewsStateEvent = event
        _stateEvent.value = state
    }
}