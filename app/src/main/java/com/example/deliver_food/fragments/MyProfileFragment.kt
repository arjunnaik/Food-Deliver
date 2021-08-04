package com.example.deliver_food.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.deliver_food.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtUserName: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)
        sharedPreferences = context!!.getSharedPreferences(
            getString(R.string.preferences_file_name),
            Context.MODE_PRIVATE
        )
        txtUserName = view.findViewById(R.id.txtUserName)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)


        txtUserName.text = sharedPreferences.getString("name", "Username")
        txtMobileNumber.text = sharedPreferences.getString("mobile_number", "+91-9876543210")
        txtEmail.text = sharedPreferences.getString("email", "username@gmail.com")
        txtAddress.text = sharedPreferences.getString("address", "India")
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}