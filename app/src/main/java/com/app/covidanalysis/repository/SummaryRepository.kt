package com.app.covidanalysis.repository

import androidx.lifecycle.LiveData
import com.app.covidanalysis.api.ApiService
import com.app.covidanalysis.ui.home.summary.state.MainViewState
import com.app.covidanalysis.model.SummaryResponse
import com.app.covidanalysis.util.ApiSuccessResponse
import com.app.covidanalysis.util.DataState
import com.app.covidanalysis.util.GenericApiResponse
import javax.inject.Inject

class SummaryRepository @Inject constructor(val apiService: ApiService){

    fun getSummary(): LiveData<DataState<MainViewState>> {
        return object: NetworkBoundResource<SummaryResponse, MainViewState>(){

            override fun handleApiSuccessResponse(response: ApiSuccessResponse<SummaryResponse>) {
                result.value = DataState.data(
                        null,
                        MainViewState(
                                summaryResponse = response.body
                        )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<SummaryResponse>> {
                return apiService.getSummary()
            }

        }.asLiveData()
    }
}