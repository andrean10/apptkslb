package com.kontakanprojects.apptkslb.view.guru.chapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentChapterBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultChapter
import com.kontakanprojects.apptkslb.utils.showMessage
import www.sanju.motiontoast.MotionToast

class ChapterFragment : Fragment() {

    private lateinit var binding: FragmentChapterBinding
    private val viewModel by viewModels<ChapterViewModel>()
    private lateinit var chaptersAdapter: ChaptersAdapter
    private var idMapel = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChapterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = ChapterFragmentArgs.fromBundle(arguments as Bundle)
        idMapel = args.idMapel
        val isUpload = args.isUpload

        with(binding) {
            val titleToolbar: String = if (isUpload) {
                "Pilih Chapter"
            } else {
                "Data Chapter"
            }

            setToolbarTitle(toolbar, titleToolbar)
            setAdapter()
            observe()
        }
    }

    private fun observe() {
        val idMapel = ChapterFragmentArgs.fromBundle(arguments as Bundle).idMapel

        viewModel.getChapters(idMapel)
        viewModel.chapters.observe(viewLifecycleOwner, { response ->
            binding.pbLoadingChapter.visibility = View.GONE
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    chaptersAdapter.setData(result)
                } else {
                    dataNotFound()
                }
            } else {
                showMessage(
                    requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR
                )
            }
        })

        viewModel.isLoading().observe(viewLifecycleOwner, { state ->
            with(binding) {
                if (state) {
                    pbLoadingChapter.visibility = View.VISIBLE
                } else {
                    pbLoadingChapter.visibility = View.GONE
                }
            }
        })
    }

    private fun setAdapter() {
        // set adapter
        chaptersAdapter = ChaptersAdapter()
        with(binding.rvChapter) {
            layoutManager = LinearLayoutManager(requireContext())
            val dividerItemDecoration = DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
            setHasFixedSize(true)
            this.adapter = chaptersAdapter
        }

        chaptersAdapter.setOnItemClickCallBack(object : ChaptersAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultChapter: ResultChapter) {
                // move intent and send id chapter
                val toLevel =
                    ChapterFragmentDirections.actionChapterFragmentToChooseUploadFragment()
                toLevel.idChapter = resultChapter.idChapter
                findNavController().navigate(toLevel)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dataNotFound() {
        with(binding) {
            foundLayout.visibility = View.GONE
            notFoundLayout.visibility = View.VISIBLE
        }
    }

    private fun setToolbarTitle(toolbar: Toolbar, actionBarTitle: String?) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = actionBarTitle
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

}