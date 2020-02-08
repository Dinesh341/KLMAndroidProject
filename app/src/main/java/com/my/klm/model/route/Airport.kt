package com.my.klm.model.route

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Airport(

    val code: String?,
    val name: String?,
    val nameLangTranl: String?,
    val city: City?,
    val location: Location?,
    val places: Places?

) : Parcelable