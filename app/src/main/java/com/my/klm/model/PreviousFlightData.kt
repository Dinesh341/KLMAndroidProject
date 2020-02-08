package com.my.klm.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PreviousFlightData (
	val id : String,
	val flightScheduleDate : String,
	val airlineCode : String,
	val flightNumber : Int
): Parcelable