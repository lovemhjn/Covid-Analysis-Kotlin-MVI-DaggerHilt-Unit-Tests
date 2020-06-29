package com.app.covidanalysis.ui.home.summary


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.covidanalysis.R
import com.app.covidanalysis.model.SummaryResponse.Country
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

    class BlogPostViewHolder
    constructor(
            itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Country) = with(itemView) {
            itemView.apply {

                tvCountryName.text = item.country
                tvConfirmedCases.text = item.totalConfirmed.toString()
                tvDeaths.text = item.totalDeaths.toString()
                tvRecoveredCases.text = item.totalRecovered.toString()

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


}


