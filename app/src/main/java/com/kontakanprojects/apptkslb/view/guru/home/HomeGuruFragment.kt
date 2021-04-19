package com.kontakanprojects.apptkslb.view.guru.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentHomeGuruBinding
import com.kontakanprojects.apptkslb.model.guru.ResultDetailGuru
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.session.UserPreference
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontakanprojects.apptkslb.view.auth.AuthActivity
import www.sanju.motiontoast.MotionToast
import java.util.*

class HomeGuruFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeGuruBinding
    private val viewModel by viewModels<HomeGuruViewModel>()

    private var resultDetailGuru: ResultDetailGuru? = null

    private val TAG = HomeGuruFragment::class.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeGuruBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeDetailGuru()

        with(binding) {
            imgProfile.setOnClickListener(this@HomeGuruFragment)
            lihatSiswa.setOnClickListener(this@HomeGuruFragment)
            uploadMateri.setOnClickListener(this@HomeGuruFragment)
            btnLogOut.setOnClickListener(this@HomeGuruFragment)
        }
    }

    private fun observeDetailGuru() {
        val idGuru = UserPreference(requireContext()).getUser().idUser!!
        with(binding) {
            progressBar.visibility = View.VISIBLE
            viewModel.detailGuru(idGuru).observe(viewLifecycleOwner, { response ->
                progressBar.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.result!!
                        resultDetailGuru = result
                        prepare(result)
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

    private fun prepare(result: ResultDetailGuru) {
        with(binding) {
            tvWelcome.text = getString(R.string.sayhello, getTimeDay(), result.nama)

            // set images
            Glide.with(requireContext())
                .load(ApiConfig.URL + result.fotoProfile)
                .error(R.drawable.no_profile_images)
                .circleCrop()
                .into(imgProfile)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgProfile -> {
                findNavController().navigate(R.id.action_homeGuruFragment_to_profileGuruFragment)
            }
            R.id.lihatSiswa -> {
                val toChooseSiswa = HomeGuruFragmentDirections.actionHomeGuruFragmentToChooseSiswaFragment().apply {
                    idKelas = resultDetailGuru?.idKelas ?: 0
                    idMapel = resultDetailGuru?.idMapel ?: 0
                }
                findNavController().navigate(toChooseSiswa)
            }
            R.id.uploadMateri -> {
                val toChapter = HomeGuruFragmentDirections.actionHomeGuruFragmentToChapterFragment().apply {
                    idMapel = resultDetailGuru?.idMapel ?: 0
                    isUpload = true
                }
                findNavController().navigate(toChapter)
            }
            R.id.btnLogOut -> showAlertDialog()
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