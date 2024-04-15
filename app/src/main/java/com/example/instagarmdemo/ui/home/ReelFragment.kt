package com.example.instagarmdemo.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.FragmentHomeBinding
import com.example.instagarmdemo.databinding.FragmentReelBinding
import com.example.instagarmdemo.ui.Models.Reel
import com.example.instagarmdemo.ui.adapter.ReelRvAdapter
import com.example.instagarmdemo.ui.mvvm.HomeViewModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class ReelFragment : Fragment() {
    private lateinit var binding: FragmentReelBinding
    lateinit var adapter: ReelRvAdapter
    private val viewModel: HomeViewModel by viewModels()
    var reelList = ArrayList<Reel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReelBinding.inflate(inflater, container, false)
        adapter = ReelRvAdapter(requireContext(), reelList)
        binding.viewPager.adapter = adapter


        viewModel.loadReels(
            onSuccess = { reels ->
                adapter.reelList = ArrayList(reels)
                reelList.reverse()
                adapter.notifyDataSetChanged()
            },
            onFailure = {
                // Handle failure
            }
        )
        return binding.root
    }

}

/*        Firebase.firestore.collection(Keys.REEL).get().addOnSuccessListener {

           var tempList = arrayListOf<Reel>()
           reelList.clear()
           for (i in it.documents) {
               var reel: Reel = i.toObject<Reel>()!!
               tempList.add(reel)
           }
           reelList.addAll(tempList)
           reelList.reverse()
           adapter.notifyDataSetChanged()

           }*/