package com.elifbesik.proje451

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListeRecyclerAdapter(val kitapListesi:ArrayList<String>,val idListesi:ArrayList<Int> ):RecyclerView.Adapter<ListeRecyclerAdapter.KitapHolder>() {
    class KitapHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapHolder {
        val inflater =LayoutInflater.from(parent.context)
        val view =inflater.inflate(R.layout.recycler_row,parent,false)
        return KitapHolder(view)
    }

    override fun onBindViewHolder(holder: KitapHolder, position: Int) {
        holder.itemView.itemTitle.text=kitapListesi[position]
        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return  kitapListesi.size
    }

}