package com.app.covidanalysis.ui.home.summary

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.app.covidanalysis.model.SummaryResponse
import com.app.covidanalysis.repository.SummaryRepository
import com.app.covidanalysis.ui.home.summary.state.MainStateEvent
import com.app.covidanalysis.ui.home.summary.state.MainStateEvent.*
import com.app.covidanalysis.ui.home.summary.state.MainViewState
import com.app.covidanalysis.util.AbsentLiveData
import com.app.covidanalysis.util.DataState

class SummaryViewModel @ViewModelInject constructor(val repository: SummaryRepository) : ViewModel() {

    private val _stateEvent: MutableLiveData<MainStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()

    val viewState: LiveData<MainViewState>
        get() = _viewState


    val dataState: LiveData<DataState<MainViewState>> = Transformations
            .switchMap(_stateEvent){stateEvent ->
                stateEvent?.let {
                    handleStateEvent(stateEvent)
                }
            }

    fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
        println("DEBUG: New StateEvent detected: $stateEvent")
        return when(stateEvent){

            is GetSummaryEvent -> {
                repository.getSummary()
            }

            is None ->{
                AbsentLiveData.create()
            }
        }
    }

    fun setSummaryData(summary: SummaryResponse){
        val update = getCurrentViewStateOrNew()
        update.summaryResponse = summary
        _viewState.value = update
    }

    fun getCurrentViewStateOrNew(): MainViewState {
        return viewState.value?.let {
            it
        }?: MainViewState()
    }

    fun setStateEvent(event: MainStateEvent){
        val state: MainStateEvent = event
        _stateEvent.value = state
    }
}