package com.kontakanprojects.apptkslb.view.siswa.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentHomeSiswaBinding
import com.kontakanprojects.apptkslb.model.mapel.ResultsMapel
import com.kontakanprojects.apptkslb.model.siswa.ResultDetailSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.session.UserPreference
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontakanprojects.apptkslb.view.auth.AuthActivity
import www.sanju.motiontoast.MotionToast
import java.util.*

class HomeSiswaFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeSiswaBinding
    private val viewModel by viewModels<HomeSiswaViewModel>()

    private lateinit var resultDetailSiswa: ResultDetailSiswa
    private lateinit var adapterSiswa: HomeSiswaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeSiswaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepare()
        setAdapter()

        with(binding) {
            btnLogOut.setOnClickListener(this@HomeSiswaFragment)
            imgProfile.setOnClickListener(this@HomeSiswaFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgProfile -> moveToProfile()
            R.id.btnLogOut -> showAlertDialog()
        }
    }

    private fun moveToProfile() {
        findNavController().navigate(R.id.action_homeFragment_to_profileSiswaFragment)
    }

    private fun setAdapter() {
        // set adapter
        adapterSiswa = HomeSiswaAdapter()
        with(binding.rvMapelSiswa) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
            this.adapter = adapterSiswa
        }

        // response click per item chapter
        adapterSiswa.setOnItemClickCallBack(object : HomeSiswaAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultMapel: ResultsMapel) {
                val toChapterSiswa =
                    HomeSiswaFragmentDirections.actionHomeFragmentToChapterSiswaFragment()
                toChapterSiswa.idMapel = resultMapel.idMapel!!

                findNavController().navigate(toChapterSiswa)
            }
        })
    }

    private fun prepare() {
        // get id user in sharedpreferences
        val idSiswa = UserPreference(requireContext()).getUser().idUser!!

        observeDetailSiswa(idSiswa)
    }

    private fun observeMapel() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            viewModel.mapel(resultDetailSiswa.idKelas ?: 0)
                .observe(viewLifecycleOwner, { response ->
                    progressBar.visibility = View.GONE
                    if (response != null) {
                        if (response.status == 200) {
                            if (response.results != null) {
                                // set all value to adapter
                                adapterSiswa.setData(response.results)
                            }
                        }
                    } else {
                        showMessage(requireActivity(), "Gagal ☹️", style = MotionToast.TOAST_ERROR)
                    }
                })
        }

    }

    private fun observeDetailSiswa(idSiswa: Int) {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            viewModel.detailSiswa(idSiswa).observe(viewLifecycleOwner, { response ->
                progressBar.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        resultDetailSiswa = response.result!!

                        // set images
                        Glide.with(requireContext())
                            .load(ApiConfig.URL + resultDetailSiswa.fotoProfile)
                            .fallback(R.drawable.no_profile_images)
                            .error(R.drawable.no_profile_images)
                            .circleCrop()
                            .into(imgProfile)

                        tvWelcome.text =
                            getString(R.string.sayhello, getTimeDay(), resultDetailSiswa.nama)

                        observeMapel()
                    } else {
                        showMessage(
                            requireActivity(),
                            "Gagal ☹️",
                            response.message,
                            MotionToast.TOAST_ERROR
                        )
                    }
                } else {
                    showMessage(requireActivity(), "Gagal ☹️", style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.log_out))
            .setMessage(getString(R.string.message_log_out))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                // clear all preferences
                UserPreference(requireContext()).apply {
                    removeLogin()
                    removeUser()
                }

                startActivity(Intent(requireContext(), AuthActivity::class.java))
                activity?.finish()
            }
            .setNegativeButton("Tidak") { dialog, i ->
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun getTimeDay(): String {
        val c: Calendar = Calendar.getInstance()
        return when (c.get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Pagi"
            in 12..15 -> "Siang"
            in 16..18 -> "Sore"
            in 19..23 -> "Malam"
            in 0..4 -> "Malam"
            else -> "Waktu tidak terdefinisi"
        }
    }
}