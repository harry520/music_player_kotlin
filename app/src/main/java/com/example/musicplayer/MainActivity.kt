package com.example.musicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mp = MediaPlayer.create(this, R.raw.music)
        mp.isLooping = true
        mp.setVolume(.5f, .5f)
        totalTime = mp.duration
        // Volume Bar
        volumeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    var volumeNum = progress / 100.0f
                    mp.setVolume(volumeNum, volumeNum)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        // Position Bar
        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    mp.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        // Thread
        Thread(Runnable {
            while (mp != null) {
                var msg = Message()
                msg.what = mp.currentPosition
                handler.sendMessage(msg)
                Thread.sleep(1000)
            }
        }).start()
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what
            // Update positionBar
            positionBar.progress = currentPosition
            // Update Labels
            var elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = elapsedTime
            var remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        timeLabel = "$min:"
        if (sec < 10)
            timeLabel += "0"
        timeLabel += sec
        return timeLabel
    }

    fun playBtnClick(v: View) {
        // Stop
        if (mp.isPlaying) {
            mp.pause()
            playBtn.setBackgroundResource(R.drawable.play)
        } else {
            // Start
            mp.start()
            playBtn.setBackgroundResource(R.drawable.stop)
        }
    }

    override fun onStop() {
        mp.pause()
        playBtn.setBackgroundResource(R.drawable.play)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        mp.start()
        playBtn.setBackgroundResource(R.drawable.stop)
    }

}
