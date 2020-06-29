package com.app.covidanalysis.repository

import androidx.lifecycle.LiveData
import com.app.covidanalysis.api.ApiService
import com.app.covidanalysis.model.NewsResponse
import com.app.covidanalysis.model.SummaryResponse
import com.app.covidanalysis.ui.home.news.state.NewsViewState
import com.app.covidanalysis.ui.home.summary.state.MainViewState
import com.app.covidanalysis.util.ApiSuccessResponse
import com.app.covidanalysis.util.DataState
import com.app.covidanalysis.util.GenericApiResponse
import javax.inject.Inject

class NewsRepository @Inject constructor(val apiService: ApiService){

    fun getNews(date:String): LiveData<DataState<NewsViewState>> {
        return object: NetworkBoundResource<NewsResponse, NewsViewState>(){

            override fun handleApiSuccessResponse(response: ApiSuccessResponse<NewsResponse>) {
                result.value = DataState.data(
                        null,
                        NewsViewState(
                                newsResponse = response.body
                        )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<NewsResponse>> {
                return apiService.getNews("https://newsapi.org/v2/everything?q=COVID&from=2020-06-28&sortBy=publishedAt&apiKey=0e0d0b5370a54e20aae71d271f58e37d&pageSize=50&page=1")
            }

        }.asLiveData()
    }
}