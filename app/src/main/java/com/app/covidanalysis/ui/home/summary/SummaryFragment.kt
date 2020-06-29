package com.app.covidanalysis.ui.home.summary

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.covidanalysis.R
import com.app.covidanalysis.ui.DataStateListener
import com.app.covidanalysis.ui.home.summary.state.MainStateEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_summary.*
import javax.inject.Inject


@AndroidEntryPoint
class SummaryFragment : Fragment(R.layout.fragment_summary) {

    private val viewModel: SummaryViewModel by viewModels()
    private lateinit var dataStateHandler:DataStateListener
    @Inject lateinit var adapter: SummaryRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        triggerGetSummaryEvent()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        rvSummary.adapter = adapter
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

                    mainViewState.summaryResponse?.let{
                        // set summary data
                        viewModel.setSummaryData(it)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState ->
            viewState.summaryResponse?.let {summary ->
                // set summary to RecyclerView
                adapter.submitList(summary.countries)
                println("DEBUG: Setting summary to RecyclerView: $summary")
            }

        })
    }

    private fun triggerGetSummaryEvent(){
        viewModel.setStateEvent(MainStateEvent.GetSummaryEvent())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu,menu)
        val searchItem = menu.findItem(R.id.search)
        (searchItem.actionView as SearchView).setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }
}