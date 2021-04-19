package com.kontakanprojects.apptkslb.view.guru.siswa.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentDetailSiswaBinding
import com.kontakanprojects.apptkslb.model.siswa.ResultDetailSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResultStateSiswa
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.utils.showMessage
import www.sanju.motiontoast.MotionToast

class DetailSiswaFragment : Fragment() {

    private lateinit var binding: FragmentDetailSiswaBinding
    private var adapterDetailSiswa = DetailSiswaAdapter()
    private val viewModel by viewModels<DetailSiswaViewModel>()

    private var idSiswa = 0
    private var resultDetailSiswa: ResultDetailSiswa? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailSiswaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle()

        // ambil data
        val args = DetailSiswaFragmentArgs.fromBundle(arguments as Bundle).dataSiswa
        idSiswa = args.id
        val idMapel = args.idMapel ?: 0

        observeDetailSiswa(idSiswa)

        with(binding) {
            // set adapter
            adapterDetailSiswa = DetailSiswaAdapter()
            with(rvNilaiSiswa) {
                layoutManager = LinearLayoutManager(requireContext())
                itemAnimator = DefaultItemAnimator()
                setHasFixedSize(true)
                this.adapter = adapterDetailSiswa
            }

            observeNilaiSiswa(idMapel, idSiswa)
            observeStateLevelSiswa(idMapel, idSiswa)
            observeNilaiRataRata(idSiswa, idMapel)

            btnEditSiswa.setOnClickListener { moveEditProfile() }
        }
    }

    private fun moveEditProfile() {
        if (resultDetailSiswa != null) {
            val toManageSiswa = DetailSiswaFragmentDirections.
            actionDetailSiswaFragmentToManageSiswaFragment(resultDetailSiswa!!)
            findNavController().navigate(toManageSiswa)
        }
    }

    private fun observeDetailSiswa(idSiswa: Int) {
        viewModel.detailSiswa(idSiswa).observe(viewLifecycleOwner, { response ->
            binding.pbLoadingDetailSiswa.visibility = View.GONE
            if (response != null) {
                if (response.status == 200) {
                    val result = response.result!!
                    resultDetailSiswa = response.result
                    prepareSiswa(result)
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

    private fun prepareSiswa(result: ResultDetailSiswa) {
        with(binding) {
            // set images
            Glide.with(requireContext())
                .load(ApiConfig.URL + result.fotoProfile)
                .error(R.drawable.no_profile_images)
                .into(imgSiswa)
            tvNamaSiswa.text = result.nama
            tvKelasSiswa.text = result.namaKelas
        }
    }

    private fun observeNilaiSiswa(idMapel: Int, idSiswa: Int) {
        with(binding) {
            pbNilaiSiswa.visibility = View.VISIBLE
            viewModel.nilaiSiswa(idMapel, idSiswa).observe(viewLifecycleOwner, { response ->
                pbNilaiSiswa.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        adapterDetailSiswa.setNilaiSiswa(result)

                        isDataAvailable(true)
                    } else {
                        isDataAvailable(false, response.message)
                    }
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR
                    )
                }
            })
        }
    }

    private fun observeStateLevelSiswa(idMapel: Int, idSiswa: Int) {
        with(binding) {
            pbLoadingDetailSiswa.visibility = View.VISIBLE
            // mengambil chapter dan level terbaru
            viewModel.stateLevelSiswa(idMapel, idSiswa).observe(viewLifecycleOwner, { response ->
                pbLoadingDetailSiswa.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        // set untuk cek di adapter view
                        if (response.result != null) {
                            val result = response.result.last()
                            observeMaxLevel(result)
                        }
                    } else {
                        binding.tvLevelSiswa.text =
                            getString(R.string.level_detail_siswa, "Chapter 1", "Level 1")
                    }
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR
                    )
                }
            })
        }
    }

    private fun observeMaxLevel(resultStateSiswa: ResultStateSiswa) {
        with(binding) {
            pbLoadingDetailSiswa.visibility = View.VISIBLE
            viewModel.maxLevel(resultStateSiswa.idChapter!!)
                .observe(viewLifecycleOwner, { response ->
                    pbLoadingDetailSiswa.visibility = View.GONE
                    if (response != null) {
                        if (response.status == 200) {
                            // set untuk cek di adapter view
                            if (response.result != null) {
                                val result = response.result

                                val chapterNow = result.namaChapter
                                val levelNow: String =
                                    if (resultStateSiswa.isReset == "Y") { // ambil level terakhir jika user sudah menyelesaikan semua level
                                        result.level!!
                                    } else {
                                        resultStateSiswa.level!!
                                    }

                                tvLevelSiswa.text =
                                    getString(R.string.level_detail_siswa, chapterNow, levelNow)
                            }
                        } else {
                            binding.tvLevelSiswa.text =
                                getString(R.string.level_detail_siswa, "Chapter 1", resultStateSiswa.level)
                        }
                    } else {
                        showMessage(
                            requireActivity(),
                            getString(R.string.failed),
                            style = MotionToast.TOAST_ERROR
                        )
                    }
                })
        }
    }

    private fun observeNilaiRataRata(idSiswa: Int, idMapel: Int) {
        with(binding) {
            pbLoadingDetailSiswa.visibility = View.VISIBLE
            // mengambil chapter dan level terbaru
            viewModel.nilaiRataRata(idSiswa, idMapel).observe(viewLifecycleOwner, { response ->
                pbLoadingDetailSiswa.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        layoutNilaiRataSiswa.visibility = View.VISIBLE
                        tvNilaiRataRata.text = response.results.toString()
                    }
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR
                    )
                }
            })
        }
    }

    private fun deleteSiswa(idSiswa: Int) {
        with(binding) {
            pbLoadingDetailSiswa.visibility = View.VISIBLE
            // mengambil chapter dan level terbaru
            viewModel.deleteSiswa(idSiswa).observe(viewLifecycleOwner, { response ->
                pbLoadingDetailSiswa.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) { // berhasil hapus siswa
                        findNavController().navigateUp()

                        showMessage(requireActivity(), getString(R.string.success),
                            response.message, MotionToast.TOAST_SUCCESS)
                    } else {
                        showMessage(requireActivity(), getString(R.string.failed), response.message,
                            MotionToast.TOAST_ERROR)
                    }
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR
                    )
                }
            })
        }
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.delete_siswa_title))
            .setMessage(getString(R.string.delete_siswa_body))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                deleteSiswa(idSiswa)
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun isDataAvailable(state: Boolean, message: String? = null) {
        with(binding) {
            if (state) {
                rvNilaiSiswa.visibility = View.VISIBLE
                notFoundLayout.visibility = View.GONE
            } else {
                rvNilaiSiswa.visibility = View.GONE
                notFoundLayout.visibility = View.VISIBLE
                messageNotFound.text = message
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.delete -> showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Detail Siswa"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
}