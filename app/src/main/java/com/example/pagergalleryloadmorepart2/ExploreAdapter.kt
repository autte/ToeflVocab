package com.example.pagergalleryloadmorepart2

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList


class ExploreAdapter(
    var context: Context,
    var exploreBeanList: ArrayList<ExploreBean?>,
    var mRecyclerView: RecyclerView,
    var tts: TextToSpeech? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    var onExploreSelectedListner: OnExploreSelectedListner? = null
    var row_index = -1
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    var isLoading = false
    var title ="A"
    var aPosition:Int = 0
    val itemAllString = context.resources.getStringArray(R.array.itemAllWithAlphaEng)
    var linlayout: LinearLayout? = null
    private val visibleThreshold = 5
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    val titleList = context.resources.getStringArray(R.array.itemAllEng)
    val waveSideBarList: Array<out String> =context.resources.getStringArray(R.array.waveSideBarLetters)
    var mergedList: Array<String> = titleList+ waveSideBarList
    lateinit var filteredStrList:ArrayList<String>
    var sortedList1= mergedList.sorted()
    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener?) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }
    interface OnLoadMoreListener {
        fun onLoadMore()
    }
    override fun getItemViewType(position: Int): Int {
        //return if (exploreBeanList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
        return if (itemAllString[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }
    @JvmName("setOnExploreSelectedListner1")
    fun setOnExploreSelectedListner(onExploreSelectedListner: OnExploreSelectedListner?) {
        this.onExploreSelectedListner = onExploreSelectedListner
    }
    interface OnExploreSelectedListner {
        fun setOnExploreSelatedListner(position: Int, dataBean: ExploreBean?)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_explore, parent, false)
            return MyViewholder(view)
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_loading_item, parent, false)
            return LoadingViewHolder(view)
        }
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_loading_item, parent, false)
        return LoadingViewHolder(view)
    }
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        if (holder is MyViewholder) {
            val myHolder = holder
            myHolder.tvNumber.setText(" $position")
            //var lent1: Int = itemAllString[position].length
            if (exploreBeanList.size > position) {
                myHolder.tvExploreName.text =
                    Html.fromHtml(exploreBeanList.get(position)?.getTitle())
            }
            //myHolder.tvExploreName.text =itemAllString[position]
            myHolder.linlayout.setOnClickListener {
                row_index = position
                notifyDataSetChanged()
            }
            if (row_index == position) {
               /*  myHolder.linlayout.setBackgroundColor(Color.BLUE)
                myHolder.tvExploreName.setTextColor(Color.parseColor("#ffffff"))*/

            } else {
                title = itemAllString[position]
                //title = exploreBeanList.get(position)?.getTitle().toString()
                myHolder.linlayout.setBackgroundColor(Color.parseColor("#ffffff"))
                myHolder.tvExploreName.setTextColor(Color.parseColor("#000000"))
            }
           // if (1 == itemAllString[position].length && ('Z' >= itemAllString[position][0] && itemAllString[position][0] >= 'A')) {
            if (waveSideBarList.contains(itemAllString[position]) ) {
                     Log.d(
                    "POSITION",
                    "itemA:" + itemAllString[position] + " len: " + itemAllString[position].length
                )
               myHolder.cdView!!.setCardBackgroundColor(Color.parseColor("#c0c0c0"))
                // myHolder.cdView!!.setCardBackgroundColor(Color.parseColor("#ABE0CD"))
                myHolder.playWord.visibility = View.GONE
                myHolder.wordMeaning.visibility = View.GONE
                myHolder.tvNumber.setVisibility(View.GONE)
            } else {
                if (itemAllString[position].length > 1) {
                    myHolder.cdView!!.setCardBackgroundColor(Color.parseColor("#ffffff"))
                    myHolder.playWord.visibility = View.VISIBLE
                    myHolder.wordMeaning.visibility = View.VISIBLE
                    myHolder.tvNumber.setVisibility(View.VISIBLE)
                }
                holder.itemView.setOnClickListener { // onExploreSelectedListner.setOnExploreSelatedListner(position, exploreBeanList.get(position));
                    title = exploreBeanList.get(position)?.getTitle().toString()
                    aPosition = titleList.indexOf(title)
                    Bundle().apply {
                        putString("CURRENT_KEY", title)
                        //Log.d(TAG,"CURRENT_KEY from index is: " + title+"  position is: "+aPosition                        )
                        putInt("ACCESS_POSITION", aPosition)
                        val bundle = Bundle()
                        holder.itemView.findNavController()
                            .navigate(R.id.action_indexFragment_to_GalleryFragment, this)
                    }
                }
            }
            myHolder.playWord.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts?.speak(
                        //itemAllString.get(position),
                        exploreBeanList.get(position)?.getTitle(),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        exploreBeanList.get(position)?.getTitle()
                        //itemAllString[position]
                        //itemAllString.get(position)
                    )
                } else {
                    tts?.speak(
                        exploreBeanList.get(position)?.getTitle(),
                        //itemAllString[position],
                        //itemAllString.get(position),
                        TextToSpeech.QUEUE_FLUSH,
                        null
                    )
                }
            }
        }
            else {
                val loadingViewHolder = holder as LoadingViewHolder?
                loadingViewHolder!!.progressBar.isIndeterminate = true
            }

    override fun getItemCount(): Int {
        return exploreBeanList.size
        //return itemAllString.size
    }
    fun getLetterPosition(letter: String): Int {
        for (i in 0..exploreBeanList.size-1){
            if(exploreBeanList.get(i)?.getTitle().toString().equals(letter) == true){
                var j=exploreBeanList.size-1-i
                //Log.d(TAG," letter: " + letter+" s position is: " + (i).toString())
                return i;
            }
        }
        return -1;
    }
    inner class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var linlayout: LinearLayout
        var tvExploreName: TextView
        var tvNumber:TextView
        var playWord: ImageView
        var wordMeaning: ImageView
        var cdView: CardView? = null
        init {
            tvExploreName = itemView.findViewById<View>(R.id.tvExploreName) as TextView
            // linlayout = itemView.findViewById<View>(R.id.linlayout) as LinearLayout
            linlayout= itemView.findViewById<View>(R.id.linlayout) as LinearLayout
            tvNumber=itemView.findViewById<View>(R.id.textViewNumber) as TextView
            playWord = itemView.findViewById<View>(R.id.playWord) as ImageView
            // aSwitch = (Switch) itemView.findViewById(R.id.switchChineseInvisible);
            cdView = itemView.findViewById<View>(R.id.cardView) as CardView
            wordMeaning=itemView.findViewById<View>(R.id.wordMeaning) as ImageView
        }
    }
    internal class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById<View>(R.id.progressBar1) as ProgressBar
        }
    }

    fun setLoaded() {
        isLoading = false
    }



    init {
        val inflater = LayoutInflater.from(context)
//        lateinit var galleryViewModel:
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        val linearLayoutManager = mRecyclerView.layoutManager as LinearLayoutManager?
        if (linearLayoutManager != null) {
            linearLayoutManager.setReverseLayout(false)
            linearLayoutManager.setStackFromEnd(false);
        };

        /* val linearLayoutManager: RecyclerView.LayoutManager =
             LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)*/
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager!!.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })
    }
    fun filterlist(filteredList: java.util.ArrayList<ExploreBean?>) {
        exploreBeanList = filteredList
        notifyDataSetChanged()
    }
}