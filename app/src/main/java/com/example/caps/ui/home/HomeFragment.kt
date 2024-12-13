package com.example.caps.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caps.R
import com.example.caps.data.model.Monument
import com.example.caps.databinding.FragmentHomeBinding
import com.example.caps.ui.adapter.FavouriteAdapter
import com.example.caps.ui.adapter.MonumentAdapter
import com.example.caps.ui.detail.DetailActivity
import com.example.caps.ui.notifications.FavouriteViewModel
import com.example.caps.ui.notifications.FavouriteViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private val favouriteViewModel: FavouriteViewModel by viewModels {
        FavouriteViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel.fetchMonuments()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Adapter untuk daftar Discover
        val discoverAdapter = MonumentAdapter<Monument> { monument ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("EXTRA_MONUMENT", monument)
            }
            startActivity(intent)
        }

        // Adapter untuk daftar Favourite
        val favouriteAdapter = FavouriteAdapter { monument ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("EXTRA_MONUMENT", monument)
            }
            startActivity(intent)
        }

        // Setup RecyclerView untuk Discover
        binding.rvUpcoming.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcoming.adapter = discoverAdapter

        // Setup RecyclerView untuk Favourite
        binding.rvFavourite.layoutManager = LinearLayoutManager(context)
        binding.rvFavourite.adapter = favouriteAdapter

        // Observe data untuk Discover
        homeViewModel.monuments.observe(viewLifecycleOwner, Observer { monuments ->
            binding.loadingProgressBar.visibility = View.GONE
            discoverAdapter.submitList(monuments)
        })


        // Observe data untuk Favourite
        favouriteViewModel.favoriteMonuments.observe(viewLifecycleOwner, Observer { favorites ->
            favouriteAdapter.submitList(favorites)
        })

        // Tombol navigasi
        binding.viewMore.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_favouriteFragment)
        }

        binding.findMore.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        }

        // Swipe down refresh
        binding.swipeRefreshLayout.setOnRefreshListener {

            discoverAdapter.submitList(emptyList())

            favouriteAdapter.submitList(emptyList())

            binding.loadingProgressBar.visibility = View.VISIBLE

            refreshData()
        }

        return binding.root
    }

    private fun refreshData() {
        homeViewModel.fetchMonuments()
        favouriteViewModel.favoriteMonuments.observe(viewLifecycleOwner, Observer { monuments ->
            (binding.rvFavourite.adapter as FavouriteAdapter).submitList(monuments)
        })
        Handler(Looper.getMainLooper()).postDelayed({

            binding.swipeRefreshLayout.isRefreshing = false
        }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
