package com.app.covidanalysis.repository

import androidx.lifecycle.LiveData
import com.app.covidanalysis.api.ApiService
import com.app.covidanalysis.model.TimelineResponse
import com.app.covidanalysis.ui.home.statistics.state.StatsStateEvent
import com.app.covidanalysis.ui.home.statistics.state.StatsViewState
import com.app.covidanalysis.ui.home.summary.state.MainViewState
import com.app.covidanalysis.util.ApiSuccessResponse
import com.app.covidanalysis.util.DataState
import com.app.covidanalysis.util.GenericApiResponse
import javax.inject.Inject

class TimelineRepository @Inject constructor(val apiService: ApiService){

    fun getTimeline(): LiveData<DataState<StatsViewState>> {
        return object: NetworkBoundResource<TimelineResponse, StatsViewState>(){

            override fun handleApiSuccessResponse(response: ApiSuccessResponse<TimelineResponse>) {
                result.value = DataState.data(
                        null,
                        StatsViewState(
                                 timelineResponse =  response.body
                        )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<TimelineResponse>> {
                return apiService.getTimeLine()
            }

        }.asLiveData()
    }
}