package ca.ggolda.guessayear.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.widget.Toast
import ca.ggolda.guessayear.R


class AudioService : Service() {

    private var audioPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        initAudioPlayerIfNeeded()

        //audioPlayer?.reset()
        //val fileName = "android.resource://" + this.packageName + "/raw/correct"
        //audioPlayer?.setDataSource(applicationContext, Uri.parse(fileName))
        audioPlayer?.start()

        return START_STICKY
    }

    private fun initAudioPlayerIfNeeded() {
        if (audioPlayer == null) {
            audioPlayer = MediaPlayer()

            val mp = MediaPlayer.create(this, R.raw.incorrect)
            //mp.start()

            audioPlayer = mp

            Toast.makeText(this, "audio service starting", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        Toast.makeText(this, "audio service stopped", Toast.LENGTH_SHORT).show()

        audioPlayer?.stop()

    }
}