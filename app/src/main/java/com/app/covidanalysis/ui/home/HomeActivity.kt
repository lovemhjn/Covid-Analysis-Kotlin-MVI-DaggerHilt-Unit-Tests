package com.app.covidanalysis.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.app.covidanalysis.R
import com.app.covidanalysis.ui.dialog.ProgressDialog
import com.app.covidanalysis.extensions.showToast
import com.app.covidanalysis.ui.DataStateListener
import com.app.covidanalysis.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(),DataStateListener {

    @Inject lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
    }


    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let{
            // Handle loading
            progressDialog.showProgress(dataState.loading)

            // Handle Message
            dataState.message?.let{ event ->
                event.getContentIfNotHandled()?.let { message ->
                    showToast(message)
                }
            }
        }
    }
}