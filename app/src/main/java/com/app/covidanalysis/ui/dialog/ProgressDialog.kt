package com.app.covidanalysis.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import com.app.covidanalysis.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ProgressDialog @Inject constructor(@ActivityContext context:Context):Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_progress_bar, FrameLayout(context)))
        this.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.window?.setGravity(Gravity.CENTER_VERTICAL)
        this.window?.setBackgroundDrawableResource(R.color.transparent)
        this.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    fun showProgress(showProgress: Boolean){
        if(showProgress) show() else dismiss()
    }

    override fun onBackPressed() = Unit
}