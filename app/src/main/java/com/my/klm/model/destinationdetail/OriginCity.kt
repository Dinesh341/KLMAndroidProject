package com.my.klm.model.destinationdetail

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class OriginCity(

    val code: String,
    val label: String
) : Parcelable