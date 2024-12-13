package com.example.caps.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caps.databinding.FragmentFavouriteBinding
import com.example.caps.ui.adapter.FavouriteAdapter
import com.example.caps.ui.detail.DetailActivity

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private val favouriteViewModel: FavouriteViewModel by viewModels {
        FavouriteViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        val favouriteAdapter = FavouriteAdapter { favouriteItem ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("EXTRA_MONUMENT", favouriteItem)
            }
            startActivity(intent)
        }

        binding.rvFavouriteFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavouriteFragment.adapter = favouriteAdapter

        // Observe LiveData
        favouriteViewModel.favoriteMonuments.observe(viewLifecycleOwner) { favourites ->
            favouriteAdapter.submitList(favourites)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
