package com.app.covidanalysis.ui.home.news

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.covidanalysis.R
import com.app.covidanalysis.ui.DataStateListener
import com.app.covidanalysis.ui.home.news.state.NewsStateEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_news.*
import javax.inject.Inject


@AndroidEntryPoint
class NewsFragment : Fragment(R.layout.fragment_news) {

    private val viewModel: NewsViewModel by viewModels()
    private lateinit var dataStateHandler: DataStateListener
    @Inject lateinit var adapter: NewsRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        triggerGetNewsEvent()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        rvNews.adapter = adapter
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            dataStateHandler = context as DataStateListener
        }catch(e: ClassCastException){
            println("$context must implement DataStateListener")
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            dataStateHandler.onDataStateChange(dataState)
            // handle Data<T>
            dataState.data?.let{ event ->
                event.getContentIfNotHandled()?.let{ mainViewState ->

                    println("DEBUG: DataState: $mainViewState")

                    mainViewState.newsResponse?.let{
                        // set summary data
                        viewModel.setSummaryData(it)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState ->
            viewState.newsResponse?.let {news ->
                // set news to RecyclerView
                adapter.submitList(news.articles)
                println("DEBUG: Setting news to RecyclerView: $news")
            }

        })
    }

    private fun triggerGetNewsEvent(){
        viewModel.setStateEvent(NewsStateEvent.GetNewsEvent(""))
    }

}