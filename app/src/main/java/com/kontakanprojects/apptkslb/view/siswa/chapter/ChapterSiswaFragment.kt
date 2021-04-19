package com.kontakanprojects.apptkslb.view.siswa.chapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentChapterSiswaBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultChapter
import com.kontakanprojects.apptkslb.model.siswa.ResultStateSiswa
import com.kontakanprojects.apptkslb.model.siswa.ResultsNilaiSiswa
import com.kontakanprojects.apptkslb.session.UserPreference
import com.kontakanprojects.apptkslb.utils.showMessage
import www.sanju.motiontoast.MotionToast

class ChapterSiswaFragment : Fragment() {

    private lateinit var binding: FragmentChapterSiswaBinding
    private val viewModel by viewModels<ChapterSiswaViewModel>()

    private var idMaapel = 0

    private lateinit var adapterChapter: ChapterSiswaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChapterSiswaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle()
        prepare()
        setAdapter()
    }

    private fun prepare() {
        idMaapel = ChapterSiswaFragmentArgs.fromBundle(arguments as Bundle).idMapel
        val idSiswa = UserPreference(requireContext()).getUser().idUser!!

        observeChapterByMapel(idMaapel)
        observeNilaiSiswa(idSiswa)
        observeStateChapterAndLevelSiswa(idSiswa)
    }

    private fun setAdapter() {
        // set adapter
        adapterChapter = ChapterSiswaAdapter()
        with(binding.rvChapterSiswa) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)

            this.adapter = adapterChapter
        }

        // response click per item chapter
        adapterChapter.setOnItemClickCallBack(object : ChapterSiswaAdapter.OnItemClickCallBack {
            override fun onItemClicked(
                resultChapter: ResultChapter,
                listChapterAndLevel: ResultStateSiswa?,
                resultsNilaiSiswa: ResultsNilaiSiswa?
            ) {
                if (resultsNilaiSiswa?.nilai != null) { // jika ada nilai siswa pindahkan intent ke result nilai siswa
                    if (resultChapter.idChapter == resultsNilaiSiswa.idChapter) { // jika id_chapter_result sama dengan dari siswa maka block kode dijalankan
                        if (listChapterAndLevel?.isReset == "Y") { // block kode ketika chapter direset ulang tampilkan intro soal

                            // arahkan ke halaman intro soal
                            var idChapterLevelSiswa = 0

                            if (resultChapter.idChapter == listChapterAndLevel.idChapter) {
                                idChapterLevelSiswa = listChapterAndLevel.idChapterLevelSiswa!!
                            } // jika tidak set default level

                            val toIntroSoal =
                                ChapterSiswaFragmentDirections.actionChapterSiswaFragmentToVideoIntroFragment()
                                    .apply {
                                        idChapter = resultChapter.idChapter
                                        this.idChapterLevelSiswa = idChapterLevelSiswa
                                        idNilai = resultsNilaiSiswa.idNilai!!
                                        this.idMapel = idMaapel
                                    }

                            findNavController().navigate(toIntroSoal)

                        } else { // block tidak di reset tampilkan halaman nilai
                            // assign to db
                            val toResult =
                                ChapterSiswaFragmentDirections.actionChapterSiswaFragmentToResultFragment()
                                    .apply {
                                        isFromHome = true
                                        idNilai = resultsNilaiSiswa.idNilai!!
                                        idChapter = resultsNilaiSiswa.idChapter
                                        idChapterLevelSiswa =
                                            listChapterAndLevel!!.idChapterLevelSiswa!!
                                    }
                            findNavController().navigate(toResult)
                        }
                    }
                } else { // jika siswa belum mendapatkan nilai pada chapter yang dikerjakan
//                    var idLevel = 1 // default level
                    var idChapterLevelSiswa = 0

                    if (resultChapter.idChapter == listChapterAndLevel?.idChapter) { // cek jika siswa pernah mengerjakan level set id level berdasarkan terakhir dikerjakan
//                        idLevel = listChapterAndLevel.idLevel!! + 1
                        idChapterLevelSiswa = listChapterAndLevel.idChapterLevelSiswa!!
                    } // jika tidak set default level

                    val toIntroSoal =
                        ChapterSiswaFragmentDirections.actionChapterSiswaFragmentToVideoIntroFragment().apply {
                            idChapter = resultChapter.idChapter
                            this.idChapterLevelSiswa = idChapterLevelSiswa
                            idMapel = idMaapel
                        }
                    findNavController().navigate(toIntroSoal)
                }
            }
        })
    }
    private fun observeNilaiSiswa(idSiswa: Int) {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.nilaiSiswa(idSiswa).observe(viewLifecycleOwner, { response ->
            binding.progressBar.visibility = View.GONE
            if (response != null) {
                if (response.status == 200) {
                    // set untuk cek di adapter view
                    if (response.results != null) {
                        // set all value to adapter
                        adapterChapter.setNilaiSiswa(response.results)
                    }
                }
            }
        })
    }

    private fun observeChapterByMapel(idMapel: Int) {
        // get all chapters
        with(binding) {
            progressBar.visibility = View.VISIBLE
            viewModel.chapters(idMapel).observe(viewLifecycleOwner, { response ->
                progressBar.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        // passing value to listchapters
                        adapterChapter.setData(response.results)
                    } else {
                        dataNotFound(response.message)
                    }
                } else {
                    dataNotFound()
                    showMessage(requireActivity(), getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    private fun observeStateChapterAndLevelSiswa(idSiswa: Int) {
        // ambil chapter yang telah selesai dan level dari siswa
        with(binding) {
            progressBar.visibility = View.VISIBLE
            viewModel.chapterAndLevelSiswa(idSiswa).observe(viewLifecycleOwner, { response ->
                progressBar.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        // set untuk cek di adapter view
                        val result = response.result
                        adapterChapter.setChapterAndLevelSiswa(result)
                    }
                }
            })
        }
    }

    private fun dataNotFound(message: String? = "Data tidak tersedia") {
        with(binding) {
            layoutChapterSiswa.visibility = View.GONE
            layoutNotFound.visibility = View.VISIBLE
            messageNotFound.text = message
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Pilih Chapter"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
}