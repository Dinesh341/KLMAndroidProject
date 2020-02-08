package com.my.klm.flightroute

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.my.klm.R
import com.my.klm.utils.PrefUtils
import com.my.klm.model.route.FlightRouteBase
import com.my.klm.viewmodels.FlightViewModel
import kotlinx.android.synthetic.main.flightroute.*
import java.text.SimpleDateFormat
import java.util.*

class FlightRouteActivity : AppCompatActivity() {

    private lateinit var flightViewModel: FlightViewModel
    private var isStartDate: Boolean = false
    private var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flightroute)
        setTitle(R.string.flight_route)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        flightViewModel =
            ViewModelProviders.of(this@FlightRouteActivity).get(FlightViewModel::class.java)
        searchroute.setOnClickListener {
            if (PrefUtils.isNetworkAvailable(this)) {
                searchFlightNumber()
            } else {
                PrefUtils.showErrorMessage(this,getString(R.string.internet_check))
            }
        }
        initTokenObservers()
        start_date.setOnClickListener {
            isStartDate = true
            datePicker()
        }
        end_date.setOnClickListener {
            isStartDate = false
            datePicker()
        }
    }

    /**
     * Get the Flight details based on the flight number and Date
     */
    private fun searchFlightNumber() {
        var tokenValue = PrefUtils.getStringPreference(this, getString(R.string.token))
        tokenValue = getString(R.string.bearer_token, tokenValue)
        val departingFrom = departing_from.text.toString()
        val arrivingAt = arriving_at.text.toString()
        if (departingFrom.isNotEmpty() && arrivingAt.isNotEmpty()) {
            if(!PrefUtils.validateFromToDate(start_date.text.toString()) && !PrefUtils.validateFromToDate(end_date.text.toString())) {
                val startDate = "${start_date.text}T10:00:00Z"
                val endDate = "${end_date.text}T19:00:00Z"
                progress_bar.visibility = View.VISIBLE
                flightViewModel.getAllRouteFlightList(
                    progress_bar,
                    departingFrom.toUpperCase(Locale.getDefault()),
                    arrivingAt.toUpperCase(Locale.getDefault()),
                    startDate,
                    endDate,
                    tokenValue.toString()
                )
            } else {
                PrefUtils.showErrorMessage(this,getString(R.string.error_date))
            }
        } else {
            if (departingFrom.isEmpty()) {
                PrefUtils.showErrorMessage(this,getString(R.string.error_departing))
            } else if (arrivingAt.isEmpty()) {
                PrefUtils.showErrorMessage(this,getString(R.string.error_arriving))
            }
        }
    }

    private fun datePicker() {
        DatePickerDialog(
            this@FlightRouteActivity,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()

    }

    // create an OnDateSetListener
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if (isStartDate) {
            start_date!!.text = sdf.format(cal.time)
        } else {
            end_date!!.text = sdf.format(cal.time)
        }
    }

    /**
     * Observer for Flight status details.
     */
    private fun initTokenObservers() {
        flightViewModel.getRouteFlightData().observe(this, Observer {
            it?.let {
                progress_bar.visibility = View.GONE
                sendData(it)
            }
        })
    }

    /**
     * Share the Flight data into Flight detail view
     */
    private fun sendData(flightRouteBase: FlightRouteBase) {
        val intent = Intent(this, FlightRouteList::class.java)
        intent.putExtra(getString(R.string.flightroutedata), flightRouteBase)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
