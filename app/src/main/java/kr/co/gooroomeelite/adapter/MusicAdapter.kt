package kr.co.gooroomeelite.adapter

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.views.mypage.BottomSheetFragment
import kr.co.gooroomeelite.views.mypage.MusicItem

/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-05
 * @desc
 */
class MusicAdapter(private val owner : AppCompatActivity,
                   private val musicList:MutableList<MusicItem>) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>(){

    val mediaPlayer : MediaPlayer? = null
    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val tvmusic : TextView =itemView.findViewById(R.id.tv_music)
        val btnplayandstop : Button = itemView.findViewById(R.id.btn_playandstop)
        val musiccheck : Boolean = false

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_music,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicAdapter.ViewHolder, position: Int) {
        val musicdata =musicList[position]
        with(holder){
            tvmusic.text =musicdata.tvmusic
            btnplayandstop.setOnClickListener {
                val mediaPlayer = MediaPlayer.create(owner,musicdata.music)
                Log.e("TEST","1")
                if (mediaPlayer.isPlaying){

                    Log.e("TEST","2")
                    mediaPlayer.stop()
                    Log.e("TEST","3")
                    mediaPlayer.reset()
                    Log.e("TEST","4")
                }
                else{
                    musiccheck == true
                    Log.e("TEST","5")
                    mediaPlayer.isLooping = true
                    mediaPlayer.start()
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}