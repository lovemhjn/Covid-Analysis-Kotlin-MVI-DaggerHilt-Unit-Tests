package com.app.covidanalysis.ui.home.statistics

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.covidanalysis.R
import com.app.covidanalysis.model.TimelineResponse
import com.app.covidanalysis.ui.DataStateListener
import com.app.covidanalysis.ui.home.statistics.state.StatsStateEvent
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.util.*


@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var dataStateHandler: DataStateListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        triggerGetSummaryEvent()
    }




    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("$context must implement DataStateListener")
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            dataStateHandler.onDataStateChange(dataState)
            // handle Data<T>
            dataState.data?.let { event ->
                event.getContentIfNotHandled()?.let { statsViewState ->

                    println("DEBUG: DataState: $statsViewState")

                    statsViewState.timelineResponse?.let {
                        // set summary data
                        viewModel.setTimelineData(it)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.timelineResponse?.let { stats ->
                // set summary to RecyclerView
                initLineChart(lineChart,stats)
                initLineChart(lineChartDeaths,stats)
                initLineChart(lineChartRecovered,stats)
                println("DEBUG: Setting summary to RecyclerView: $stats")
            }

        })
    }

    private fun triggerGetSummaryEvent() {
        viewModel.setStateEvent(StatsStateEvent.GetTimelineEvent())
    }

    private fun initLineChart(chart:LineChart,stats: TimelineResponse) {
        // no description text

        // no description text
        chart.description.isEnabled = false

        // enable touch gestures

        // enable touch gestures
        chart.setTouchEnabled(true)

        chart.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true)

        // set an alternative background color

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE)

        chart.animateX(1500)

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l: Legend = chart.getLegend()

        // modify the legend ...

        // modify the legend ...
        l.form = LegendForm.LINE
        l.textSize = 11f
        l.textColor = Color.BLACK
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
//        l.setYOffset(11f);

        //        l.setYOffset(11f);
        val xAxis: XAxis = chart.xAxis
        xAxis.textSize = 11f
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        //leftAxis.axisMaximum = 200f
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true

        val rightAxis: YAxis = chart.axisRight
        rightAxis.textColor = Color.RED
        //rightAxis.axisMaximum = 900f
        rightAxis.axisMinimum = -200f
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawZeroLine(false)
        rightAxis.isGranularityEnabled = false
        rightAxis.setDrawAxisLine(false)

        setData(chart,stats)
    }

    private fun setData(chart:LineChart,stats: TimelineResponse) {
        stats.reverse()
        var label = ""
        val values1 = ArrayList<Entry>()
        for (i in 0 until stats.size) {
            label = when (chart.id) {
                lineChart.id -> {
                    values1.add(Entry(i.toFloat(), stats[i].total_cases.toFloat()))
                    "Total Cases"
                }
                lineChartDeaths.id -> {
                    values1.add(Entry(i.toFloat(), stats[i].total_deaths.toFloat()))
                    "Total Deaths"
                }
                else -> {
                    values1.add(Entry(i.toFloat(), stats[i].total_recovered.toFloat()))
                    "Total Recovered"
                }
            }
        }
        val set1: LineDataSet

        if (chart.data != null &&
                chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet

            set1.values = values1
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values1, label)
            set1.axisDependency = AxisDependency.LEFT
            set1.color = ColorTemplate.getHoloBlue()
            set1.setCircleColor(Color.BLACK)
            set1.lineWidth = 2f
            set1.circleRadius = 3f
            set1.fillAlpha = 65
            set1.fillColor = ColorTemplate.getHoloBlue()
            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.setDrawCircleHole(false)


            // create a data object with the data sets
            val data = LineData(set1)
            data.setValueTextColor(Color.BLACK)
            data.setValueTextSize(9f)

            // set data
            chart.data = data
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()

        }
    }


}