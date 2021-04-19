package com.kontakanprojects.apptkslb.view.guru.uploadmateri.uploadintrosoal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.ActivityUploadSoalIntroBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultIntroSoal
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.network.UploadRequestBody
import com.kontakanprojects.apptkslb.utils.showMessage
import okhttp3.MultipartBody
import www.sanju.motiontoast.MotionToast
import java.io.File

class UploadIntroSoalActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {

    private lateinit var binding: ActivityUploadSoalIntroBinding
    private val viewModel by viewModels<UploadViewModel>()
    private lateinit var filePath: String
    private var videoPlayer: SimpleExoPlayer? = null
    private var isVideoAvailable = false
    private var videoPath: String? = null
    private lateinit var data: ResultIntroSoal
    private var idIntro = 0

    private val TAG = UploadIntroSoalActivity::class.simpleName

    companion object {
        const val EXTRA_ID_CHAPTER = "extra_id_chapter"
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_IS_EDITINTRO = "extra_is_editintro"
        private const val REQUEST_CODE_PERMISSIONS = 1
        private const val DELAY_FINISH = 4000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadSoalIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get id chapter
        val idChapter = intent.getIntExtra(EXTRA_ID_CHAPTER, 0)
        val isEditIntro = intent.getBooleanExtra(EXTRA_IS_EDITINTRO, false)

        // Init simpleexoplayer
        videoPlayer = SimpleExoPlayer.Builder(this@UploadIntroSoalActivity).build()
        listenerLoadingExoplayer()

        with(binding) {
            val title: String
            if (isEditIntro) {
                title = "Edit Intro Soal"

                data = intent.getParcelableExtra(EXTRA_DATA)
                idIntro = data.idIntro!!
                val video = data.video

                buildMediaSource(ApiConfig.URL + video).apply {
                    videoView.player = videoPlayer
                    videoView.keepScreenOn = true
                    videoPlayer?.prepare(this!!)
                    videoPlayer?.playWhenReady = true
                }
            } else {
                title = "Upload Intro Soal"
            }

            // set toolbar
            setToolbarTitle(toolbar, title)

            btnPickVideo.setOnClickListener {
                if (isVideoAvailable) { // jika video sudah di pilih arahkan ke upload video
                    visibilityProgress(true)
                    upload(filePath, idIntro, idChapter, isEditIntro)
                } else { // jika belum ada video pick video
                    permission()
                    loadVideo()
                }
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    val selectedVideo = data.data

                    Log.d(TAG, "$selectedVideo: ")
                    if (selectedVideo != null) {
                        videoPath = getUri(selectedVideo)

                        with(binding) {
                            // edit btn to upload
                            tvUpload.text = getString(R.string.upload)
                            // initialize videoplayer use exoplayer
                            videoPlayer =
                                SimpleExoPlayer.Builder(this@UploadIntroSoalActivity).build()
                            listenerLoadingExoplayer()
                            buildMediaSource(videoPath!!).apply {
                                videoView.player = videoPlayer
                                videoView.keepScreenOn = true
                                videoPlayer?.prepare(this!!)
                                videoPlayer?.playWhenReady = true
                            }

                            isVideoAvailable = true
                        }
                    }
                }
            }
        }

    private fun loadVideo() {
        val mimeTypes = arrayOf("video/mp4", "image/3gp")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        resultLauncher.launch(intent)
    }

    private fun getUri(selectedVideo: Uri): String {
        val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver?.query(
            selectedVideo,
            filePathColumn, null, null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            filePath = cursor.getString(columnIndex)
            cursor.close()
        } else {
            filePath = selectedVideo.path.toString()
        }
        return filePath
    }

    private fun upload(filePath: String, idIntro: Int, idChapter: Int, isEditIntro: Boolean) {
        stopVideo()

        with(binding) {
            visibilityProgress(true)
            pbUpload.progress = 0

            val fileVideo = File(filePath)
            val reqFile =
                UploadRequestBody(fileVideo, "video/mp4/3gp", this@UploadIntroSoalActivity)
            val body = MultipartBody.Part.createFormData("video", fileVideo.name, reqFile)

            if (!isEditIntro) { // jika tambah intro video
                viewModel.uploadIntroSoal(idChapter, body)
                    .observe(this@UploadIntroSoalActivity) { response ->
                        visibilityProgress(false, "Upload")
                        if (response != null) {
                            if (response.status == 201) {
                                pbUpload.progress = 100
                                visibilityProgress(false, "Done")
                                finish()

                                showMessage(
                                    this@UploadIntroSoalActivity,
                                    "Sukses 😍",
                                    response.message,
                                    MotionToast.TOAST_SUCCESS
                                )
                            } else {
                                showMessage(
                                    this@UploadIntroSoalActivity,
                                    "Gagal ☹️",
                                    response.message,
                                    MotionToast.TOAST_ERROR
                                )
                            }
                        } else {
                            showMessage(
                                this@UploadIntroSoalActivity,
                                "Gagal ☹️",
                                style = MotionToast.TOAST_ERROR
                            )
                        }
                    }
            } else { // jika edit intro video
                viewModel.editIntroSoal(idIntro, idChapter, body)
                    .observe(this@UploadIntroSoalActivity) { response ->
                        visibilityProgress(false, "Upload")
                        if (response != null) {
                            if (response.status == 200) {
                                pbUpload.progress = 100
                                visibilityProgress(false, "Done")
                                finish()

                                showMessage(
                                    this@UploadIntroSoalActivity,
                                    "Sukses 😍",
                                    response.message,
                                    MotionToast.TOAST_SUCCESS
                                )
                            } else {
                                showMessage(
                                    this@UploadIntroSoalActivity,
                                    "Gagal ☹️",
                                    response.message,
                                    MotionToast.TOAST_ERROR
                                )
                            }
                        } else {
                            showMessage(
                                this@UploadIntroSoalActivity,
                                "Gagal ☹️",
                                style = MotionToast.TOAST_ERROR
                            )
                        }
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
                    Player.STATE_ENDED -> stopVideo()
                    Player.STATE_IDLE -> {
                        Log.i(TAG, "onPlayerStateChanged: ")
                    }
                }
            }
        })
    }

    private fun buildMediaSource(pathVideo: String): MediaSource? {
        val uri = Uri.parse(pathVideo)
        val factory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "My App Name")
        )
        val extractorsFactory = DefaultExtractorsFactory()
        return ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(uri)
    }

    // permission camera, write file, read file , and image
    private fun permission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                loadVideo()
            } else {
                Toast.makeText(this, "Not All Permission Granted!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onProgressUpdate(percentage: Int) {
        binding.pbUpload.progress = percentage
    }

    override fun onPause() {
        super.onPause()
        stopVideo()
    }

    override fun onResume() {
        super.onResume()
        playVideo()
    }

    override fun onStop() {
        super.onStop()
        stopVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun playVideo() {
        videoPlayer?.playWhenReady = true
    }

    private fun stopVideo() {
        videoPlayer?.playWhenReady = false
    }

    private fun releasePlayer() {
        videoPlayer?.release()
    }

    private fun visibilityProgress(isUpload: Boolean, text: String = "") {
        with(binding) {
            if (isUpload) {
                pbUpload.visibility = View.VISIBLE
                tvUpload.visibility = View.GONE
            } else {
                pbUpload.visibility = View.GONE
                tvUpload.visibility = View.VISIBLE
                tvUpload.text = text
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle(toolbar: Toolbar, actionBarTitle: String) {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = actionBarTitle
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
}