package com.kontakanprojects.apptkslb.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentChooseLoginBinding

class ChooseLoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentChooseLoginBinding

    companion object {
        const val ROLE_GURU = 1
        const val ROLE_SISWA = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnGuru.setOnClickListener(this@ChooseLoginFragment)
            btnSiswa.setOnClickListener(this@ChooseLoginFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnGuru -> {
                val toLogin =
                    ChooseLoginFragmentDirections.actionChooseLoginFragmentToLoginFragment(
                        ROLE_GURU
                    )
                findNavController().navigate(toLogin)
            }
            R.id.btnSiswa -> {
                val toLogin =
                    ChooseLoginFragmentDirections.actionChooseLoginFragmentToLoginFragment(
                        ROLE_SISWA
                    )
                findNavController().navigate(toLogin)
            }
        }
    }
}