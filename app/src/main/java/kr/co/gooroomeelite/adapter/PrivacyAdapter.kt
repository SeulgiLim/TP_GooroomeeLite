package kr.co.gooroomeelite.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kr.co.gooroomeelite.databinding.ItemRecyclerviewOpensourceBinding
import kr.co.gooroomeelite.databinding.ItemRecyclerviewPrivacyBinding
import kr.co.gooroomeelite.databinding.ItemRecyclerviewServiceBinding
import kr.co.gooroomeelite.views.mypage.OpenSourceItem
import kr.co.gooroomeelite.views.mypage.PrivacyItem
import kr.co.gooroomeelite.views.mypage.ServiceItem

/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-08
 * @desc
 */
class PrivacyAdapter(private val privacyList : MutableList<PrivacyItem>) :
    RecyclerView.Adapter<PrivacyAdapter.ViewHolder>(){

        inner class ViewHolder(private val binding : ItemRecyclerviewPrivacyBinding):RecyclerView.ViewHolder(binding.root){
            val title : TextView = binding.tvPrivacyTitle
            val content : TextView = binding.tvPrivacyContent
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerviewPrivacyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val privacyItem = privacyList[position]
        with(holder){
            title.text = privacyItem.title
            content.text = privacyItem.content
        }
    }

    override fun getItemCount(): Int {
        return privacyList.size
    }
}


class ServiceAdapter(private val serviceList : MutableList<ServiceItem>) :
    RecyclerView.Adapter<ServiceAdapter.ViewHolder>(){

    inner class ViewHolder(private val binding : ItemRecyclerviewServiceBinding):RecyclerView.ViewHolder(binding.root){
        val title : TextView = binding.tvServiceTitle
        val content : TextView = binding.tvServiceContent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerviewServiceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val serviceItem = serviceList[position]
        with(holder){
            title.text = serviceItem.title
            content.text = serviceItem.content
        }
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }
}


class OpenSourceAdapter(private val openSourceList : MutableList<OpenSourceItem>) :
    RecyclerView.Adapter<OpenSourceAdapter.ViewHolder>(){

    inner class ViewHolder(private val binding : ItemRecyclerviewOpensourceBinding):RecyclerView.ViewHolder(binding.root){
        val title : TextView = binding.tvOpensourceTitle
        val content : TextView = binding.tvOpensourceContent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerviewOpensourceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val openSourceItem = openSourceList[position]
        with(holder){
            title.text = openSourceItem.title
            content.text = openSourceItem.content
        }
    }

    override fun getItemCount(): Int {
        return openSourceList.size
    }
}