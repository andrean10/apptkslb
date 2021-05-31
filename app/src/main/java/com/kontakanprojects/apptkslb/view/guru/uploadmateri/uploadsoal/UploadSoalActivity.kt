package com.kontakanprojects.apptkslb.view.guru.uploadmateri.uploadsoal

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.databinding.ActivityUploadSoalBinding
import com.kontakanprojects.apptkslb.model.chapter.ResultsSoalByChapter
import com.kontakanprojects.apptkslb.network.ApiConfig
import com.kontakanprojects.apptkslb.network.UploadRequestBody
import com.kontakanprojects.apptkslb.utils.createPartFromString
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontakanprojects.apptkslb.utils.snackbar
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class UploadSoalActivity : AppCompatActivity(), View.OnClickListener,
    UploadRequestBody.UploadCallback {

    private lateinit var binding: ActivityUploadSoalBinding
    private val viewModel by viewModels<UploadSoalViewModel>()

    private var type = 0
    private var idChapter = 0
    private var dataSoal: ResultsSoalByChapter? = null

    private lateinit var bottomSheetView: View

    private var imageFile: File? = null
    private var imageUri: Uri? = null

    private var videoPlayer: SimpleExoPlayer? = null
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler()

    private var videoPath: String? = null
    private var gambarPath: String? = null
    private var audioPath: String? = null

    private var isVideoAvailable = false
    private var isImageAvailable = false
    private var isVoiceAvailable = false
    private var isPlayingVoice = false

    private val TAG = UploadSoalActivity::class.simpleName

    companion object {
        const val TYPE = "type"
        const val REQUEST_ADD_VIDEO = 10
        const val REQUEST_ADD_IMAGE = 11
        const val REQUEST_EDIT_VIDEO = 20
        const val REQUEST_EDIT_IMAGE = 21
        const val REQUEST_DELETE = 30
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_ID_CHAPTER = "extra_id_chapter"
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_CODE_CAPTURE_IMAGE = 222
        private const val REQUEST_CODE_SELECT_IMAGE = 333
        private const val VIDEO = 100
        private const val AUDIO = 200
        private const val MUST_PICK_VIDEO = "Pilih video untuk diupload"
        private const val MUST_PICK_IMAGE = "Pilih gambar untuk diupload"
        private const val MUST_PICK_AUDIO = "Pilih audio untuk diupload"
        private const val MUST_SET_OPSI = "Kolom inputan pernyataan opsi %s tidak boleh kosong!"
        private const val MUST_SET_KEY_OPSI = "Opsi kunci jawaban tidak boleh kosong!"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadSoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getIntExtra(TYPE, 0)
        idChapter = intent.getIntExtra(EXTRA_ID_CHAPTER, 0)
        dataSoal = intent.getParcelableExtra(EXTRA_DATA)

        initPlayer()

        lateinit var title: String
        with(binding) {
            when (type) {
                REQUEST_ADD_VIDEO -> {
                    title = "Upload Soal"
                    layoutUploadVideo.visibility = View.VISIBLE
                }
                REQUEST_ADD_IMAGE -> {
                    title = "Upload Soal"
                    layoutUploadImg.visibility = View.VISIBLE
                }
                REQUEST_EDIT_VIDEO -> {
                    title = "Ubah Soal"
                    prepareEditSoal()
                    tvUploadSoal.text = "Simpan Perubahan"

                    // passing to videoPath and audioPath
                    videoPath = dataSoal!!.video
                    audioPath = dataSoal!!.soalSuara
                }
                REQUEST_EDIT_IMAGE -> {
                    title = "Ubah Soal"
                    prepareEditSoal()
                    tvUploadSoal.text = "Simpan Perubahan"

                    // passing to imagepath and audiopath
                    gambarPath = dataSoal!!.gambar
                    audioPath = dataSoal!!.soalSuara
                }
                else -> {
                }
            }
        }

        with(binding) {
            setToolbarTitle(toolbar, title) // set toolbar

            btnPickVideo.setOnClickListener(this@UploadSoalActivity)
            btnPickImage.setOnClickListener(this@UploadSoalActivity)
            btnPickVoice.setOnClickListener(this@UploadSoalActivity)
            btnPlayVoice.setOnClickListener(this@UploadSoalActivity)
            btnUploadSoal.setOnClickListener(this@UploadSoalActivity)
        }
    }

    private fun initPlayer() {
        // Init simpleExoplayer
        videoPlayer = SimpleExoPlayer.Builder(this).build()
        listenerLoadingExoplayer()

        // initialize mediaplayer
        mediaPlayer = MediaPlayer()
        binding.seekBarVoice.max = 100
        configurationPlayVoice()
    }

    private fun prepareEditSoal() {
        with(binding) {
            // visible media player
            val urlPoint = ApiConfig.URL
            if (type == REQUEST_EDIT_VIDEO) {
                if (dataSoal?.video != null) {
                    layoutUploadVideo.visibility = View.VISIBLE
                    videoView.visibility = View.VISIBLE

                    buildMediaSource(urlPoint + dataSoal!!.video!!).apply {
                        videoView.player = videoPlayer
                        videoView.keepScreenOn = true
                        videoPlayer?.prepare(this!!)
                        videoPlayer?.playWhenReady = true
                    }
                }
            } else {
                if (dataSoal?.gambar != null) {
                    layoutUploadImg.visibility = View.VISIBLE
                    imgPreviewUpload.visibility = View.VISIBLE

                    Glide.with(this@UploadSoalActivity)
                        .load(urlPoint + dataSoal!!.gambar)
                        .error(R.drawable.img_not_found)
                        .into(imgPreviewUpload)
                }
            }

            if (dataSoal?.soalSuara != null) {
                rlVoice.visibility = View.VISIBLE
                prepareMediaPlayer(urlPoint + dataSoal!!.soalSuara!!)
            }

            edtOpsiA.setText(dataSoal!!.opsiA)
            edtOpsiB.setText(dataSoal!!.opsiB)
            edtOpsiC.setText(dataSoal!!.opsiC)
            edtOpsiD.setText(dataSoal!!.opsiD)

            when (dataSoal!!.kunciJawaban) { // kunci jawaban
                1 -> rbOpsiA.isChecked = true
                2 -> rbOpsiB.isChecked = true
                3 -> rbOpsiC.isChecked = true
                4 -> rbOpsiD.isChecked = true
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnPickVideo -> {
                permission()
                loadVideo()
            }
            R.id.btnPickImage -> {
                permission()
                showBottomSheet()
            }
            R.id.btnPickVoice -> {
                permission()
                loadVoice()
            }
            R.id.btnPlayVoice -> {
                isPlayingVoice = if (isPlayingVoice) {
                    pauseAudioPlay()
                    false
                } else {
                    startAudioPlay()
                    true
                }
            }
            R.id.btnUploadSoal -> {
                // get inputan edtext
                with(binding) {
                    val opsiA = edtOpsiA.text.toString().trim()
                    val opsiB = edtOpsiB.text.toString().trim()
                    val opsiC = edtOpsiC.text.toString().trim()
                    val opsiD = edtOpsiD.text.toString().trim()

                    // check pertama
                    when (type) {
                        REQUEST_ADD_VIDEO -> {
                            if (videoPath.isNullOrEmpty()) {
                                layoutUploadSoal.snackbar(MUST_PICK_VIDEO)
                                return@with
                            }
                        }
                        REQUEST_ADD_IMAGE -> {
                            if (gambarPath.isNullOrEmpty()) {
                                layoutUploadSoal.snackbar(MUST_PICK_IMAGE)
                                return@with
                            }
                        }
                    }

                    when {
                        // cek submit
                        audioPath.isNullOrEmpty() -> {
                            layoutUploadSoal.snackbar(MUST_PICK_AUDIO)
                            return@with
                        }
                        opsiA.isEmpty() -> {
                            layoutUploadSoal.snackbar(String.format(MUST_SET_OPSI, "A"))
                            return@with
                        }
                        opsiB.isEmpty() -> {
                            layoutUploadSoal.snackbar(String.format(MUST_SET_OPSI, "B"))
                            return@with
                        }
                        opsiC.isEmpty() -> {
                            layoutUploadSoal.snackbar(String.format(MUST_SET_OPSI, "C"))
                            return@with
                        }
                        opsiD.isEmpty() -> {
                            layoutUploadSoal.snackbar(String.format(MUST_SET_OPSI, "D"))
                            return@with
                        }
                        !rbOpsiA.isChecked && !rbOpsiB.isChecked &&
                                !rbOpsiC.isChecked && !rbOpsiD.isChecked -> {
                            layoutUploadSoal.snackbar(MUST_SET_KEY_OPSI)
                            return@with
                        }
                        else -> { // upload soal
                            visibilityProgress(true)
                            pbUploadSoal.progress = 0
                            var kunciJawaban = 0

                            when {
                                rbOpsiA.isChecked -> kunciJawaban = 1
                                rbOpsiB.isChecked -> kunciJawaban = 2
                                rbOpsiC.isChecked -> kunciJawaban = 3
                                rbOpsiD.isChecked -> kunciJawaban = 4
                            }

                            // assign to params
                            val params = HashMap<String, RequestBody>().apply {
                                put("opsiA", createPartFromString(opsiA))
                                put("opsiB", createPartFromString(opsiB))
                                put("opsiC", createPartFromString(opsiC))
                                put("opsiD", createPartFromString(opsiD))
                                put("kunci_jawaban", createPartFromString(kunciJawaban.toString()))
                            }

                            val idSoal = 0
                            var bodyVideo: MultipartBody.Part? = null
                            var bodyImage: MultipartBody.Part? = null
                            var bodyAudio: MultipartBody.Part? = null

                            when (type) {
                                REQUEST_ADD_VIDEO -> {
                                    Log.d(TAG, "onClick: Tipe Tambah Video")

                                    bodyVideo = reqFileVideo()
                                    bodyAudio = reqFileAudio()
                                }
                                REQUEST_ADD_IMAGE -> {
                                    Log.d(TAG, "onClick: Tipe Tambah Gambar")

                                    bodyImage = reqFileImage()
                                    bodyAudio = reqFileAudio()
                                }
                                REQUEST_EDIT_VIDEO -> {
                                    Log.d(TAG, "onClick: Tipe Edit Video")

                                    // cek apakah user mengubah video atau audio
                                    when {
                                        isVideoAvailable && isVoiceAvailable -> {
                                            Log.d(
                                                TAG,
                                                "onClick: User mengirim video dan audio baru"
                                            )

                                            bodyVideo = reqFileVideo()
                                            bodyAudio = reqFileAudio()
                                        }
                                        isVideoAvailable -> {
                                            Log.d(TAG, "onClick: User mengirim video baru")

                                            bodyVideo = reqFileVideo()
                                            bodyAudio = reqFileAudioEmpty()
                                        }
                                        isVoiceAvailable -> {
                                            Log.d(TAG, "onClick: User mengirim audio baru")

                                            bodyVideo = reqFileVideoEmpty()
                                            bodyAudio = reqFileAudio()
                                        }
                                        else -> {
                                            Log.d(
                                                TAG,
                                                "onClick: User tidak mengirimkan video dan audio baru"
                                            )

                                            bodyVideo = reqFileVideoEmpty()
                                            bodyAudio = reqFileAudioEmpty()
                                        }
                                    }
                                }
                                REQUEST_EDIT_IMAGE -> {
                                    Log.d(TAG, "onClick: Tipe Edit Gambar")

                                    // cek apakah user mengubah video atau audio
                                    when {
                                        isImageAvailable && isVoiceAvailable -> {
                                            Log.d(
                                                TAG,
                                                "onClick: User mengirim gambar dan audio baru"
                                            )

                                            bodyImage = reqFileImage()
                                            bodyAudio = reqFileAudio()
                                        }
                                        isImageAvailable -> {
                                            Log.d(TAG, "onClick: User mengirim gambar baru")

                                            bodyImage = reqFileImage()
                                            bodyAudio = reqFileAudioEmpty()
                                        }
                                        isVoiceAvailable -> {
                                            Log.d(TAG, "onClick: User mengirim audio baru")

                                            bodyImage = reqFileImageEmpty()
                                            bodyAudio = reqFileAudio()
                                        }
                                        else -> {
                                            Log.d(
                                                TAG,
                                                "onClick: User tidak mengirimkan gambar dan audio baru"
                                            )

                                            bodyImage = reqFileImageEmpty()
                                            bodyAudio = reqFileAudioEmpty()
                                        }
                                    }
                                }
                            }

                            manageSoal(type, idSoal, bodyVideo, bodyImage, bodyAudio, params)
                        }
                    }
                }
            }
        }
    }

    private fun reqFileVideo(): MultipartBody.Part {
        val fileVideo = File(videoPath!!)
        val reqFileVideo = UploadRequestBody(fileVideo, "video/mp4/3gp", this)
        return MultipartBody.Part.createFormData("video", fileVideo.name, reqFileVideo)
    }

    private fun reqFileImage(): MultipartBody.Part {
        val fileImage = File(gambarPath!!)
        val reqFileImage =
            fileImage.asRequestBody("image/jpeg/jpg/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            "gambar", fileImage.name, reqFileImage
        )
    }

    private fun reqFileAudio(): MultipartBody.Part {
        val fileAudio = File(audioPath!!)
        val reqFileAudio = UploadRequestBody(fileAudio, "audio/mp3/wav/m4a", this)
        return MultipartBody.Part.createFormData("soal_suara", fileAudio.name, reqFileAudio)
    }

    private fun reqFileVideoEmpty(): MultipartBody.Part {
        val reqFileVideo = ""
            .toRequestBody("video/mp4/3gp".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("video", "", reqFileVideo)
    }

    private fun reqFileImageEmpty(): MultipartBody.Part {
        val reqFileImage = ""
            .toRequestBody("image/jpeg/jpg/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("gambar", "", reqFileImage)
    }

    private fun reqFileAudioEmpty(): MultipartBody.Part {
        val reqFileAudio = ""
            .toRequestBody("audio/mp3/wav/m4a".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("soal_suara", "", reqFileAudio)
    }

    private fun manageSoal(
        type: Int,
        idSoal: Int? = null,
        bodyVideo: MultipartBody.Part? = null,
        bodyImage: MultipartBody.Part? = null,
        bodyAudio: MultipartBody.Part? = null,
        params: HashMap<String, RequestBody>? = null
    ) {
        when (type) {
            REQUEST_ADD_VIDEO -> {
                observeUpload(bodyVideo, bodyAudio = bodyAudio!!, params = params)
            }
            REQUEST_ADD_IMAGE -> {
                observeUpload(bodyImage = bodyImage, bodyAudio = bodyAudio!!, params = params)
            }
            REQUEST_EDIT_VIDEO -> {
                observeEdit(idSoal, bodyVideo, bodyImage, bodyAudio, params)
            }
            REQUEST_EDIT_IMAGE -> {
                observeEdit(idSoal, bodyVideo, bodyImage, bodyAudio, params)
            }
            REQUEST_DELETE -> {
                observeDelete(idSoal!!)
            }
        }
    }

    private fun observeUpload(
        bodyVideo: MultipartBody.Part? = null,
        bodyImage: MultipartBody.Part? = null,
        bodyAudio: MultipartBody.Part,
        params: HashMap<String, RequestBody>?
    ) {
        viewModel.uploadSoal(idChapter, bodyVideo, bodyImage, bodyAudio, params!!)
            .observe(this, { response ->
                visibilityProgress(false, "Upload Soal")
                Log.d(TAG, "upload: Dijalankan")
                if (response != null) {
                    if (response.status == 200) {
                        binding.pbUploadSoal.progress = 100
                        visibilityProgress(false, "Done")
                        finish()

                        showMessage(
                            this, getString(R.string.success),
                            "Berhasil menambahkan data soal", MotionToast.TOAST_SUCCESS
                        )
                    } else {
                        showMessage(
                            this, getString(R.string.failed), response.message,
                            MotionToast.TOAST_ERROR
                        )
                    }
                } else {
                    showMessage(
                        this, getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR
                    )
                }
            })
    }

    private fun observeEdit(
        idSoal: Int?,
        bodyVideo: MultipartBody.Part? = null,
        bodyImage: MultipartBody.Part? = null,
        bodyAudio: MultipartBody.Part? = null,
        params: HashMap<String, RequestBody>?
    ) {
//        viewModel.editSoal(idChapter, idSoal!!, bodyVideo, bodyImage, bodyAudio, params!!)
//            .observe(this, { response ->
//                visibilityProgress(false, "Simpan Perubahan")
//                if (response != null) {
//                    if (response.status == 200) {
//                        binding.pbUploadSoal.progress = 100
//                        visibilityProgress(false, "Done")
//                        finish()
//
//                        showMessage(
//                            this, getString(R.string.success), response.message,
//                            MotionToast.TOAST_SUCCESS
//                        )
//                    } else {
//                        showMessage(
//                            this, getString(R.string.failed), response.message,
//                            MotionToast.TOAST_ERROR
//                        )
//                    }
//                } else {
//                    showMessage(this, getString(R.string.failed), style = MotionToast.TOAST_ERROR)
//                }
//            })
    }

    private fun observeDelete(idSoal: Int) {
        viewModel.deleteSoal(idChapter, idSoal).observe(this, { response ->
            if (response != null) {
                if (response.status == 200) {
                    binding.pbUploadSoal.progress = 100
                    releasePlayer()
                    finish()

                    showMessage(
                        this, getString(R.string.success), response.message,
                        MotionToast.TOAST_SUCCESS
                    )
                } else {
                    showMessage(
                        this, getString(R.string.failed), response.message,
                        MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(this, getString(R.string.failed), style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private var resultLauncherVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    val selectedVideo = data.data

                    if (selectedVideo != null) {
                        videoPath = getFilePath(VIDEO, selectedVideo)

                        with(binding) {
                            // visible playervideo
                            videoView.visibility = View.VISIBLE
                            // set video ke player Video
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

    private var resultLauncherVoice =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    val selectedVoice = data.data

                    if (selectedVoice != null) {
                        audioPath = getFilePath(AUDIO, selectedVoice)

                        with(binding) {
                            // visible player Audio
                            rlVoice.visibility = View.VISIBLE
                            // set audio ke player Audio
                            // reset mediaplayer
                            mediaPlayer!!.reset()
                            prepareMediaPlayer(audioPath!!)
                            updateSeekBar()

                            Log.d(TAG, "Audio Path: $audioPath")

                            isVoiceAvailable = true
                        }
                    }
                }
            }
        }

    private fun loadVideo() {
        val mimeTypes = arrayOf("video/mp4", "video/3gp")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        resultLauncherVideo.launch(Intent.createChooser(intent, "Pilih 1 Video"))
    }

    private fun loadVoice() {
        val mimeTypes = arrayOf("audio/wav", "audio/m4a", "audio/mp3")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        resultLauncherVoice.launch(Intent.createChooser(intent, "Pilih 1 Audio"))
    }

    // gambar
    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        bottomSheetView = LayoutInflater.from(this).inflate(
            R.layout.bottom_sheet_pick, findViewById(R.id.bottomSheetContainer)
        )

        val chooseImage: ImageView = bottomSheetView.findViewById(R.id.imgCamera)
        chooseImage.setOnClickListener {
            dispatchCaptureImageIntent()
            bottomSheetDialog.dismiss()
        }

        val chooseGaleri: ImageView = bottomSheetView.findViewById(R.id.imgGaleri)
        chooseGaleri.setOnClickListener {
            selectImageIntent()
            bottomSheetDialog.dismiss()
        }

        // set ke view
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    // logic menampilkan kamera
    private fun dispatchCaptureImageIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager!!) != null) {
            try {
                imageFile = createImageFile()
            } catch (e: IOException) {
                showMessage(this, getString(R.string.failed), e.message!!, MotionToast.TOAST_ERROR)
            }

            if (imageFile != null) {
                imageUri = FileProvider.getUriForFile(
                    applicationContext,
                    "com.kontakanprojects.apptkslb.fileprovider",
                    imageFile!!
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE)
            }
        }
    }

    private fun selectImageIntent() {
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        if (intent.resolveActivity(packageManager!!) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    @Throws(Exception::class)
    private fun createImageFile(): File? {
        val fileName =
            "IMAGE_" + SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(Date())
        val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(fileName, ".jpg", directory)
        val currentImagePath = imageFile.absolutePath
        return imageFile
    }

    private fun getPathImage(contentUri: Uri): String? {
        val filePath: String?
        val cursor = contentResolver?.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_CAPTURE_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    try {
                        imageUri?.let { startCrop(it) }
                    } catch (e: Exception) {
                        Log.e(TAG, "onActivityResult: ${e.message}")
                        showMessage(
                            this,
                            getString(R.string.failed),
                            e.message.toString(),
                            MotionToast.TOAST_ERROR
                        )
                    }
                }
            }
            REQUEST_CODE_SELECT_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let {
                        startCrop(it)
                    }
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    gambarPath = getPathImage(UCrop.getOutput(data!!)!!)

                    // ubah data ke server
//                    changeFotoProfile(result)

                    Log.d(TAG, "onActivityResult: $gambarPath")

                    with(binding) {
                        imgPreviewUpload.visibility = View.VISIBLE
                        Glide.with(this@UploadSoalActivity)
                            .load(gambarPath)
                            .into(imgPreviewUpload)
                    }

                    isImageAvailable = true
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    val cropError = UCrop.getError(data!!)
                    showMessage(
                        this, getString(R.string.failed), cropError?.message.toString(),
                        MotionToast.TOAST_ERROR
                    )
                    Log.e(TAG, "onActivityResult: ${cropError?.message}")
                }
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val uCrop = UCrop.of(uri, Uri.fromFile(createImageFile()))
        uCrop.withAspectRatio(1F, 1F)
        uCrop.withMaxResultSize(640, 640)
        uCrop.withOptions(getCropOptions())
        uCrop.start(this)
    }

    private fun getCropOptions(): UCrop.Options {
        return UCrop.Options().apply {
            setCompressionQuality(100)
            setHideBottomControls(false)
            setToolbarTitle("Crop Image")
        }
    }

    // video
    private fun buildMediaSource(url: String): MediaSource? {
        val uri = Uri.parse(url)
        val factory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "My App Name")
        )
        val extractorsFactory = DefaultExtractorsFactory()
        return ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(uri)
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

    private fun configurationPlayVoice() {
        with(binding) {
            seekBarVoice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    // why check it from user
                    // because we have two event
                    // 1 is a change from mediaplayer
                    // 2 change from user
                    if (fromUser) {
                        val playPosition = (mediaPlayer!!.duration / 100) * seekBar!!.progress
                        mediaPlayer!!.seekTo(playPosition)
                        tvCurrentTime.text =
                            miliSecondToTimer(mediaPlayer!!.currentPosition.toLong())
                    }

                    Log.d(TAG, "onProgressChanged, Progress seekbar = $progress")
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
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

                // set ulang lagu
                when {
                    dataSoal?.soalSuara != null -> {
                        prepareMediaPlayer(ApiConfig.URL + dataSoal!!.soalSuara!!)
                    }
                    audioPath != null -> {
                        prepareMediaPlayer(audioPath!!)
                    }
                }
            }
        }
    }

    private fun prepareMediaPlayer(url: String) {
        Log.d(TAG, "prepareMediaPlayer, URL = $url")

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

    // permission camera, write file, read file , and image
    private fun permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA,
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                dispatchCaptureImageIntent()
            } else if (grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                // load video or load images
            } else {
                showMessage(
                    this, "Failed", "Not All Permission Granted!",
                    MotionToast.TOAST_WARNING
                )
            }
        }
    }

    private fun getFilePath(type: Int, uri: Uri): String {
        lateinit var filePathColumn: Array<String>
        lateinit var filePath: String
        when (type) {
            VIDEO -> {
                filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
            }
            AUDIO -> {
                filePathColumn = arrayOf(MediaStore.Audio.Media.DATA)
            }
        }
        val cursor = contentResolver?.query(
            uri,
            filePathColumn, null, null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            filePath = cursor.getString(columnIndex)
            cursor.close()
        } else {
            filePath = uri.path.toString()
        }
        return filePath
    }

    override fun onResume() {
        super.onResume()
        if (videoPlayer?.isPlaying!!) {
            playVideo()
        }

        if (isPlayingVoice) {
            startAudioPlay()
        }
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

    private fun visibilityProgress(isUpload: Boolean, text: String = "") {
        with(binding) {
            if (isUpload) {
                pbUploadSoal.visibility = View.VISIBLE
                tvUploadSoal.visibility = View.GONE
            } else {
                pbUploadSoal.visibility = View.GONE
                tvUploadSoal.visibility = View.VISIBLE
                tvUploadSoal.text = text
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (type == REQUEST_EDIT_VIDEO or REQUEST_EDIT_IMAGE) {
            menuInflater.inflate(R.menu.delete, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.delete -> showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.delete_soal_title))
            .setMessage(getString(R.string.delete_soal_body))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                manageSoal(REQUEST_DELETE, dataSoal?.idSoal!!)
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun setToolbarTitle(toolbar: Toolbar, titleToolbar: String) {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = titleToolbar
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onProgressUpdate(percentage: Int) {
        binding.pbUploadSoal.progress = percentage
    }
}