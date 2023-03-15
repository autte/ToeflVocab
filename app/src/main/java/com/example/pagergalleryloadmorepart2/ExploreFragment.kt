package com.example.pagergalleryloadmorepart2

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable

import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.example.pagergalleryloadmorepart2.ExploreAdapter.OnExploreSelectedListner
import com.example.pagergalleryloadmorepart2.ExploreAdapter.OnLoadMoreListener
import com.example.pagergalleryloadmorepart2.WaveSideBarView.OnTouchLetterChangeListener
import java.util.*
import kotlin.collections.ArrayList


class ExploreFragment : Fragment(), OnExploreSelectedListner, OnLoadMoreListener,
    TextToSpeech.OnInitListener {
    @BindView(R.id.rvExplore)
    @JvmField
    var rvExplore: RecyclerView? = null
    @BindView(R.id.side_view)
    @JvmField
    var mSideBarView: WaveSideBarView? = null
    @BindView(R.id.edtSearch)
    @JvmField
    var edtSearch: EditText? = null
    var unbinder: Unbinder? = null
    var txts: TextToSpeech? = null
    val allEnglishString = context?.resources?.getStringArray(R.array.itemAllEng)
    var progressDialog: ProgressDialog? = null
    private var exploreAdapter: ExploreAdapter? = null
    private val explorelist = java.util.ArrayList<ExploreBean?>()
    private var sortedList=java.util.ArrayList<ExploreBean?>()
    private var per_page = 1
    var alphaString:String="Please enter alphanumeric characters only"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        unbinder = ButterKnife.bind(this, view)
        alphaString= context?.resources?.getString(R.string.pixabaykey) ?: "Please enter alphanumeric characters only"
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.reverseLayout = true
        rvExplore!!.layoutManager = linearLayoutManager
        ProgressDialogSetup()
        //设置回退键
        var actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        val colorDrawable = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorEditHintText))
        actionBar!!.setBackgroundDrawable(colorDrawable)
        actionBar.setStackedBackgroundDrawable(colorDrawable)

        //实现SIDEBAR排序ABCDEFG
        val decoration = PinnedHeaderDecoration()
        decoration.registerTypePinnedHeader(1) { parent, adapterPosition -> true }
        rvExplore!!.addItemDecoration(decoration)
        explorelist.sortBy{ it?.title }
        //读法语
        txts = TextToSpeech(activity, this)
        exploreAdapter = activity?.let { ExploreAdapter(it, explorelist, rvExplore!!, txts) }
        exploreAdapter!!.setOnExploreSelectedListner(this)
        exploreAdapter!!.setOnLoadMoreListener(this)
        rvExplore!!.adapter = exploreAdapter

        getExplore(per_page)

        //val titleList = this.resources.getStringArray(R.array.itemAllEng)
        //titleList.sortedArrayDescending()

        //设置sidebar
        mSideBarView!!.setOnTouchLetterChangeListener(object : OnTouchLetterChangeListener {
            override fun onLetterChange(letter: String?) {
                //找下一个LETTER
                val pos = exploreAdapter!!.getLetterPosition(letter!!)
                if (pos != -1) {
                    var posit =pos
                    //if(pos<=7){var posit:Int=pos}else{posit=pos-7}
                    rvExplore!!.scrollToPosition(posit)
                    //og.d(TAG," position is: " + (pos))
                    val mLayoutManager = rvExplore!!.layoutManager as LinearLayoutManager?
                    mLayoutManager!!.scrollToPositionWithOffset(posit, 0)
                }
            }
        })
        edtSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString())
            }
        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            //TextToSpeech txts = new TextToSpeech(getActivity(), this);
            val result = txts!!.setLanguage(Locale.US)
            val text = "AAA"

        }else{
            Log.d("tts","oninit Failed")
        }
    }
    override fun onDestroy() {
        if (txts != null) {
            txts!!.stop()
            txts!!.shutdown()
        }
        super.onDestroy()
    }
    private fun filter(text: String) {
        val filteredList = ArrayList<ExploreBean?>()
        for (item in explorelist) {
            if (item!!.title.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        if(filteredList.size>1){
            //Log.e("OnTxtC","filteredList size: "+filteredList.size)
            exploreAdapter!!.filterlist(filteredList)
        }
        else{
            Toast.makeText(context, "This string does not exist.", Toast.LENGTH_SHORT).show()
        }

    }


    fun ProgressDialogSetup() {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("please wait")
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setCancelable(false)
    }

    fun getExplore(per_page: Int) {
        //  if (per_page == 1) {
        Log.e("editSearch", "getExplore")
        explorelist.clear()
        //    String[] titleList =this.getResources().getStringArray(R.array.itemAllEng);
        val titleList = this.resources.getStringArray(R.array.itemAllEng)
        //titleList.sortDescending()
        var fullList=this.resources.getStringArray(R.array.itemAllWithAlphaEng)
        val waveSideBarList: Array<out String> =this.resources.getStringArray(R.array.waveSideBarLetters)
        var mergedList: Array<String> = titleList+ waveSideBarList
        var sortedList1= mergedList.sorted()
        //for (i in sortedList1.indices) {
        for(i in fullList.indices){
            val title = sortedList1[i]
            explorelist.add(ExploreBean(title))
        }
        rvExplore!!.adapter = exploreAdapter
        progressDialog!!.dismiss()
    }
    override fun setOnExploreSelatedListner(position: Int, dataBean: ExploreBean?) {
      //Aurora 先注释掉
          //((MainActivity) getActivity()).loadFragment(new ExploreDetailFragment());
/*          val newGalleryFragment =
              GalleryFragment.newInstance(dataBean?.title,position)
              loadFragment(newGalleryFragment)*/
          //  ((MainActivity) getActivity()).loadFragment(newsDetailsFragment);
      }
    private fun loadFragment(fragment: GalleryFragment) {
        val backStateName: String = GalleryFragment::class.java.name
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        //val ft: FragmentTransaction = fragmentManager.beginTransaction()
        val fragmentPopped: Boolean = fragmentManager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped) { //fragment not in back stack, create it.
            val ft = fragmentManager.beginTransaction()
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment, fragment)
            //ft.hide(this@ExploreFragment)
            //ft.addToBackStack(backStateName)
            ft.commit()
        }
    }

    override fun onLoadMore() {
        Log.e("hint", "Load More")
        if (!exploreAdapter!!.isLoading) {
            explorelist.add(null)
            //  exploreAdapter.notifyDataSetChanged();
            per_page++
            getExplore(per_page)
        }
    }
}





