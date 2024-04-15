package com.example.instagarmdemo.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.FragmentAddBinding
import com.example.instagarmdemo.databinding.FragmentMyPostBinding
import com.example.instagarmdemo.ui.post.PostActivity
import com.example.instagarmdemo.ui.post.ReelsActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddFragment : BottomSheetDialogFragment() {


    private lateinit var binding: FragmentAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentAddBinding.inflate(inflater,container,false)
        binding.postLl.setOnClickListener{
            activity?.startActivity(Intent(requireContext(),PostActivity::class.java))
             dismiss()
        }
        binding.reelsLl.setOnClickListener{
            activity?.startActivity(Intent(requireContext(),ReelsActivity::class.java))
              dismiss()
        }
        return binding.root
    }


}