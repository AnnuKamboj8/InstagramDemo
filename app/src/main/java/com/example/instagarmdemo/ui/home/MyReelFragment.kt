package com.example.instagarmdemo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.instagarmdemo.databinding.FragmentMyReelBinding
import com.example.instagarmdemo.ui.Models.Reel
import com.example.instagarmdemo.ui.adapter.MyReelRvAdapter
import com.example.instagarmdemo.ui.mvvm.ProfileViewModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class MyReelFragment : Fragment() {
    private lateinit var binding: FragmentMyReelBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyReelBinding.inflate(inflater, container, false)

        var reelList = ArrayList<Reel>()
        var adapter = MyReelRvAdapter(requireContext(), reelList)
        binding.myReelRV.layoutManager =
            StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        binding.myReelRV.adapter = adapter


        viewModel.reelList.observe(viewLifecycleOwner) { reeList ->

            adapter.updateData(reeList)
        }
        return binding.root


    }
}


/*  Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.REEL).get()
           .addOnSuccessListener {
               var tempList = arrayListOf<Reel>()
               for (i in it.documents) {
                   var reel: Reel = i.toObject<Reel>()!!
                   tempList.add(reel)
               }
               reelList.addAll(tempList)
               adapter.notifyDataSetChanged()

           }*/