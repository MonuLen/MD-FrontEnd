package com.example.caps.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Monument(
    val id: String,
    val name: String,
    val location: String,
    val history: String,
    val image_url: String,
    val created_by: List<String>,
    val created_date: String,
    val photos_url: List<String>
) : Parcelable
{
}