package com.example.instagarmdemo.ui.home

import MyPostFragment
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.FragmentProfileBinding
import com.example.instagarmdemo.ui.adapter.ViewPagerAdapter
import com.example.instagarmdemo.ui.login.LoginActivity
import com.example.instagarmdemo.ui.login.SignUpActivity
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.ui.mvvm.HomeViewModel
import com.example.instagarmdemo.ui.mvvm.ProfileViewModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {


    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.editProfileButton.setOnClickListener {
            val intent = Intent(activity, SignUpActivity::class.java)
            intent.putExtra("MODE", 1)
            activity?.startActivity(intent)
            activity?.finish()
        }
        binding.logOutButton.setOnClickListener {
            openAlertDialog()
        }
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.addFragments(MyPostFragment(), getString(R.string.my_post))
        viewPagerAdapter.addFragments(MyReelFragment(), getString(R.string.my_reel))
        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        return binding.root

    }

    private fun openAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(R.string.log_out)
        alertDialogBuilder.setMessage(R.string.log_out_message)

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()

            // Redirect to login screen
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("MODE", 0)
            startActivity(intent)
            activity?.finish()
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    override fun onStart() {
        super.onStart()
        viewModel.userProfile?.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.profileTextView.text = it.name
                binding.bioTextView.text = it.email
                it.image?.let { image ->
                    Glide.with(requireContext()).load(image).into(binding.profileImage)
                }
            }
        }
    }
}



/*     Firebase.firestore.collection(Keys.USER_NODE).document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                .addOnSuccessListener {
                    val user : UserModel =it.toObject<UserModel>()!!
                    binding.profileTextView.text = user.name
                    binding.bioTextView.text = user.email
                    if(!user.image.isNullOrEmpty()){
                        Glide.with(requireContext()).load(user.image).into(binding.profileImage)
                    }
                }*/