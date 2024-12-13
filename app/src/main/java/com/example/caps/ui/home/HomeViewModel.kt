package com.example.caps.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.caps.data.model.Monument
import com.example.caps.data.retrofit.ApiConfig
import com.example.caps.data.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = ApiConfig.getApiService()

    // LiveData untuk menyimpan data Monuments
    private val _monuments = MutableLiveData<List<Monument>>()
    val monuments: LiveData<List<Monument>> get() = _monuments

    // Fungsi untuk mem-fetch data Monuments
    fun fetchMonuments() {
        apiService.getMonuments().enqueue(object : Callback<List<Monument>> {
            override fun onResponse(
                call: Call<List<Monument>>,
                response: Response<List<Monument>>
            ) {
                if (response.isSuccessful) {
                    // Mengupdate data di LiveData ketika response sukses
                    _monuments.value = response.body()
                } else {
                    // Tangani jika response tidak sukses
                    _monuments.value = emptyList()  // Atau kamu bisa mengatur pesan error di sini
                }
            }

            override fun onFailure(call: Call<List<Monument>>, t: Throwable) {
                // Tangani kegagalan API
                _monuments.value = emptyList()  // Atau tampilkan pesan error sesuai kebutuhan
            }
        })
    }
}
