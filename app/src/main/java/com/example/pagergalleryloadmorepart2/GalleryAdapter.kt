package com.example.pagergalleryloadmorepart2

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*
import android.util.Log

class GalleryAdapter(val galleryViewModel: GalleryViewModel)
    : ListAdapter<PhotoItem, MyViewHolder>(DIFFCALLBACK) {
    companion object {
        const val NORMAL_VIEW_TYPE = 0
        const val FOOTER_VIEW_TYPE = 1
    }
    var footerViewStatus = DATA_STATUS_CAN_LOAD_MORE
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder: MyViewHolder
        if (viewType == NORMAL_VIEW_TYPE) {
            holder = MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.gallery_cell,
                    parent,
                    false
                )
            )
            holder.itemView.setOnClickListener {
                //Log.d(TAG, "From Gallary to Photo FRAGMENT CURRENT_KEY: "+galleryViewModel.currentKey)
                Bundle().apply {
                    putParcelableArrayList("PHOTO_LIST", ArrayList(currentList))
                    putInt("PHOTO_POSITION", holder.adapterPosition)
                    putString("CURRENT_KEY",galleryViewModel.currentKey)
                    val currentKey = galleryViewModel.currentKey
                 Log.d("currentKey", "ONCLICK KEY: "+galleryViewModel.currentKey+" GETcurrentk: "+galleryViewModel.getCurrentKey())
                    val bundle = Bundle()
                    if (holder.itemView.findNavController().currentDestination?.id
                        == R.id.galleryFragment) {
                        holder.itemView.findNavController().
                            navigate(R.id.action_galleryFragment_to_pagerPhotoFragment,this)
                    }
                }
            }
        } else {
            holder = MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.gallery_footer,
                    parent,
                    false
                ).also {
                    (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
                    it.setOnClickListener {itemView->
                        itemView.progressBar.visibility = View.VISIBLE
                        itemView.textView.text = "Loading..."
                        galleryViewModel.fetchData()
                        //Log.d("FFF", "FFF: From Gallary fetch data")

                    }
                }
            )
        }
        return holder
    }
    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) FOOTER_VIEW_TYPE else NORMAL_VIEW_TYPE
    }
    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == itemCount - 1) {
            with(holder.itemView) {
                when(footerViewStatus) {
                    DATA_STATUS_CAN_LOAD_MORE -> {
                        progressBar.visibility = View.VISIBLE
                        textView.text = "Loading..."
                        isClickable = false
                    }
                    DATA_STATUS_NO_MORE -> {
                        progressBar.visibility = View.GONE
                        //textView.text = "Please pull to refresh."

                        textView.text=this.resources.getString(R.string.PullToRefresh)
                        isClickable = false
                    }
                    DATA_STATUS_NETWORK_ERROR -> {
                        progressBar.visibility = View.GONE
                        textView.text = "Network error, please try for another time."
                        isClickable = true
                    }
                }
            }
            return
        }
        val photoItem = getItem(position)
        with(holder.itemView) {
            shimmerLayoutCell.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            //textViewUser.text = photoItem.photoUser
            textViewUser.text = galleryViewModel.getCurrentKey()
            //Log.d("currentKey","GAdpt .txt"+galleryViewModel.getCurrentKey())
            val typeface = Typeface.createFromAsset(
                context?.getAssets(),
                "fonts/AslinaBold-2.otf"
            ) // create a typeface from the raw ttf
            textViewUser.setTypeface(typeface) // apply the typeface to the textview

          /*  textViewLikes.text = photoItem.photoLikes.toString()
            textViewFavorites.text = photoItem.photoFavorites.toString()*/
            imageView.layoutParams.height = photoItem.photoHeight
        }
        holder.itemView.shimmerLayoutCell.apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        Glide.with(holder.itemView)
            .load(getItem(position).previewUrl)
            .placeholder(R.drawable.photo_placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { holder.itemView.shimmerLayoutCell?.stopShimmerAnimation() }
                }
            })
            .into(holder.itemView.imageView)
    }
    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }
        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }
}
class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)