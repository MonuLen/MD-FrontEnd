package com.example.caps.ui

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.example.caps.data.model.Monument
import com.example.caps.data.retrofit.ApiConfig
import com.example.caps.databinding.ActivityDisplayImageBinding
import com.example.caps.ui.detail.DetailActivity
import com.example.caps.ui.helper.PredictionHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DisplayImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayImageBinding
    private lateinit var predictionHelper: PredictionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Confirm Image"

        // Inisialisasi PredictionHelper
        predictionHelper = PredictionHelper(
            context = this,
            onResult = { name, confidence ->
                // Tampilkan hasil prediksi
                Toast.makeText(this, "Prediksi: $name ($confidence%)", Toast.LENGTH_LONG).show()

                // Tampilkan loader
                binding.progressBar.visibility = View.VISIBLE

                // Ambil data dari API
                val apiService = ApiConfig.getApiService()
                apiService.getMonuments().enqueue(object : Callback<List<Monument>> {
                    override fun onResponse(
                        call: Call<List<Monument>>,
                        response: Response<List<Monument>>
                    ) {
                        binding.progressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            val monuments = response.body()
                            val selectedMonument = monuments?.find { it.name.equals(name, ignoreCase = true) }

                            if (selectedMonument != null) {
                                // Pindah ke DetailActivity
                                val intent = Intent(this@DisplayImageActivity, DetailActivity::class.java)
                                intent.putExtra("EXTRA_MONUMENT", selectedMonument)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@DisplayImageActivity,
                                    "Monumen tidak ditemukan!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@DisplayImageActivity,
                                "Gagal mengambil data: ${response.message()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<List<Monument>>, t: Throwable) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@DisplayImageActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            },
            onError = { error ->
                // Tampilkan error
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            },
            onDownloadSuccess = {
                binding.analyze.isEnabled = true
            }
        )

        showImage()

        binding.cancel.setOnClickListener {
            finish()
        }

        binding.analyze.setOnClickListener {
            val drawable = binding.previewImageView.drawable
            if (drawable != null) {
                val bitmap = (drawable as BitmapDrawable).bitmap
                predictionHelper.predict(bitmap)
            } else {
                Toast.makeText(this, "No image to analyze", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImage() {
        val imageUriString = intent.getStringExtra(CameraActivity.EXTRA_GALLERY_IMAGE)
        val cameraUriString = intent.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)

        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.previewImageView.setImageURI(imageUri)
        } else if (cameraUriString != null) {
            val cameraUri = Uri.parse(cameraUriString)
            binding.previewImageView.setImageURI(cameraUri)
        } else {
            Toast.makeText(this, "No image URI found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        predictionHelper.close()
    }
}
