package com.kontakanprojects.apptkslb.view.guru.kelas.mapel

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentMapelBinding
import com.kontakanprojects.apptkslb.model.mapel.ResultsMapel
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontakanprojects.apptkslb.view.guru.kelas.mapel.managemapel.ManageMapelFragment
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import www.sanju.motiontoast.MotionToast

class MapelFragment : Fragment() {

    private val viewModel by viewModels<MapelViewModel>()
    private lateinit var binding: FragmentMapelBinding
    private lateinit var mapelAdapter: MapelAdapter

    private var idKelas = 0
    private var idMapel = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = MapelFragmentArgs.fromBundle(arguments as Bundle)
        idKelas = args.idKelas
        val isFromSiswa = args.isFromSiswa

        setToolbarTitle()
        setAdapter(isFromSiswa)
        observe(idKelas)

        with(binding) {
            if (isFromSiswa) {
                fabAddMapel.visibility = View.GONE
            }

            fabAddMapel.setOnClickListener {
                val toManageMapel =
                    MapelFragmentDirections.actionMapelFragmentToManageMapelFragment()
                toManageMapel.type = ManageMapelFragment.REQUEST_ADD
                toManageMapel.idKelas = idKelas
                findNavController().navigate(toManageMapel)
            }
        }
    }

    private fun observe(idKelas: Int) {
        viewModel.getMapels(idKelas)
        viewModel.mapel.observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    mapelAdapter.setData(result)

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
                    pbLoadingMapel.visibility = View.VISIBLE
                } else {
                    pbLoadingMapel.visibility = View.GONE
                }
            }
        })
    }

    private fun setAdapter(isFromSiswa: Boolean) {
        // set adapter
        mapelAdapter = MapelAdapter()
        with(binding.rvMapel) {
            layoutManager = LinearLayoutManager(requireContext())
            val dividerItemDecoration = DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL)
            addItemDecoration(dividerItemDecoration)
            setHasFixedSize(true)
            this.adapter = mapelAdapter

            if (!isFromSiswa) {
                val itemTouchHelper = ItemTouchHelper(simpleCallback)
                itemTouchHelper.attachToRecyclerView(this)
            }
        }

        mapelAdapter.setOnItemClickCallBack(object : MapelAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsMapel: ResultsMapel) {
                if (isFromSiswa) { // jika routing-nya dari siswa arahkan ke list siswa
                    val toShowListSiswa = MapelFragmentDirections.actionMapelFragmentToSiswaFragment()
                    toShowListSiswa.idKelas = idKelas
                    toShowListSiswa.idMapel = resultsMapel.idMapel ?: 0
                    findNavController().navigate(toShowListSiswa)
                } else {
                    val toChapterKelas = MapelFragmentDirections.actionMapelFragmentToChapterKelasFragment()
                    toChapterKelas.idMapel = resultsMapel.idMapel ?: 0
                    findNavController().navigate(toChapterKelas)
                }
            }
        })
    }

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
            val dataResultMapelFragment = mapelAdapter.getData(position)

            if (direction == ItemTouchHelper.LEFT) {
                val toManageMapel = MapelFragmentDirections.actionMapelFragmentToManageMapelFragment()
                toManageMapel.type = ManageMapelFragment.REQUEST_EDIT
                toManageMapel.resultsMapel = dataResultMapelFragment
                findNavController().navigate(toManageMapel)
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
        if (android.R.id.home == item.itemId) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Data Mata Pelajaran"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

}