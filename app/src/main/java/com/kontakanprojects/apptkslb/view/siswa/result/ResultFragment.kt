package com.kontakanprojects.apptkslb.view.siswa.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentResultBinding
import com.kontakanprojects.apptkslb.session.UserPreference
import com.kontakanprojects.apptkslb.utils.showMessage
import www.sanju.motiontoast.MotionToast
import kotlin.random.Random

class ResultFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentResultBinding
    private val viewModel by viewModels<ResultViewModel>()

    private var idSiswa: Int? = null
    private var namaSiswa: String? = null

    private var idChapter: Int? = null
    private var idChapterLevel: Int? = null

    private val TAG = ResultFragment::class.simpleName

    companion object {
        private const val REWARD_ERASER = 1
        private const val REWARD_BOOK = 2
        private const val REWARD_PENCIL = 3
        private const val REWARD_SHARPENER = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_resultFragment_to_homeFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = ResultFragmentArgs.fromBundle(arguments as Bundle)
        val isFromHome = args.isFromHome
        val idNilai = args.idNilai
        idChapter = args.idChapter
        idChapterLevel = args.idChapterLevelSiswa

        idSiswa = UserPreference(requireContext()).getUser().idUser!!
        namaSiswa = UserPreference(requireContext()).getUser().namaUser!!

        observeNilaiSiswa(idNilai)

        with(binding) {
            if (isFromHome) {
                btnRestart.visibility = View.VISIBLE
                btnClose.visibility = View.VISIBLE
            } else {
                btnFinish.visibility = View.VISIBLE
            }

            btnFinish.setOnClickListener(this@ResultFragment)
            btnRestart.setOnClickListener(this@ResultFragment)
            btnClose.setOnClickListener(this@ResultFragment)
        }
    }

    private fun prepareResult(chapter: String?, nilai: Float?, totalSoal: Int?) {
        val reward = Random.nextInt(1, 4)
        with(binding) {

            val imagesReward = when (reward) {
                REWARD_ERASER -> ResourcesCompat.getDrawable(resources, R.drawable.ic_eraser, null)
                REWARD_BOOK -> ResourcesCompat.getDrawable(resources, R.drawable.ic_book, null)
                REWARD_PENCIL -> ResourcesCompat.getDrawable(resources, R.drawable.ic_pencil, null)
                REWARD_SHARPENER -> ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_sharpener,
                    null
                )
                else -> ResourcesCompat.getDrawable(resources, R.drawable.img_not_found, null)
            }

            Glide.with(requireContext())
                .load(imagesReward)
                .into(ivReward)

            tvCongratulations.text = getString(R.string.result_congratulations, chapter)
            tvName.text = namaSiswa
            tvScore.text = getString(R.string.resultNilai, nilai.toString(), totalSoal.toString())
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_finish -> findNavController().navigate(R.id.action_resultFragment_to_homeFragment)
            R.id.btn_restart -> observeDeleteStateLevelSiswa()
            R.id.btnClose -> findNavController().navigate(R.id.action_resultFragment_to_homeFragment)
        }
    }

    private fun observeNilaiSiswa(idNilai: Int) {
        viewModel.nilaiSiswa(idSiswa!!, idNilai).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results!![0]
                    prepareResult(result.namaChapter, result.nilai, result.totalSoal)
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        response.message,
                        MotionToast.TOAST_ERROR
                    )
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

    private fun observeDeleteStateLevelSiswa() {
        viewModel.resetLevelSiswa(idSiswa!!, idChapterLevel!!, idChapter!!).observe(
            viewLifecycleOwner,
            { response ->
                if (response != null) {
                    if (response.status == 200) {
                        findNavController().navigateUp()
                    } else {
                        showMessage(
                            requireActivity(),
                            getString(R.string.failed),
                            response.message,
                            MotionToast.TOAST_ERROR
                        )
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