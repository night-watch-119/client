package com.sleep119.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.content.Intent



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var layout: LinearLayout
    private lateinit var layout1: LinearLayout
    private lateinit var layout2: LinearLayout
    private lateinit var imageButton: ImageButton



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        layout = view.findViewById(R.id.layout)

         layout1 = view.findViewById<LinearLayout>(R.id.layout1)

        layout1.setOnClickListener {
            // 클릭 이벤트 처리
            layout.visibility = View.VISIBLE
        }

         layout2 = view.findViewById<LinearLayout>(R.id.layout2)

        layout2.setOnClickListener {
            // 클릭 이벤트 처리
            layout.visibility = View.VISIBLE
        }
        val imageButton = view.findViewById<View>(R.id.imageButton)
        imageButton.setOnClickListener {
            val intent = Intent(activity, GuardianAdd::class.java)
            startActivity(intent) }

        val button2 = view.findViewById<View>(R.id.button2)
        button2.setOnClickListener {
            val intent = Intent(activity, UserModify::class.java)
            startActivity(intent) }



        val buttonmodify = view.findViewById<View>(R.id.buttonmodify)
        buttonmodify.setOnClickListener {
            val intent = Intent(activity, GuardianModify::class.java)
            startActivity(intent) }
        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
}





