package com.internshala.FoodRunnerApp.fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.internshala.FoodRunnerApp.R


class ProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtMobile: TextView
    lateinit var txtAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences =
            activity!!.getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
        val mobile_no = sharedPreferences.getString("user_mobile_number", null)
        val email = sharedPreferences.getString("user_email", null)
        val address = sharedPreferences.getString("user_address", null)
        val name = sharedPreferences.getString("user_name", null)

        txtName = view.findViewById(R.id.txtName)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtMobile = view.findViewById(R.id.txtPhoneNumber)
        txtAddress = view.findViewById(R.id.txtAddress)


        if (mobile_no != null && name != null && address != null && email != null) {
            txtAddress.text = address
            txtMobile.text = mobile_no
            txtEmail.text = email
            txtName.text = name
        } else {
            Toast.makeText(activity as Context, "Error Occurred!!", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}
