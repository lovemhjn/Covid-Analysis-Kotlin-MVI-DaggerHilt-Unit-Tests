package com.app.covidanalysis.ui.home.summary


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.covidanalysis.R
import com.app.covidanalysis.model.AnalyticsChartData
import com.app.covidanalysis.model.SummaryResponse.Country
import com.app.covidanalysis.model.TimelineResponse
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.item_summary.view.*
import java.util.*
import javax.inject.Inject


class SummaryRecyclerAdapter @Inject constructor() :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    lateinit var allCountry: List<Country>

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Country>() {

        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.country == newItem.country
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return BlogPostViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_summary,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BlogPostViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Country>) {
        allCountry = list
        differ.submitList(list)
    }

    inner class BlogPostViewHolder
    constructor(
            itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Country) = with(itemView) {
            itemView.apply {

                tvCountryName.text = item.country
                tvConfirmedCases.text = item.totalConfirmed.toString()
                tvDeaths.text = item.totalDeaths.toString()
                tvRecoveredCases.text = item.totalRecovered.toString()
                initPieChart(pieChart,item)

                setOnClickListener {

                }

            }
        }
    }


    interface Interaction {
        fun onItemSelected(position: Int, item: Country)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredCountries: MutableList<Country> = ArrayList()

                if (constraint.isNullOrEmpty()) {
                    filteredCountries.addAll(allCountry)
                } else {
                    val filterPattern: String = constraint.toString().toLowerCase(Locale.getDefault()).trim { it <= ' ' }

                    allCountry.forEach {
                        if (it.country.toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                            filteredCountries.add(it)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredCountries
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                differ.submitList(results?.values as List<Country>)

            }

        }
    }

    private fun initPieChart(pieChart: PieChart, item: Country) {

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart.dragDecelerationFrictionCoef = 0.95f


        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f

        pieChart.setDrawCenterText(true)

        pieChart.rotationAngle = 0f
        // enable rotation of the pieChart by touch
        // enable rotation of the pieChart by touch
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true

        // add a selection listener
        //pieChart.setOnChartValueSelectedListener(this)


        pieChart.animateY(1400, Easing.EaseInOutQuad)
        // pieChart.spin(2000, 0, 360);
        pieChart.legend.isEnabled = false


        // entry label styling
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)

        makeDataForChart(pieChart,item)

    }

    private fun makeDataForChart(pieChart: PieChart, item: Country) {
        val filteredList = ArrayList<AnalyticsChartData>()

        filteredList.add(AnalyticsChartData("Deaths", item.totalDeaths))
        filteredList.add(AnalyticsChartData("Recovered", item.totalRecovered))
        filteredList.add(AnalyticsChartData("Active", item.totalConfirmed-item.totalDeaths-item.totalRecovered))



        setChartData(pieChart,filteredList, item.totalConfirmed)
    }

    private fun setChartData(
            pieChart: PieChart,
            filteredList: ArrayList<AnalyticsChartData>,
            total: Int
    ) {
        val entries = ArrayList<PieEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (i in 0 until filteredList.size) {
            entries.add(
                    PieEntry(
                            filteredList[i].count.toFloat(),
                            filteredList[i].name)
            )

        }
        pieChart.centerText = "Confirmed: $total"
        val dataSet = PieDataSet(entries, "Cases Stats")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colorsList = ArrayList<Int>()

        for (c in ColorTemplate.COLORFUL_COLORS) colorsList.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS) colorsList.add(c)

        for (c in ColorTemplate.PASTEL_COLORS) colorsList.add(c)

        for (c in ColorTemplate.MATERIAL_COLORS) colorsList.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorsList.add(c)

        colorsList.remove(Color.rgb(255, 247, 140))
        colorsList.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colorsList
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)
        pieChart.data = data

        // undo all highlights
        pieChart.highlightValues(null)
        pieChart.invalidate()
    }


}


