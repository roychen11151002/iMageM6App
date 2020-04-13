package com.example.imagebtapp_v001b001

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BtListAdapter(val items: ArrayList<String>) : RecyclerView.Adapter<BtListAdapter.ViewHolder>() {
    lateinit var clickListener: OnItemClickListener
    lateinit var longClickListener: OnItemLongClickListener
    val ImgTypeId = intArrayOf(R.drawable.android_image_1, R.drawable.android_image_2,
        R.drawable.android_image_3, R.drawable.android_image_4, R.drawable.android_image_5,
        R.drawable.android_image_6, R.drawable.android_image_7, R.drawable.android_image_8)

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(listen: OnItemClickListener) {
        this.clickListener = listen
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int): Boolean
    }

    fun setOnItemLongClickListener(listen: OnItemLongClickListener): Boolean {
        this.longClickListener = listen
        return true
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ImgDevType: ImageView
        var TxvDevName: TextView
        var TXVDevBdaddr: TextView

        init {
            ImgDevType = itemView.findViewById(R.id.imgDevType)
            TxvDevName = itemView.findViewById(R.id.txvDevName)
            TXVDevBdaddr = itemView.findViewById(R.id.txvDevBdaddr)

            itemView.setOnClickListener(View.OnClickListener {
                clickListener.onItemClick(it, adapterPosition)
            })

            itemView.setOnLongClickListener(View.OnLongClickListener {
                longClickListener.onItemLongClick(it, adapterPosition)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.bdaddr_adapter, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.TxvDevName.text = items[position].removeRange(items[position].lastIndexOf(" + "), items[position].lastIndex + 1)
        holder.TXVDevBdaddr.text = items[position].removeRange(0, items[position].lastIndexOf(" + ") + 3)
        holder.ImgDevType.setImageResource(ImgTypeId[position % ImgTypeId.size])
    }
}
