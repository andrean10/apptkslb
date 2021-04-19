package com.kontakanprojects.apptkslb.view.siswa.introsoal

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.FragmentVideoIntroBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultIntroSoal
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.utils.showMessage
import www.sanju.motiontoast.MotionToast

class VideoIntroFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentVideoIntroBinding

    private var navidChapter = 0
    private var navIdChapterLevelSiswa = 0
    private var navIdNilai = 0
    private var navIdMapel = 0

    private var videoPlayer: SimpleExoPlayer? = null
    private val viewModel by viewModels<VideoIntroViewModel>()
    private val TAG = VideoIntroFragment::class.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVideoIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            val args = VideoIntroFragmentArgs.fromBundle(arguments as Bundle)
            navidChapter = args.idChapter
            navIdChapterLevelSiswa = args.idChapterLevelSiswa
            navIdNilai = args.idNilai
            navIdMapel = args.idMapel
            // observe video intro
            observeIntro()

            with(binding) {
                // initialize videoplayer use exoplayer
                videoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
                videoView.player = videoPlayer

                btnToSoal.setOnClickListener(this@VideoIntroFragment)
                btnClose.setOnClickListener(this@VideoIntroFragment)
            }
        }
    }

    private fun observeIntro() {
        viewModel.introSoal(navidChapter).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.result!!
                    prepareSoal(result)
                    isDataAvailable(true, response.message)
                } else {
                    isDataAvailable(false, response.message)
                }
            } else {
                showMessage(requireActivity(), "Gagal ", style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun prepareSoal(result: ResultIntroSoal) {
        with(binding) {
            tvChapterIntro.text = result.namaChapter

            if (result.video != null) {
                val urlVideo = ApiConfig.URL + result.video
                buildMediaSource(urlVideo)?.let {
                    videoPlayer?.prepare(it)
                    videoPlayer?.playWhenReady = true
                }
            }

            listenerLoadingExoplayer()
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
                    Player.STATE_ENDED -> pauseVideo()
                    Player.STATE_IDLE -> {
                        Log.i(TAG, "onPlayerStateChanged: ")
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnToSoal -> moveToSoal()
            R.id.btnClose -> findNavController().navigateUp()
        }
    }

    private fun buildMediaSource(url: String): MediaSource? {
        val dataSourceFactory = DefaultDataSourceFactory(requireContext(), "sample")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(url))
    }

    private fun moveToSoal() {
        val toSoal =
            VideoIntroFragmentDirections.actionVideoIntroFragmentToSoalFragment().apply {
                idChapter = navidChapter
                idChapterLevelSiswa = navIdChapterLevelSiswa
                idNilai = navIdNilai
                idMapel = navIdMapel
            }
        findNavController().navigate(toSoal)
    }

    override fun onResume() {
        super.onResume()
        playVideo()
    }

    override fun onPause() {
        super.onPause()
        pauseVideo()
    }

    override fun onStop() {
        super.onStop()
        pauseVideo()
        releasePlayer()
    }

    private fun playVideo() {
        videoPlayer?.playWhenReady = true
    }

    private fun pauseVideo() {
        videoPlayer?.playWhenReady = false
    }

    private fun releasePlayer() {
        videoPlayer?.release()
        videoPlayer = null
    }

    private fun isDataAvailable(state: Boolean, message: String? = null) {
        with(binding) {
            if (state) {
                layoutIntro.visibility = View.VISIBLE
                layoutNotFound.visibility = View.GONE
            } else {
                layoutIntro.visibility = View.GONE
                layoutNotFound.visibility = View.VISIBLE
                messageNotFound.text = message
            }
        }
    }
}