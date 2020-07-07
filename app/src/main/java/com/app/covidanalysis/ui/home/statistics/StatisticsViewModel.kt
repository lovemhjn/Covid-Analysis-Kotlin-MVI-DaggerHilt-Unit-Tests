package com.app.covidanalysis.ui.home.statistics

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.app.covidanalysis.model.TimelineResponse
import com.app.covidanalysis.repository.TimelineRepository
import com.app.covidanalysis.ui.home.statistics.state.StatsStateEvent
import com.app.covidanalysis.ui.home.statistics.state.StatsViewState
import com.app.covidanalysis.util.AbsentLiveData
import com.app.covidanalysis.util.DataState

class StatisticsViewModel @ViewModelInject constructor(val repository: TimelineRepository) : ViewModel() {
    
    private val _stateEvent: MutableLiveData<StatsStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<StatsViewState> = MutableLiveData()

    val viewState: LiveData<StatsViewState>
        get() = _viewState


    val dataState: LiveData<DataState<StatsViewState>> = Transformations
            .switchMap(_stateEvent){stateEvent ->
                stateEvent?.let {
                    handleStateEvent(stateEvent)
                }
            }

    fun handleStateEvent(stateEvent: StatsStateEvent): LiveData<DataState<StatsViewState>> {
        println("DEBUG: New StateEvent detected: $stateEvent")
        return when(stateEvent){

            is StatsStateEvent.GetTimelineEvent -> {
                repository.getTimeline()
            }

            is StatsStateEvent.None ->{
                AbsentLiveData.create()
            }
        }
    }

    fun setTimelineData(timeline: TimelineResponse){
        val update = getCurrentViewStateOrNew()
        update.timelineResponse = timeline
        _viewState.value = update
    }

    fun getCurrentViewStateOrNew(): StatsViewState {
        return viewState.value?.let {
            it
        }?: StatsViewState()
    }

    fun setStateEvent(event: StatsStateEvent){
        val state: StatsStateEvent = event
        _stateEvent.value = state
    }
}