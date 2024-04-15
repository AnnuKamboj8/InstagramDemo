import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.instagarmdemo.databinding.FragmentMyPostBinding
import com.example.instagarmdemo.ui.Models.Post
import com.example.instagarmdemo.ui.adapter.MyPostRvAdapter
import com.example.instagarmdemo.ui.mvvm.ProfileViewModel

class MyPostFragment : Fragment() {
    private lateinit var binding: FragmentMyPostBinding
    private val viewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)
        val postList = ArrayList<Post>()
        val adapter = MyPostRvAdapter(requireContext(), postList)
        binding.myPostRV.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        binding.myPostRV.adapter = adapter


        viewModel.postList.observe(viewLifecycleOwner) { postList ->

            adapter.updateData(postList)
        }


        return binding.root
    }
}


/* // Retrieve posts of the currently signed-in user
       Firebase.firestore.collection(Keys.POST)
           .whereEqualTo("uid", FirebaseAuth.getInstance().currentUser!!.uid)
           .get()
           .addOnSuccessListener { postSnapshot ->
               val tempList = arrayListOf<Post>()
               for (i in postSnapshot.documents) {
                   val post: Post = i.toObject<Post>()!!
                   tempList.add(post)
               }
               postList.addAll(tempList)*/