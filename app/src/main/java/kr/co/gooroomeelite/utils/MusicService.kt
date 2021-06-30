//package kr.co.gooroomeelite.utils
//
//import android.app.Service
//import android.content.Intent
//import android.media.MediaPlayer
//import android.os.IBinder
//import androidx.appcompat.app.AppCompatActivity
//import kr.co.gooroomeelite.R
//
//class MusicService: Service() {
//    private var mediaPlayer: MediaPlayer? = null
//    var playlist = listOf<Int>()
//    var select : String? = null
//
//    override fun onBind(intent: Intent): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        mediaPlayer = MediaPlayer.create(this, playlist[0])
//        mediaPlayer = MediaPlayer.create(this, R.raw.serenity)
//        mediaPlayer!!.isLooping = true
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        mediaPlayer!!.start()
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer!!.stop()
//    }
//
//}