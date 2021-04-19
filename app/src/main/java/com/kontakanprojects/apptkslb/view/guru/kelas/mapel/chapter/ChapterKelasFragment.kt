package com.kontakanprojects.apptkslb.view.guru.kelas.mapel.chapter

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentChapterKelasBinding
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontakanprojects.apptkslb.view.guru.chapter.ChaptersAdapter
import com.kontakanprojects.apptkslb.view.guru.kelas.mapel.chapter.managechapter.ManageChapterFragment
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import www.sanju.motiontoast.MotionToast

class ChapterKelasFragment : Fragment() {

    private lateinit var binding: FragmentChapterKelasBinding
    private val viewModel by viewModels<ChapterKelasViewModel>()

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
        binding = FragmentChapterKelasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idMapel = ChapterKelasFragmentArgs.fromBundle(arguments as Bundle).idMapel

        with(binding) {
            setToolbarTitle(toolbar)
            setAdapter()
            observe()

            fabAddChapter.setOnClickListener {
                val toManageChapter =
                    ChapterKelasFragmentDirections.actionChapterKelasFragmentToManageChapterFragment()
                toManageChapter.type = ManageChapterFragment.REQUEST_ADD
                toManageChapter.idMapel = idMapel
                findNavController().navigate(toManageChapter)
            }
        }
    }

    private fun observe() {
        val idMapel = ChapterKelasFragmentArgs.fromBundle(arguments as Bundle).idMapel

        viewModel.getChapters(idMapel)
        viewModel.chapters.observe(viewLifecycleOwner, { response ->
            binding.pbLoadingChapter.visibility = View.GONE
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    chaptersAdapter.setData(result)

                    isDataExist(true)
                } else {
                    isDataExist(false)
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

            val itemTouchHelper = ItemTouchHelper(simpleCallback)
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    // touch helper
    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // ambil posisi data dari chapters
            val position = viewHolder.adapterPosition
            val dataResultChapterFragment = chaptersAdapter.getData(position)

            if (direction == ItemTouchHelper.LEFT) {
                val toManageChapter =
                    ChapterKelasFragmentDirections.actionChapterKelasFragmentToManageChapterFragment()
                toManageChapter.type = ManageChapterFragment.REQUEST_EDIT
                toManageChapter.resultChapter = dataResultChapterFragment
                toManageChapter.idMapel = idMapel
                findNavController().navigate(toManageChapter)
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c, recyclerView, viewHolder, dX, dY, actionState,
                isCurrentlyActive
            )
                .addSwipeLeftBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorCorrect
                    )
                )
                .addSwipeLeftActionIcon(R.drawable.ic_edit)
                .create()
                .decorate()

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        }
    }

    private fun isDataExist(state: Boolean) {
        with(binding) {
            if (state) {
                foundLayout.visibility = View.VISIBLE
                notFoundLayout.visibility = View.GONE
            } else {
                foundLayout.visibility = View.GONE
                notFoundLayout.visibility = View.VISIBLE
            }
        }
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

    private fun setToolbarTitle(toolbar: Toolbar) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Data Chapter"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

}