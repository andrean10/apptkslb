package com.kontakanprojects.apptkslb.view.siswa.soal

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentSoalBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultsSoalByChapter
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.session.UserPreference
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontakanprojects.apptkslb.utils.snackbar
import www.sanju.motiontoast.MotionToast

class SoalFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSoalBinding
    private lateinit var viewModel: SoalViewModel

    private var videoPlayer: SimpleExoPlayer? = null
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler()

    private val listSoal: ArrayList<ResultsSoalByChapter> = ArrayList()

    private var idSiswa = 0
    private var idChapter = 0
    private var idChapterLevelSiswa = 0
    private var idNilai = 0
    private var idMaapel = 0

    private var nextLevel = 1
    private var totalLevel = 0

    private var mSelectedOptionPosition = 0
    private var isPlayingVoice = false
    private var isSelected = false
    private var answer = 0

    private val TAG = SoalFragment::class.simpleName

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
////        requireActivity().onBackPressedDispatcher.addCallback(this) {
////            findNavController().navigate(R.id.action_soalFragment_to_homeFragment)
////        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[
                SoalViewModel::class.java]

        val args = SoalFragmentArgs.fromBundle(arguments as Bundle)
        idChapter = args.idChapter
        idChapterLevelSiswa = args.idChapterLevelSiswa
        idNilai = args.idNilai
        idMaapel = args.idMapel

        idSiswa = UserPreference(requireContext()).getUser().idUser!! // passing id user

        if (idChapterLevelSiswa != 0) {
            // observe level siswa
            observeStateChapterAndLevelSiswa()
        }

        // observe soal
        observe(idChapter)

        with(binding) {
            // initialize videoplayer use exoplayer
            videoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
            videoView.player = videoPlayer
            listenerLoadingExoplayer()

            // initialize mediaplayer
            mediaPlayer = MediaPlayer()
            seekBarVoice.max = 100
            configurationPlayVoice()

            tvOptionA.setOnClickListener(this@SoalFragment)
            tvOptionB.setOnClickListener(this@SoalFragment)
            tvOptionC.setOnClickListener(this@SoalFragment)
            tvOptionD.setOnClickListener(this@SoalFragment)
            btnClose.setOnClickListener(this@SoalFragment)
            btnNextSoal.setOnClickListener(this@SoalFragment)
            btnPlayVoice.setOnClickListener(this@SoalFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvOptionA -> {
                selectedOptionView(binding.tvOptionA, 1)
                isSelected = true
            }
            R.id.tvOptionB -> {
                selectedOptionView(binding.tvOptionB, 2)
                isSelected = true
            }
            R.id.tvOptionC -> {
                selectedOptionView(binding.tvOptionC, 3)
                isSelected = true
            }
            R.id.tvOptionD -> {
                selectedOptionView(binding.tvOptionD, 4)
                isSelected = true
            }
            R.id.btnPlayVoice -> {
                isPlayingVoice = if (isPlayingVoice) {
                    pauseAudioPlay()
                    false
                } else {
                    startAudioPlay()
                    updateSeekBar()
                    true
                }
            }
            R.id.btnNextSoal -> {
                if (isSelected) {
                    if (mSelectedOptionPosition == 0) { // jika tidak

                        // check final page button
                        if (nextLevel <= totalLevel) { // cek level dan total level jika lebih dari level arahkan ke result

                            val params = HashMap<String, Int>()
                            params["id_chapter"] = idChapter
                            params["id_level"] =
                                nextLevel // dikurang 1 karena level ditambahkan untuk pengecekkan total level
                            params["id_soal"] =
                                listSoal[nextLevel - 1].idSoal // dikurang 1 karena masih dengan id soal sebelumnya
                            params["jawaban"] = answer

                            if (idChapterLevelSiswa == 0) { // membuat chapter_level_siswa baru
                                observeAddStateLevelSiswa(params)
                            } else { // mengupdate chapter_level_siswa yang sudah dibuat
                                observeUpdateLevelSiswa(idChapterLevelSiswa, params)
                            }
                        }
                    } else { // jika siswa memilih jawaban
                        // cocokkan kunci jawaban dengan opsi yang dipilih
                        val questionCorrect = listSoal[nextLevel - 1].kunciJawaban
                        if (questionCorrect != mSelectedOptionPosition) {
                            answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                        }
                        answerView(questionCorrect, R.drawable.correct_option_border_bg)

                        // passing value jawaban
                        answer = mSelectedOptionPosition

                        if (nextLevel == totalLevel) {
                            binding.btnNextSoal.text = getString(R.string.done)
                        } else {
                            binding.btnNextSoal.text = getString(R.string.next)
                        }

                        // disabled touch area when choose questions
                        enabledTouchOptionsView(false)

                        mSelectedOptionPosition = 0
                    }
                } else {
                    binding.layoutSoal.snackbar("Pilih Salah Satu Jawaban")
                }
            }
            R.id.btnClose -> {
                findNavController().navigateUp()
            }
        }
    }

    // mengambil data soal di awal
    private fun observe(idChapter: Int) {
        viewModel.soal(idChapter).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val results = response.results
                    totalLevel = results?.size!!

                    listSoal.addAll(results)
                    prepareSoal()
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), response.message,
                        MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun observeStateChapterAndLevelSiswa() {
        // ambil chapter yang telah selesai dan level dari siswa
        viewModel.chapterAndLevelSiswa(idSiswa, idChapterLevelSiswa).observe(
            viewLifecycleOwner, { response ->
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.result!!
                        val level = result[0].idLevel!!

                        if (level != 1) {
                            nextLevel = result[0].idLevel!!

                            nextLevel++

                            Log.d(
                                TAG,
                                "observeStateChapterAndLevelSiswa: dijalankan karena bukan level 1"
                            )
                        } else {
                            observeJawabanLevelSatu()
                            Log.d(TAG, "observeStateChapterAndLevelSiswa: level pada chapter satu")
                        }
                    }
                }
            })
    }

    private fun observeJawabanLevelSatu() {
        viewModel.jawabanChapterLevelSatu(idChapter).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    // berarti level satu sudah dikerjakan dan jawaban sudah ada
                    nextLevel = 2 // tinggal ubah level ke 2
                    prepareSoal()
                } else {
                    nextLevel = 1
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
                isSelected = true
            }
        })
    }

    private fun observeAddStateLevelSiswa(params: HashMap<String, Int>) {
        viewModel.stateLevelSiswa(idSiswa, params).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 201 || response.status == 200) {
                    // get id_chapter_level_siswa untuk di update selanjutnya
                    idChapterLevelSiswa = response.result?.get(0)?.idChapterLevelSiswa!!

                    observeRiwayatMapel(idSiswa, idMaapel)
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), response.message,
                        MotionToast.TOAST_ERROR)
                    isSelected = true
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
                isSelected = true
            }
        })
    }

    private fun observeUpdateLevelSiswa(idChapterLevelSiswa: Int, params: HashMap<String, Int>) {
        viewModel.updateStateLevelSiswa(idSiswa, idChapterLevelSiswa, params).observe(
            viewLifecycleOwner, { response ->
                if (response != null) {
                    if (response.status == 200) {

                        // check jika level sekarang sama dengan total level
                        if (nextLevel == totalLevel) {
                            observeStoreNilaiSiswa()
                        } else {
                            nextLevel++

                            prepareSoal()
                        }

                        // falsekan pilihan
                        isSelected = false
                    } else {
                        showMessage(requireActivity(), getString(R.string.failed), response.message,
                            MotionToast.TOAST_ERROR)
                        isSelected = true
                    }
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
                    isSelected = true
                }
            })
    }

    private fun observeStoreNilaiSiswa() {
        // cek jika id_nilai_siswa sudah ada tinggal di update saja tanpa menambahkan data baru
        if (idNilai == 0) { // berarti ditambahkan
            viewModel.storeNilaiSiswa(idSiswa, idChapter).observe(viewLifecycleOwner, { response ->
                if (response != null) {
                    if (response.status == 201) { // jika sukses tertambah nilainnya pindah intent
                        val newIdNilai = response.results!![0].idNilai

                        val toSoal =
                            SoalFragmentDirections.actionSoalFragmentToResultFragment().apply {
                                idNilai = newIdNilai!!
                            }
                        findNavController().navigate(toSoal)
                    } else {
                        showMessage(
                            requireActivity(),
                            getString(R.string.failed),
                            response.message,
                            MotionToast.TOAST_ERROR
                        )

                        isSelected = true
                    }
                } else {
                    showMessage(requireActivity(), getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR)
                }
            })
        } else { // berarti diedit idnilainnya
            viewModel.updateNilaiSiswa(idSiswa, idNilai, idChapter, idChapterLevelSiswa)
                .observe(viewLifecycleOwner, { response ->
                    if (response != null) {
                        if (response.status == 200) {
                            val toSoal =
                                SoalFragmentDirections.actionSoalFragmentToResultFragment()
                            toSoal.idNilai = idNilai
                            findNavController().navigate(toSoal)
                        } else {
                            showMessage(requireActivity(), getString(R.string.failed),
                                response.message, MotionToast.TOAST_ERROR)
                            isSelected = true
                        }
                    } else {
                        showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
                        isSelected = true
                    }
                })
        }
    }

    private fun observeRiwayatMapel(idSiswa: Int, idMapel: Int) {
        viewModel.addRiwayatMapelSiswa(idSiswa, idMapel).observe(viewLifecycleOwner, { response ->
                if (response != null) {
                    if (response.status == 201 || response.status == 200) {
                        nextLevel++

                        // persiapkan soal baru
                        prepareSoal()

                        // falsekan pilihan
                        isSelected = false
                    } else {
                        showMessage(requireActivity(), getString(R.string.failed), response.message,
                            MotionToast.TOAST_ERROR)
                    }
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), style = MotionToast.TOAST_ERROR)
                    isSelected = true
                }
            })
    }

    // untuk mempersiapkan soal
    private fun prepareSoal() {
        with(binding) {
            // set title level and progressbar
            tvCurrentLevel.text = getString(R.string.level, nextLevel)
            tvTotalLevel.text = getString(R.string.totalLevel, totalLevel)

            // progressbar view
            val progressTotal = (nextLevel.toDouble() / totalLevel) * 100
            pbLevel.progress = progressTotal.toInt()

            // set default view
            defaultOptionsView()
            btnNextSoal.text = getString(R.string.submit)
            enabledTouchOptionsView(true)

            for (soal in listSoal) {
                if (soal.idLevel == nextLevel) {
                    Log.d(TAG, "prepareSoal: $nextLevel")

                    tvOptionA.text = soal.opsiA
                    tvOptionB.text = soal.opsiB
                    tvOptionC.text = soal.opsiC
                    tvOptionD.text = soal.opsiD

                    val endPoint = ApiConfig.URL

                    if (soal.video != null) {
                        val urlVideo = endPoint + soal.video
                        buildMediaSource(urlVideo)?.let {
                            videoPlayer?.prepare(it)
                            videoPlayer?.playWhenReady = true
                        }

                        layoutVideoView.visibility = View.VISIBLE
                    } else {
                        layoutVideoView.visibility = View.GONE
                    }

                    if (soal.gambar != null) {
                        loadImage(true)
                        val urlImage = endPoint + soal.gambar
                        Glide.with(requireContext())
                            .load(urlImage)
                            .listener(listenerImage)
                            .error(R.drawable.img_not_found)
                            .into(imgSoal)

                        layoutImageView.visibility = View.VISIBLE
                    } else {
                        layoutImageView.visibility = View.GONE
                    }

                    // prepare voice in mediaplayer
                    if (soal.soalSuara != null) {
                        val urlAudio = endPoint + soal.soalSuara
                        prepareMediaPlayer(urlAudio)
                        updateSeekBar()
                    }
                }
            }
        }
    }

    private val listenerImage = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            loadImage(false)
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            loadImage(false)
            return false
        }

    }

    private fun defaultOptionsView() {
        with(binding) {
            val options = ArrayList<TextView>()
            options.add(0, tvOptionA)
            options.add(1, tvOptionB)
            options.add(2, tvOptionC)
            options.add(3, tvOptionD)

            for (option in options) {
                option.setTextColor(Color.parseColor("#FFFFFFFF"))
                option.typeface = Typeface.DEFAULT
                option.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.default_option_border_bg
                )
            }
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        defaultOptionsView()
        mSelectedOptionPosition = selectedOptionNum

        tv.setTextColor(Color.parseColor("#DFF3F3"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.select_option_border_bg
        )
    }

    private fun enabledTouchOptionsView(isEnabled: Boolean) {
        with(binding) {
            if (isEnabled) {
                tvOptionA.isEnabled = true
                tvOptionB.isEnabled = true
                tvOptionC.isEnabled = true
                tvOptionD.isEnabled = true
            } else {
                tvOptionA.isEnabled = false
                tvOptionB.isEnabled = false
                tvOptionC.isEnabled = false
                tvOptionD.isEnabled = false
            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int) {
        with(binding) {
            when (answer) {
                1 -> {
                    tvOptionA.background = ContextCompat.getDrawable(
                        requireContext(), drawableView
                    )
                }
                2 -> {
                    tvOptionB.background = ContextCompat.getDrawable(
                        requireContext(), drawableView
                    )
                }
                3 -> {
                    tvOptionC.background = ContextCompat.getDrawable(
                        requireContext(), drawableView
                    )
                }
                4 -> {
                    tvOptionD.background = ContextCompat.getDrawable(
                        requireContext(), drawableView
                    )
                }
            }
        }
    }

    private fun listenerLoadingExoplayer() {
        // event listener exoplayer
        videoPlayer?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> binding.pbVideo.visibility = View.VISIBLE
                    Player.STATE_READY -> binding.pbVideo.visibility = View.GONE
                    Player.STATE_ENDED -> videoPlayer!!.playWhenReady = false
                    Player.STATE_IDLE -> {
                        Log.i(TAG, "onPlayerStateChanged: ")
                    }
                }
            }
        })
    }

    private fun configurationPlayVoice() {
        with(binding) {
            seekBarVoice.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    val seekBar: SeekBar = v as SeekBar
                    val playPosition = (mediaPlayer!!.duration / 100) * seekBar.progress
                    mediaPlayer!!.seekTo(playPosition)
                    tvCurrentTime.text = miliSecondToTimer(mediaPlayer!!.currentPosition.toLong())
                    return false
                }
            })

            mediaPlayer!!.setOnBufferingUpdateListener { _, percent ->
                seekBarVoice.secondaryProgress = percent
            }

            mediaPlayer!!.setOnCompletionListener {
                seekBarVoice.progress = 0
                btnPlayVoice.setImageResource(R.drawable.ic_play)
                tvCurrentTime.text = getString(R.string.time_zero)
                tvTotalDuration.text = getString(R.string.time_zero)
                mediaPlayer?.reset()
                isPlayingVoice = false

                if (listSoal.size > 0) {
                    val url = ApiConfig.URL + listSoal[nextLevel - 1].soalSuara
                    prepareMediaPlayer(url)

                    Log.d(TAG, "configurationPlayVoice: Dijalankan ulang")
                }
            }
        }
    }

    private fun buildMediaSource(url: String): MediaSource? {
        val dataSourceFactory = DefaultDataSourceFactory(requireContext(), "sample")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(url))
    }

    private fun prepareMediaPlayer(url: String) {
        try {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setDataSource(url) // URL music file
            mediaPlayer?.prepare()

            binding.tvTotalDuration.text = miliSecondToTimer(mediaPlayer?.duration?.toLong()!!)
        } catch (e: Exception) {
            Log.e(TAG, "prepareMediaPlayer: ${e.message}")
        }
    }

    private val updater = Runnable {
        updateSeekBar()
        if (mediaPlayer != null) {
            val currentDuration: Long = mediaPlayer!!.currentPosition.toLong()
            binding.tvCurrentTime.text = miliSecondToTimer(currentDuration)
        }
    }

    private fun updateSeekBar() {
        if (mediaPlayer != null) {
            if (mediaPlayer?.isPlaying!!) {
                binding.seekBarVoice.progress =
                    (mediaPlayer!!.currentPosition.toFloat() / mediaPlayer!!.duration * 100).toInt()
                handler.postDelayed(updater, 1000)
            }
        }
    }

    private fun miliSecondToTimer(milliSeconds: Long): String {
        var timerString = ""
        var secondString = ""

        val hours: Int = ((milliSeconds / (1000 * 60 * 60)).toInt())
        val minutes: Int = (milliSeconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds: Int = ((milliSeconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt())

        if (hours > 0) {
            timerString = "$hours:"
        } else {
            timerString = "0"
        }

        if (seconds < 10) {
            secondString = "0$seconds"
        } else {
            secondString = "" + seconds
        }

        timerString = "$timerString$minutes:$secondString"
        return timerString
    }

    override fun onResume() {
        super.onResume()
        playVideo()
        observe(idChapter)
        observeStateChapterAndLevelSiswa()
    }

    override fun onPause() {
        super.onPause()
        pauseVideo()
        pauseAudioPlay()
    }

    override fun onStop() {
        super.onStop()
        pauseVideo()
        pauseAudioPlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun playVideo() {
        videoPlayer?.playWhenReady = true
    }

    private fun pauseVideo() {
        videoPlayer?.playWhenReady = false
    }

    private fun startAudioPlay() {
        mediaPlayer?.start()
        binding.btnPlayVoice.setImageResource(R.drawable.ic_pause)
        updateSeekBar()

        isPlayingVoice = true
    }

    private fun pauseAudioPlay() {
        mediaPlayer?.pause()
        binding.btnPlayVoice.setImageResource(R.drawable.ic_play)
        handler.removeCallbacks(updater)

        isPlayingVoice = false
    }

    private fun releasePlayer() {
        videoPlayer?.release()
        mediaPlayer?.release()
        videoPlayer = null
        mediaPlayer = null
    }

    private fun loadImage(state: Boolean) {
        with(binding) {
            if (state) {
                pbImage.visibility = View.VISIBLE
            } else {
                pbImage.visibility = View.GONE
            }
        }
    }
}