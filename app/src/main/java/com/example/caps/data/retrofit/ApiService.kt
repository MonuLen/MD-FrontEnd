package com.example.caps.data.retrofit

import com.example.caps.data.model.Monument
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("monuments")
    fun getMonuments(): Call<List<Monument>>

    @GET("monuments/{id}")
    fun getMonumentById(@Path("id") id: String): Call<Monument>
}
