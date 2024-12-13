package com.example.caps.ui.detail

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caps.R
import com.example.caps.database.FavoriteMonument
import com.example.caps.data.model.Monument
import com.example.caps.ui.adapter.PhotoAdapter
import com.example.caps.ui.notifications.FavouriteViewModel
import com.example.caps.ui.notifications.FavouriteViewModelFactory
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailActivity : AppCompatActivity() {

    private val viewModel: FavouriteViewModel by viewModels {
        FavouriteViewModelFactory(application)
    }

    private var isFavorite: Boolean = false
    private lateinit var monument: Monument

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Ambil data dari Intent
        monument = intent.getParcelableExtra("EXTRA_MONUMENT")!!
        Log.d("DetailActivity", "Monument data: $monument")

        // Setup CollapsingToolbarLayout
        val collapsingToolbarLayout: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.title = monument.name

        // Atur gambar header
        val imageView: ImageView = findViewById(R.id.image)
        Glide.with(this)
            .load(monument.image_url)
            .placeholder(R.drawable.monas_icon_best_quality) // Gambar default sementara
            .error(R.drawable.ic_launcher_background) // Gambar error jika gagal memuat
            .into(imageView)

        // Atur elemen TextView
        val tvMonumentName: TextView = findViewById(R.id.tv_monument_name)
        val tvLocation: TextView = findViewById(R.id.tv_location_val)
        val tvDescription: TextView = findViewById(R.id.tv_description_val)
        val tvPerancang: TextView = findViewById(R.id.tv_perancang_val)
        val tvTanggal: TextView = findViewById(R.id.tv_tanggal_val)

        tvMonumentName.text = monument.name
        tvLocation.text = monument.location
        tvDescription.text = monument.history
        tvPerancang.text = monument.created_by.joinToString(", ") { it.ifEmpty { "Unknown" } }
        tvTanggal.text = monument.created_date.ifEmpty { "Unknown Date" }

        // Floating Action Button (FAB) untuk favorit
        val fab: FloatingActionButton = findViewById(R.id.fab)

        // Observasi apakah monumen ini ada di daftar favorit
        viewModel.favoriteMonuments.observe(this) { favorites ->
            isFavorite = favorites.any { it.id == monument.id }
            updateFabIcon(fab, isFavorite)
            Log.d("FavoriteMonuments", favorites.toString())
        }

        // Handle klik FAB
        fab.setOnClickListener {
            if (isFavorite) {
                viewModel.removeFavorite(toFavoriteMonument(monument))
            } else {
                viewModel.addFavorite(toFavoriteMonument(monument))
            }
        }

        // Setup RecyclerView untuk foto
        val photosRecyclerView: RecyclerView = findViewById(R.id.rv_photos)
        val photoAdapter = PhotoAdapter(monument.photos_url)

        photosRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = photoAdapter
        }

    }

    // Mengupdate ikon FAB berdasarkan status favorit
    private fun updateFabIcon(fab: FloatingActionButton, isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.baseline_favorite_24
        } else {
            R.drawable.baseline_favorite_border_24
        }
        fab.setImageResource(iconRes)
    }

    // Konversi Monument ke FavoriteMonument
    private fun toFavoriteMonument(monument: Monument): FavoriteMonument {
        return FavoriteMonument(
            id = monument.id,
            name = monument.name,
            location = monument.location,
            history = monument.history,
            image_url = monument.image_url,
            created_by = monument.created_by.ifEmpty { listOf("Unknown") }, // Berikan default jika kosong
            created_date = monument.created_date ?: "Unknown", // Berikan default jika null
            photos_url = monument.photos_url.ifEmpty { listOf("No Photos") } // Berikan default jika kosong
        )
    }

}
