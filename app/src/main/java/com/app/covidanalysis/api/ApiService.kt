package com.app.covidanalysis.api

import androidx.lifecycle.LiveData
import com.app.covidanalysis.constants.ServerConstants
import com.app.covidanalysis.model.NewsResponse
import com.app.covidanalysis.model.SummaryResponse
import com.app.covidanalysis.model.TimelineResponse
import com.app.covidanalysis.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {

    @GET(ServerConstants.GET_SUMMARY)
    fun getSummary(): LiveData<GenericApiResponse<SummaryResponse>>


    @GET
    fun getNews(@Url url:String): LiveData<GenericApiResponse<NewsResponse>>

    @GET
    fun getTimeLine(@Url url:String=ServerConstants.GET_TIMELINE): LiveData<GenericApiResponse<TimelineResponse>>
}