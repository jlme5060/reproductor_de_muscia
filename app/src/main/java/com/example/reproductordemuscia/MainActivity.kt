package com.example.reproductordemuscia

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.reproductordemuscia.R

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var songTitle: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var handler: Handler
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private var songList = mutableListOf<Int>()
    private var currentSongIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        nextButton = findViewById(R.id.nextButton)
        songTitle = findViewById(R.id.songTitle)
        seekBar = findViewById(R.id.seekBar)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        handler = Handler()

        // Cargar lista de canciones desde la carpeta raw
        loadSongs()

        // Inicializa el MediaPlayer con la primera canción
        initializeMediaPlayer()

        // Reproducir
        playButton.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                updateSeekBar()
            }
        }

        // Pausar
        pauseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }

        // Siguiente canción
        nextButton.setOnClickListener {
            if (currentSongIndex < songList.size - 1) {
                currentSongIndex++
            } else {
                currentSongIndex = 0
            }
            mediaPlayer.reset()
            initializeMediaPlayer()
            mediaPlayer.start()
            updateSeekBar()
        }

        // Buscar canción
        searchButton.setOnClickListener {
            val searchTerm = searchEditText.text.toString().lowercase()
            val foundIndex = songList.indexOfFirst {
                resources.getResourceEntryName(it).lowercase().contains(searchTerm)
            }
            if (foundIndex != -1) {
                currentSongIndex = foundIndex
                mediaPlayer.reset()
                initializeMediaPlayer()
                mediaPlayer.start()
                updateSeekBar()
            } else {
                songTitle.text = "Canción no encontrada"
            }
        }
    }

    private fun loadSongs() {
        val fields = R.raw::class.java.fields
        for (field in fields) {
            val resourceId = resources.getIdentifier(field.name, "raw", packageName)
            songList.add(resourceId)
        }
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, songList[currentSongIndex])
        songTitle.text = resources.getResourceEntryName(songList[currentSongIndex]) + ".mp3"
        seekBar.max = mediaPlayer.duration
    }

    private fun updateSeekBar() {
        seekBar.progress = mediaPlayer.currentPosition
        if (mediaPlayer.isPlaying) {
            handler.postDelayed({ updateSeekBar() }, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // Libera los recursos del reproductor
    }
}