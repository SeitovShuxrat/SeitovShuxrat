package com.example.landtech.presentation.ui.audio_record

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.landtech.R
import com.example.landtech.databinding.FragmentAudioRecordBinding
import com.example.landtech.domain.utils.AudioPlayer
import com.example.landtech.domain.utils.AudioRecorder
import java.io.File
import java.lang.Exception

class AudioRecordFragment : Fragment() {

    private lateinit var binding: FragmentAudioRecordBinding
    private lateinit var fileName: String
    private val viewModel: AudioRecordViewModel by viewModels()
    private val args: AudioRecordFragmentArgs by navArgs()
    private var isRecording = false
    private var isPlaying = false

    private val audioRecorder by lazy {
        AudioRecorder(requireActivity().applicationContext)
    }

    private val audioPlayer by lazy {
        AudioPlayer(requireActivity().applicationContext)
    }

    private var audioFile: File? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        startRecording()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setOrderId(args.orderId)

        val orderId = viewModel.orderId
        fileName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            "${requireActivity().getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)?.absolutePath}/land_${orderId}.mp3"
        } else {
            "${requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath}/land_${orderId}.mp3"
        }

        val file = File(fileName)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            if (file.exists()) {
                binding.fileNameText.text = "Файл записи ${fileName}"
            }

            recordBtn.setOnClickListener {
                if (isRecording) {
                    stopRecording()
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startRecording()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }

            playBtn.setOnClickListener {
                if (file.exists()) {
                    if (isPlaying)
                        stopPlaying()
                    else
                        startPlaying()
                }
            }

            deleteBtn.setOnClickListener {
                deleteRecording()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (isRecording)
            stopRecording()

        if (isPlaying)
            stopPlaying()
    }

    private fun startRecording() {
        isRecording = true
        binding.recordBtn.setImageResource(R.drawable.record_btn_recording)

        binding.recordTimer.base = SystemClock.elapsedRealtime()
        binding.recordTimer.start()

        File(fileName).also {
            audioRecorder.start(it)
            audioFile = it
        }
    }

    private fun stopRecording() {
        isRecording = false

        binding.recordBtn.setImageResource(R.drawable.record_btn_stopped)
        binding.recordTimer.stop()
        binding.fileNameText.text = "Запись сохранен ${fileName}"
        audioRecorder.stop()
    }

    private fun startPlaying() {
        try {
            audioPlayer.playFile(File(fileName)) {
                isPlaying = false
                binding.playBtn.setImageResource(R.drawable.list_play_btn)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.playBtn.setImageResource(R.drawable.list_pause_btn)
        isPlaying = true
    }

    private fun stopPlaying() {
        audioPlayer.stop()
        isPlaying = false

        binding.playBtn.setImageResource(R.drawable.list_play_btn)
    }

    private fun deleteRecording() {
        val file = File(fileName)

        if (file.exists()) {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Удаление")
                setMessage("Вы действительно хотите удалить запись?")
                setPositiveButton("Да") { _, _ ->
                    file.delete()
                    binding.fileNameText.text = "Нажмите на кнопки ниже \n для записи или воспроизведения аудио файла"
                }
                setNegativeButton("Нет") { _, _ -> }
                show()
            }
        } else {
            Toast.makeText(requireContext(), "Нет записи!", Toast.LENGTH_SHORT).show()
        }
    }
}