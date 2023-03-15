package com.example.pagergalleryloadmorepart2

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_pager_photo.*

class PagerPhotoListAdapter:ListAdapter<PhotoItem,PagerPhotoViewHolder>(DiffCallback) {
    private lateinit var mContext: Context

    object DiffCallback:DiffUtil.ItemCallback<PhotoItem>(){
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerPhotoViewHolder {
        mContext = parent.context

        LayoutInflater.from(parent.context).inflate(R.layout.pager_photo_view,parent, false).apply {
            return PagerPhotoViewHolder(this)
        }
    }
    override fun onBindViewHolder(holder: PagerPhotoViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(getItem(position).previewUrl)
            .placeholder(R.drawable.photo_placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {   TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    Log.e(TAG,"Error!!")
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    Log.d(TAG, "OnResourceReady")
                    return false
                    //do something when picture already loaded
                }
            })
            .into(holder.itemView.pagerPhoto)
   /*     holder.meaning_toast?.setOnClickListener {
            var str111:String="A"+position
            Log.d("str111","str: "+str111)
            var strToast:String=getStringResourceByName(str111)
            //Log.d("str111",str111)
            Toast.makeText(mContext, strToast, Toast.LENGTH_LONG).show()
        }*/
    }
    private fun getStringResourceByName(aString: String): String {
        val packageName: String = mContext.packageName ?: "Null"
        val resId = mContext.resources.getIdentifier(aString, "string", packageName)
        return mContext.getString(resId)
        Log.d("str111","str strToast : "+mContext.getString(resId))
    }
}

class PagerPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
/*    var meaning_toast:ImageView
    init {
        meaning_toast=itemView.findViewById<View>(R.id.meaning_toast) as ImageView
    }*/
}