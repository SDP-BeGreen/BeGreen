package com.github.sdp_begreen.begreen.retrofit

//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.github.sdp_begreen.begreen.R
//import com.jakewharton.picasso.OkHttp3Downloader
//import com.squareup.picasso.Picasso
//
//
//class CustomAdapter(context: Context, dataList: List<RetroPhoto>) :
//    RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {
//    private val dataList: List<RetroPhoto>
//    private val context: Context
//
//    init {
//        this.context = context
//        this.dataList = dataList
//    }
//
//    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val mView: View
//        var txtTitle: TextView
//        private val coverImage: ImageView
//
//        init {
//            mView = itemView
//            txtTitle = mView.findViewById(R.id.title)
//            coverImage = mView.findViewById(R.id.coverImage)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val view: View = layoutInflater.inflate(R.layout.custom_row, parent, false)
//        return CustomViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        holder.txtTitle.text = dataList[position].title
//        val builder = Picasso.Builder(context)
//        builder.downloader(OkHttp3Downloader(context))
//        builder.build().load(dataList[position].thumbnailUrl)
//            .placeholder(R.drawable.ic_launcher_background)
//            .error(R.drawable.ic_launcher_background)
//            .into(holder.coverImage)
//    }
//
//    override fun getItemCount(): Int {
//        return dataList.size
//    }
//}