package kr.co.gooroomeelite.adapter

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.selects.select
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ItemRecyclerviewMusicBinding
import kr.co.gooroomeelite.views.mypage.BottomSheetFragment
import kr.co.gooroomeelite.views.mypage.MusicItem
import java.io.IOException

/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-05
 * @desc
 */
class MusicAdapter(private val owner : AppCompatActivity,
                   private val musicList:MutableList<MusicItem>) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>(){

    inner class ViewHolder(private val binding : ItemRecyclerviewMusicBinding): RecyclerView.ViewHolder(binding.root){
        val tvmusic : TextView =itemView.findViewById(R.id.tv_music)
        val btnplayandstop : Button = itemView.findViewById(R.id.btn_playandstop)

        fun setView(item : MusicItem) {
            with(binding) {
                holderView = this@ViewHolder
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        val binding = ItemRecyclerviewMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicAdapter.ViewHolder, position: Int) {
        val musicdata =musicList[position]
        val music = MediaPlayer.create(owner,musicdata.music)
        with(holder){
            setView(musicdata)
            tvmusic.text =musicdata.tvmusic
            btnplayandstop.setOnClickListener {
                if (music.isPlaying){
                    music.stop()
                    try {
                        music.prepare()
                    }
                    catch (e:IOException){
                        e.printStackTrace()
                    }
                    btnplayandstop.setBackgroundResource(R.drawable.ic_playmusic)
                }
                else{
                    music.start()
                    music.isLooping = true
                    btnplayandstop.setBackgroundResource(R.drawable.ic_stopmusic)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}