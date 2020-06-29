package com.app.covidanalysis.ui

import com.app.covidanalysis.util.DataState


interface DataStateListener {

    fun onDataStateChange(dataState: DataState<*>?)
}