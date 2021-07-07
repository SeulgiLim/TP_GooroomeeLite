package kr.co.gooroomeelite.adapter

import android.media.MediaPlayer
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.MusicAdapter.medi.mediaplayer
import kr.co.gooroomeelite.databinding.ItemRecyclerviewMusicBinding
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


    //Item의 클릭 상태를 저장할 SparseBooleanarray 객체
    private val selectedItems = SparseBooleanArray()
    private var prePosition = -1

    inner class ViewHolder(private val binding : ItemRecyclerviewMusicBinding): RecyclerView.ViewHolder(binding.root){
        val tvmusic : TextView =itemView.findViewById(R.id.tv_music)
        val btnplayandstop : Button = itemView.findViewById(R.id.btn_playandstop)

        fun setView(item : MusicItem) {
            with(binding) {
                holderView = this@ViewHolder
            }
        }
        fun onClick(v: View) {
            if (selectedItems[layoutPosition]) {    //클릭시 닫힌다 -> 이벤트 동작한 포지션의 아이템뷰가 selectedItems에 추가
                selectedItems.delete(layoutPosition)
            } else {    //클릭시
                selectedItems.delete(prePosition)   //이전 포지션 아이템뷰 삭제
                selectedItems.put(layoutPosition, true)
            }
            if (prePosition != -1) notifyItemChanged(prePosition)
            notifyItemChanged(layoutPosition)
            prePosition = layoutPosition
        }

        //음악을 켜고 끄고, 이미지를 바꾸는 메소드
        fun changemusic(isPlaying:Boolean){
            val musicdata =musicList[position]

            if (mediaplayer!=null){
                mediaplayer?.release()
                mediaplayer = null
            }
            mediaplayer = MediaPlayer.create(owner,musicdata.music)

            if (isPlaying){
                musicdata.musiccheck = true
                mediaplayer?.start()
                mediaplayer?.isLooping = true
                btnplayandstop.setBackgroundResource(R.drawable.ic_stopmusic)
            }
            else{
                musicdata.musiccheck = false
                mediaplayer?.stop()
                Log.e("TEST","1")

                try {
                    Log.e("TEST","2")
                }
                catch (e:IOException){
                    e.printStackTrace()
                }
                btnplayandstop.setBackgroundResource(R.drawable.ic_playmusic)
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
            changemusic(selectedItems[position])
            tvmusic.text =musicdata.tvmusic
        }

    }

    override fun getItemCount(): Int {
        return musicList.size
    }
    object medi{
        var mediaplayer:MediaPlayer?=null
    }
}