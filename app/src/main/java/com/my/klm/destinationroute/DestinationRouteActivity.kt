package com.my.klm.destinationroute

import com.my.klm.model.destination.DestinationRouteBase
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.my.klm.R
import com.my.klm.utils.PrefUtils
import com.my.klm.viewmodels.FlightViewModel
import kotlinx.android.synthetic.main.destinationroute.*
import kotlinx.android.synthetic.main.flightroute.progress_bar
import java.util.*


class DestinationRouteActivity : AppCompatActivity() {

    private lateinit var flightViewModel: FlightViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.destinationroute)
        setTitle(R.string.destination_suggestions)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        flightViewModel =
            ViewModelProviders.of(this@DestinationRouteActivity).get(FlightViewModel::class.java)
        search_destination.setOnClickListener {
            if (PrefUtils.isNetworkAvailable(this)) {
                destonationSuggestion()
            } else {
                PrefUtils.showErrorMessage(this, getString(R.string.internet_check))
            }
        }
        initObservers()
    }

    /**
     * Get the Flight details based on the flight number and Date
     */
    private fun destonationSuggestion() {
        var tokenValue = PrefUtils.getStringPreference(this, getString(R.string.token))
        tokenValue = getString(R.string.bearer_token, tokenValue)
        val cities = cities.text.toString()
        if (cities.isNotEmpty()) {

            progress_bar.visibility = View.VISIBLE
            flightViewModel.getDestinationData(
                progress_bar, cities.toUpperCase(Locale.getDefault()),
                tokenValue.toString()
            )

        } else {
            PrefUtils.showErrorMessage(this, getString(R.string.error_cities))
        }

    }

    /**
     * Observer for Flight status details.
     */
    private fun initObservers() {
        flightViewModel.getDestinationData().observe(this, Observer {
            it?.let {
                progress_bar.visibility = View.GONE
                sendData(it)
            }
        })
    }

    /**
     * Share the Flight data into Flight detail view
     */
    private fun sendData(destinationBase: DestinationRouteBase) {
        val intent = Intent(this, DestinationRouteList::class.java)
        intent.putExtra(getString(R.string.destinationroutedata), destinationBase)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
